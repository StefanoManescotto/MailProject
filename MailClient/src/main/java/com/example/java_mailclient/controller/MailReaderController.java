package com.example.java_mailclient.controller;

import com.example.java_mailclient.MailApplication;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import org.example.Email;

import java.util.ArrayList;

public class MailReaderController {
    @FXML
    private TextArea mailBody;
    @FXML
    private Label mailFrom;
    @FXML
    private Label mailTo;
    @FXML
    private Label mailSubject;

    private Email email;
    private MailController mailController;

    public void setClickedMail(Email email){
        this.email = email;

        mailFrom.setText(email.getSender());
        mailTo.setText(email.getReceiverString());
        mailSubject.setText(email.getSubject());
        mailBody.setText(email.getBody());
    }

    @FXML
    public void initialize(){

    }

    public void setMailController(MailController mailController){
        this.mailController = mailController;
    }

    @FXML
    public void onBackClick(){
        mailController.viewEmails();
    }

    @FXML
    public void onForwardClick(){
        Email e = new Email(MailApplication.getCurrUser(), "",
                "[Forwarded from " + email.getSender() + "] " + email.getSubject(), email.getBody());
        mailController.sendFormedEmail(e);
    }

    @FXML
    public void onReplyClick(){
        Email e = new Email(MailApplication.getCurrUser(), email.getSender(), "[Re] " + email.getSubject(), "");
        mailController.sendFormedEmail(e);
    }

    @FXML
    public void onReplyAllClick(){
        ArrayList<String> r = new ArrayList<>();
        r.add(email.getSender());

        for(String s : email.getReceiver()){
            if(!s.equals(MailApplication.getCurrUser())){
                r.add(s);
            }
        }

        Email e = new Email(MailApplication.getCurrUser(), r, "[Re] " + email.getSubject(), "");
        mailController.sendFormedEmail(e);
    }
}
