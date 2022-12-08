package com.example.java_mailclient.model;

import com.example.java_mailclient.MailApplication;
import org.example.Email;
import org.example.ServerRequest;
import org.example.ServerResponse;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientConnection {
    Socket s = null;

    public boolean sendEmail(Email email) {
        ServerRequest serverRequest = new ServerRequest(email.getSender(), ServerRequest.Type.SEND_EMAIL);
        try {
            Object o = contactServer(serverRequest);
            if (o.getClass() != ServerResponse.class || ((ServerResponse) o).getResponseType() != ServerResponse.Type.OK) {
                return false;
            }

            o = sendEmailToServer(email);
            if (o.getClass() != ServerResponse.class || ((ServerResponse)o).getResponseType() != ServerResponse.Type.SENT) {
                return false;
            }
        } catch (IOException e) {
            return false;
        } finally {
            closeConnection();
        }
        return true;
    }

    public boolean deleteMail(Email email){
        ServerRequest serverRequest = new ServerRequest(MailApplication.getCurrUser(), ServerRequest.Type.RMV_EMAIL);
        try {
            Object o = contactServer(serverRequest);
            if (o.getClass() != ServerResponse.class || ((ServerResponse) o).getResponseType() != ServerResponse.Type.OK) {
                return false;
            }

            o = sendEmailToServer(email);
            if (o.getClass() != ServerResponse.class || ((ServerResponse)o).getResponseType() != ServerResponse.Type.RMVD) {
                return false;
            }
        } catch (IOException e) {
            return false;
        } finally {
            closeConnection();
        }

        return true;
    }

    public ArrayList<Email> getInbox(String mailAddress){
        ArrayList<Email> inbox;
        ServerRequest serverRequest = new ServerRequest(mailAddress, ServerRequest.Type.RCV_INBOX_EMAILS);
        inbox = getListFromServer(serverRequest);
        closeConnection();
        return inbox;
    }

    public ArrayList<Email> getSent(String mailAddress){
        ArrayList<Email> sent;
        ServerRequest serverRequest = new ServerRequest(mailAddress, ServerRequest.Type.RCV_SENT_EMAILS);
        sent = getListFromServer(serverRequest);
        closeConnection();
        return sent;
    }

    /**
     * ask the server if a user can be authenticated
     * @param user to be authenticated
     * @return response of the server
     */
    public ServerResponse userExist(String user){
        ServerRequest serverRequest = new ServerRequest(user, ServerRequest.Type.AUTHENTICATE);
        try {
            Object o = contactServer(serverRequest);

            if (o.getClass() != ServerResponse.class) {
                return null;
            }
            return (ServerResponse) o;
        } catch (IOException e) {
            return new ServerResponse(serverRequest.getClientMail(), ServerResponse.Type.SERVER_OFFLINE);
        } finally {
            closeConnection();
        }
    }

    /**
     * retrieve a list sent by the server, the list can be the inbox or the mail sent
     * @param serverRequest request to send to the server, saying if the response should be inbox list or sent list
     * @return the list
     */
    private ArrayList<Email> getListFromServer(ServerRequest serverRequest){
        try {
            Object o = contactServer(serverRequest);

            if (o.getClass() == ArrayList.class) {
                return (ArrayList<Email>) o;
            }
            return null;
        } catch (IOException e) {
            return null;
        } finally {
            closeConnection();
        }
    }

    /**
     * send a request to the server
     * @param request to send to the server
     * @return the response from the server
     * @throws IOException thrown by output/input streams
     */
    private Object contactServer(ServerRequest request) throws IOException {
        s = new Socket("127.0.0.1", 8189);
        s.setSoTimeout(2000);

        ObjectOutputStream out = new ObjectOutputStream(new DataOutputStream(s.getOutputStream()));

        out.writeObject(request);
        ObjectInputStream in = new ObjectInputStream(new DataInputStream(s.getInputStream()));

        try {
            return in.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * send an Email obj to the server
     * @param email email to send
     * @return the object received as a response from the server
     * @throws IOException thrown by output/input streams
     */
    private Object sendEmailToServer(Email email) throws IOException {
        Object o;
        ObjectOutputStream out = new ObjectOutputStream(new DataOutputStream(s.getOutputStream()));
        out.writeObject(email);

        ObjectInputStream in = new ObjectInputStream(new DataInputStream(s.getInputStream()));
        try {
            o = in.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return o;
    }

    /**
     * close the socket if it's open
     */
    private void closeConnection(){
        try {
            if(s != null && !s.isClosed()){
                s.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
