package com.example.java_mailclient.Model;

import com.example.java_mailclient.MailApplication;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

import java.io.*;

public class MailController {
    @FXML
    private BorderPane borderPane;

    @FXML
    private ListView<String> mailListView;
    FXMLLoader mailReaderLoader;
    FXMLLoader mailSenderLoader;
    MailReaderController readerController;
    MailSenderController senderController;

    private ObservableList<String> items;

    //TODO: after num character in listMail cut the text and add '...'

    @FXML
    public void initialize() throws IOException{
        items =  FXCollections.observableArrayList ("Nome1 - Object1 - Text1Text1Text1", "Nome2 - Object2 - Text1Text1Text2",
                "Nome3 - Object3 - Text1Text1Text3", "Nome4 - Object4 - Text1Text1Text4");
        mailListView.setItems(items);

        mailReaderLoader = new FXMLLoader(MailApplication.class.getResource("emailTextView.fxml"));
        mailReaderLoader.load();
        readerController = mailReaderLoader.getController();
        readerController.setMailController(this);

        mailSenderLoader = new FXMLLoader(MailApplication.class.getResource("emailSendView.fxml"));
        mailSenderLoader.load();
        senderController = mailSenderLoader.getController();
        senderController.setMailController(this);

        mailListView.setOnMouseClicked(this::onListDoubleClick);
    }

    private void onListDoubleClick(MouseEvent event){
        if(event.getClickCount() == 2){

            readerController.setMailText(mailListView.getSelectionModel().getSelectedItems().toString());
            borderPane.setCenter(mailReaderLoader.getRoot());
        }
    }

    @FXML
    protected void onInboxButtonClick(){
        borderPane.setCenter(mailListView);
    }

    @FXML
    protected void onSendButtonClick() {
        ((MailSenderController) mailSenderLoader.getController()).clearText();
        borderPane.setCenter(mailSenderLoader.getRoot());
    }


    public void viewEmails(){
        borderPane.setCenter(mailListView);
    }
}
