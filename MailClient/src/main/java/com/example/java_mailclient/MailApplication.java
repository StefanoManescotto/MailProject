package com.example.java_mailclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.ServerRequest;
import org.example.ServerResponse;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class MailApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MailApplication.class.getResource("emailMainView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1080, 720);
        stage.setMinWidth(900);
        stage.setMinHeight(550);
        stage.setTitle("MyMail");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
