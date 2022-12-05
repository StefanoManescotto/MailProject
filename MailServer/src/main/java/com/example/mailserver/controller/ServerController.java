package com.example.mailserver.controller;

import com.example.mailserver.ServerMain;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class ServerController {
    @FXML
    private TextArea log;
    @FXML
    public Button serverBtn;
    public static ObservableStringBuffer logText = new ObservableStringBuffer();

    @FXML
    public void initialize(){
        log.textProperty().bind(logText);
        logText.addListener((ChangeListener<Object>) (observable, oldValue, newValue) -> logAutoScroll());
    }

    private void logAutoScroll(){
        log.selectPositionCaret(log.getLength());
        log.deselect();
    }

    @FXML
    public void onServerStateButtonClick() {
        if(!ServerMain.isAlive()){
            serverBtn.setText("Stop Server");
            ServerMain.startServer();
        }else{
            serverBtn.setText("Start Server");
            ServerMain.stopServer();
        }
    }

    @FXML
    public void onCleanButtonClick(){
        logText.reset();
    }
}
