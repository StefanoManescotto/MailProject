package com.example.java_mailclient.Model;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

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

    public void setMailText(String s){
        mailBody.setText(s);
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
}
