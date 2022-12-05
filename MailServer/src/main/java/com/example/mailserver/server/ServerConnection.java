package com.example.mailserver.server;

import com.example.mailserver.ServerMain;
import com.example.mailserver.server.datamanager.MailManager;
import org.example.Email;
import org.example.ServerRequest;
import org.example.ServerResponse;

import java.io.*;
import java.net.Socket;

public class ServerConnection implements Runnable {
    private final Socket clientSocket;
    private final MailManager mailManager = new MailManager(this);
    public ServerConnection(Socket clientSocket){
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            ObjectInputStream in =  new ObjectInputStream(new DataInputStream(clientSocket.getInputStream()));
            Object o = in.readObject();

            if(o.getClass() != ServerRequest.class){
                sendResponse(new ServerResponse(null, ServerResponse.Type.BAD_REQUEST));
            }

            ServerRequest serverRequest = (ServerRequest) o;

            ServerMain.addLog("New connected client: " + serverRequest.getClientMail() + " " + serverRequest.getRequestType());

            chooseResponse(serverRequest);

            if(!clientSocket.isClosed()){
                clientSocket.close();
            }
        } catch (IOException e) {
            ServerMain.addLog(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void chooseResponse(ServerRequest request) throws IOException, ClassNotFoundException {
        switch (request.getRequestType()){
            case SEND_EMAIL:
                ServerMain.addLog("SEND_EMAIL received");

                sendResponse(new ServerResponse(request.getClientMail(), ServerResponse.Type.OK));
                ObjectInputStream in =  new ObjectInputStream(new DataInputStream(clientSocket.getInputStream()));
                Object o = in.readObject();

                if(o.getClass() != Email.class){
                    sendResponse(new ServerResponse(request.getClientMail(), ServerResponse.Type.NOT_AN_EMAIL));
                }

                Email email = (Email) o;
                mailManager.sendMail(email);
                sendResponse(new ServerResponse(request.getClientMail(), ServerResponse.Type.SENT));

                //clientSocket.close();

                ServerMain.addLog("OK sent");
                break;
            case RCV_LIST_EMAILS:
                ServerMain.addLog("RCV_LIST_EMAILS received");

                mailManager.getInbox(request.getClientMail());
                sendResponse(mailManager.getInbox(request.getClientMail()));
                //clientSocket.close();
                ServerMain.addLog("Email list sent");
                break;
        }
    }

    public void sendResponse(Serializable response) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(new DataOutputStream(clientSocket.getOutputStream()));
        out.writeObject(response);
    }
}
