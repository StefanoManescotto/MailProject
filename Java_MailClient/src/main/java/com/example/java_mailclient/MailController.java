package com.example.java_mailclient;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class MailController {
    @FXML
    private BorderPane borderPane;

//    @FXML
//    Label test;
    @FXML
    VBox vBox;
    @FXML
    private Button inboxBtn;

    @FXML
    private ListView<String> mailListView;
    FXMLLoader mailReaderLoader;
    MailReaderController readerController;

    //TODO: after num character in listMail cut the text and add '...'

    @FXML
    public void initialize() throws IOException{
        ObservableList<String> items =  FXCollections.observableArrayList ("Nome1 - Object1 - Text1Text1Text1", "Double", "Suite", "Family App");
        mailListView.setItems(items);

        mailReaderLoader = new FXMLLoader(getClass().getResource("emailTextView.fxml"));
        mailReaderLoader.load();
        readerController = mailReaderLoader.getController();
        readerController.setMailController(this);

        mailListView.setOnMouseClicked(this::onListDoubleClick);
    }

    private void onListDoubleClick(MouseEvent event){
        if(event.getClickCount() == 2){
            readerController.setMailText("CIAO TEST TESTO");
            borderPane.setCenter(mailReaderLoader.getRoot());
        }
    }

    @FXML
    protected void onHelloButtonClick(){
        borderPane.setCenter(mailListView);
    }

    public void viewEmails(){
        borderPane.setCenter(mailListView);
    }
}
