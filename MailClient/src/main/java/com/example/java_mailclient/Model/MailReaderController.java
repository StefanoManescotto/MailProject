package com.example.java_mailclient.Model;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class MailReaderController {
    @FXML
    private TextArea mailTxt;

    private MailController mailController;

    public void setMailText(String s){
        mailTxt.setText(s);
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
