package com.example.java_mailclient;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Control;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class MailReaderController {
    @FXML
    private VBox vBox;

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
