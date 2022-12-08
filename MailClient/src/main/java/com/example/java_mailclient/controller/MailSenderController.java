package com.example.java_mailclient.controller;

import com.example.java_mailclient.MailApplication;
import com.example.java_mailclient.model.ClientConnection;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import org.example.Email;

import java.util.ArrayList;
import java.util.Collections;

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
    ClientConnection clientConnection;

    @FXML
    public void initialize(){
        mailFrom.setText(MailApplication.getCurrUser());
        clientConnection = new ClientConnection();
    }

    public void setMailController(MailController mailController){
        this.mailController = mailController;
    }

    public void setClientConnection(ClientConnection clientConnection){
        this.clientConnection = clientConnection;
    }

    @FXML
    public void onBackClick(){
        mailController.viewEmails();
    }

    @FXML
    public void onSendClick(){
        String[] rs = mailTo.getText().split(" ");
        ArrayList<String> receivers = new ArrayList<>();
        Collections.addAll(receivers, rs);

        if(!checkFields(receivers)){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Make sure that all the fields are correct", ButtonType.CANCEL);
            alert.showAndWait();
            return;
        }

        Email newEmail = new Email(mailFrom.getText(), receivers, mailSubject.getText(), mailBody.getText());
        boolean ok = clientConnection.sendEmail(newEmail);

        if(!ok){
            Alert alert = new Alert(Alert.AlertType.ERROR, "An error as occurred, the mail as not been sent", ButtonType.CANCEL);
            alert.showAndWait();
        }else{
            mailController.addSentEmail(newEmail);

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "The Email has been sent", ButtonType.OK);
            alert.showAndWait();
            if(alert.getResult() == ButtonType.OK){
                mailController.onSentButtonClick();
            }
        }
    }


    private boolean checkFields(ArrayList<String> receivers){
        for(String r : receivers){
            if(!Email.emailVerifier(r) || receivers.indexOf(r) != receivers.lastIndexOf(r)){
                return false;
            }
        }

        return mailFrom.getText().compareTo("") != 0 && mailTo.getText().compareTo("") != 0 && mailBody.getText().compareTo("") != 0;
    }

    public void setEmailText(Email email){
        mailTo.setText(email.getReceiverString());
        mailSubject.setText(email.getSubject());
        mailBody.setText(email.getBody());
    }

    public void clearText(){
        mailTo.setText("");
        mailSubject.setText("");
        mailBody.setText("");
    }
}
