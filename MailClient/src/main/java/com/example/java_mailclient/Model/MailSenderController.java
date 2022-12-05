package com.example.java_mailclient.Model;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.example.Email;
import org.example.ServerRequest;
import org.example.ServerResponse;

import java.io.*;
import java.net.Socket;

public class MailSenderController {
    @FXML
    private TextArea mailBody;

    @FXML
    private TextField mailFrom;
    @FXML
    private TextField mailTo;
    @FXML
    private TextField mailSubject;

    private MailController mailController;

    Socket s = null;

//    public void setMailText(String s){
//        mailBody.setText(s);
//    }

    @FXML
    public void initialize(){
        clearText();
    }

    public void setMailController(MailController mailController){
        this.mailController = mailController;
    }

    @FXML
    public void onBackClick(){
        mailController.viewEmails();
    }

    @FXML
    public void onSendClick(){
        if(!checkFields()){
            Alert alert = new Alert(Alert.AlertType.ERROR, "An error as occurred, the mail as not been sent", ButtonType.CANCEL);
            alert.showAndWait();
            return;
        }


        String myMail = mailFrom.getText();
        ServerRequest serverRequest = new ServerRequest(myMail, ServerRequest.Type.SEND_EMAIL);
        try {
            s = new Socket("127.0.0.1", 8189);

            ObjectOutputStream out = new ObjectOutputStream(new DataOutputStream(s.getOutputStream()));

            out.writeObject(serverRequest);
            ObjectInputStream in =  new ObjectInputStream(new DataInputStream(s.getInputStream()));
            Object o = in.readObject();

            ServerResponse serverResponse = (ServerResponse) o;
            if(o.getClass() == serverResponse.getClass() && ((ServerResponse) o).getResponseType() == ServerResponse.Type.OK){ //TODO: remove first condition (?)
                out = new ObjectOutputStream(new DataOutputStream(s.getOutputStream()));
                out.writeObject(new Email(mailFrom.getText(), mailTo.getText(), mailSubject.getText(), mailBody.getText()));
            }

            in =  new ObjectInputStream(new DataInputStream(s.getInputStream()));
            o = in.readObject();
            serverResponse = (ServerResponse) o;
            if(!(o.getClass() == serverResponse.getClass()) || !(((ServerResponse) o).getResponseType() == ServerResponse.Type.SENT)){ //TODO: remove first condition (?)
                Alert alert = new Alert(Alert.AlertType.ERROR, "An error as occurred, the mail as not been sent", ButtonType.CANCEL);
                alert.showAndWait();
            }

        } catch (IOException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection();
        }

//        if(o.getClass() != ServerResponse.class){
//
//        }
    }


    private boolean checkFields(){
        return mailFrom.getText().compareTo("") != 0 && mailTo.getText().compareTo("") != 0 && mailBody.getText().compareTo("") != 0
                && Email.emailVerifier(mailFrom.getText()) && Email.emailVerifier(mailTo.getText());
    }


    private void closeConnection(){
        try {
            if(!s.isClosed()){
                s.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void clearText(){
        mailFrom.setText("");
        mailTo.setText("");
        mailSubject.setText("");
        mailBody.setText("");
    }
}
