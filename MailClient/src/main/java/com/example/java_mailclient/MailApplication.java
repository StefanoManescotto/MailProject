package com.example.java_mailclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.Email;

import java.io.*;

public class MailApplication extends Application {
    private static String currUser = "";

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MailApplication.class.getResource("logInView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 480, 360);
        stage.setResizable(false);
        stage.setTitle("MyMail");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public static void setCurrUser(String user){
        if(!Email.emailVerifier(user)){
            throw new IllegalArgumentException();
        }
        currUser = user;
    }

    public static String getCurrUser(){
        return currUser;
    }
}
