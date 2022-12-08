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
    private final MailManager mailManager;
    public ServerConnection(Socket clientSocket, MailManager mailManager){
        this.mailManager = mailManager;
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

            ServerMain.addLog("Client connected: " + serverRequest.getClientMail() + " " + serverRequest.getRequestType());
            chooseResponse(serverRequest);

            if(!clientSocket.isClosed()){
                clientSocket.close();
            }
        } catch (IOException e) {
            ServerMain.addLog(e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("ERR 2");
            throw new RuntimeException(e);
        }
    }

    private void chooseResponse(ServerRequest request) throws IOException, ClassNotFoundException {
        switch (request.getRequestType()){
            case AUTHENTICATE:
                ServerMain.addLog("AUTHENTICATE received");
                authenticateUser(request.getClientMail());
                ServerMain.addLog("User authenticated");
                break;
            case SEND_EMAIL:
                ServerMain.addLog("SEND_EMAIL received");
                sendEmail(request);
                ServerMain.addLog("OK sent");
                break;
            case RCV_INBOX_EMAILS:
                ServerMain.addLog("RCV_INBOX_EMAILS received");
                sendResponse(mailManager.getInbox(request.getClientMail()));
                ServerMain.addLog("Email inbox list sent");
                break;
            case RCV_SENT_EMAILS:
                ServerMain.addLog("RCV_SENT_EMAILS received");
                sendResponse(mailManager.getSent(request.getClientMail()));
                ServerMain.addLog("Email sent list sent");
                break;
            case RMV_EMAIL:
                ServerMain.addLog("RMV_EMAIL received");
                removeEmail(request);
                ServerMain.addLog("Email removed");
                break;
            default:
                ServerMain.addLog("Bad request received");
                sendResponse(new ServerResponse(request.getClientMail(), ServerResponse.Type.BAD_REQUEST));
                break;
        }
    }

    private void authenticateUser(String user) throws IOException {
        if(mailManager.authenticateUser(user)){
            sendResponse(new ServerResponse(user, ServerResponse.Type.OK));
        }else {
            sendResponse(new ServerResponse(user, ServerResponse.Type.ADDRESS_UNKNOWN));
        }
    }

    private void sendEmail(ServerRequest request) throws IOException, ClassNotFoundException {
        sendResponse(new ServerResponse(request.getClientMail(), ServerResponse.Type.OK));

        if(mailManager.sendMail(receiveEmail(request))){
            sendResponse(new ServerResponse(request.getClientMail(), ServerResponse.Type.SENT));
        }else{
            sendResponse(new ServerResponse(request.getClientMail(), ServerResponse.Type.ADDRESS_UNKNOWN));
        }
    }

    private void removeEmail(ServerRequest request) throws IOException, ClassNotFoundException {
        sendResponse(new ServerResponse(request.getClientMail(), ServerResponse.Type.OK));

        mailManager.removeMail(receiveEmail(request), request.getClientMail());
        sendResponse(new ServerResponse(request.getClientMail(), ServerResponse.Type.RMVD));
    }

    private Email receiveEmail(ServerRequest request) throws IOException, ClassNotFoundException {
        ObjectInputStream inRemove =  new ObjectInputStream(new DataInputStream(clientSocket.getInputStream()));
        Object oRemove = inRemove.readObject();

        if(oRemove.getClass() != Email.class){
            sendResponse(new ServerResponse(request.getClientMail(), ServerResponse.Type.NOT_A_EMAIL));
        }
        return (Email) oRemove;
    }

    private void sendResponse(Serializable response) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(new DataOutputStream(clientSocket.getOutputStream()));
        out.writeObject(response);
    }
}
