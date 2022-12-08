package com.example.java_mailclient.controller;

import com.example.java_mailclient.MailApplication;
import com.example.java_mailclient.model.ClientConnection;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.Email;
import org.example.ServerResponse;

import java.io.IOException;

public class LogInController {

    @FXML
    public TextField mailField;

    private ClientConnection clientConnection;

    @FXML
    public void initialize() {
        clientConnection = new ClientConnection();
    }

    @FXML
    public void onLogInClick(ActionEvent event) {
        boolean isEmail = Email.emailVerifier(mailField.getText());
        if(isEmail && clientConnection.userExist(mailField.getText()).getResponseType() == ServerResponse.Type.OK){
            MailApplication.setCurrUser(mailField.getText());
            Parent root;
            FXMLLoader loader;
            try {
                loader = new FXMLLoader(MailApplication.class.getResource("emailMainView.fxml"));
                root = loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setOnCloseRequest((e) -> ((MailController) loader.getController()).shutDown());
            Scene scene = new Scene(root, 1080, 720);
            stage.setResizable(true);
            stage.setScene(scene);
            stage.show();
        }else if(!isEmail){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Insert a valid Email", ButtonType.OK);
            alert.showAndWait();
        }else if(clientConnection.userExist(mailField.getText()).getResponseType() == ServerResponse.Type.SERVER_OFFLINE){
            Alert alert = new Alert(Alert.AlertType.ERROR, "The server is offline", ButtonType.OK);
            alert.showAndWait();
        }else if(clientConnection.userExist(mailField.getText()).getResponseType() == ServerResponse.Type.ADDRESS_UNKNOWN){
            Alert alert = new Alert(Alert.AlertType.ERROR, "The user does not exist", ButtonType.OK);
            alert.showAndWait();
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR, "An error has occurred", ButtonType.OK);
            alert.showAndWait();
        }
    }
}
