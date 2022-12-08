package com.example.mailserver;

import com.example.mailserver.controller.ServerController;
import com.example.mailserver.server.Server;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ServerMain extends Application {
    private static Thread tServer = null;
    private static Server server;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ServerMain.class.getResource("server_log_view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1080, 780);
        stage.setTitle("Server Status");
        stage.setScene(scene);
        stage.setOnCloseRequest(windowEvent -> stopServer());
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public static void startServer(){
        if(tServer == null || !tServer.isAlive()){
            tServer = new Thread(() -> {
                try {
                    server = new Server();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                server.start();
            });
            tServer.start();
        }
    }

    public static void stopServer(){
        if(tServer != null && tServer.isAlive()){
            server.stopServer();
        }
    }

    public static boolean isAlive(){
        return tServer != null && tServer.isAlive();
    }

    public static void addLog(String sLog){
        Platform.runLater(() -> ServerController.logText.addLine(sLog));
    }
}
