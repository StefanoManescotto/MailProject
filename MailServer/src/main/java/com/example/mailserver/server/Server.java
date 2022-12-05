package com.example.mailserver.server;

import com.example.mailserver.ServerMain;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private final ServerSocket serverSocket = new ServerSocket(Configs.PORT);
    private final ExecutorService executor = Executors.newFixedThreadPool(Configs.THREAD_NUM);
    public Server() throws IOException {
    }

    public void start(){
        ServerMain.addLog("Started");
        try {
            while(!Thread.interrupted()){
                Socket incoming = serverSocket.accept();
                incoming.setSoTimeout(10000);
                executor.execute(new ServerConnection(incoming));
            }
        } catch (IOException e) {
            ServerMain.addLog(e.getMessage());
        } finally {
            ServerMain.addLog("Shutting down...");
            stopServer();
            ServerMain.addLog("END");
        }
    }

    public void stopServer(){
        try {
            if(!serverSocket.isClosed()){
                serverSocket.close();
            }
            if(!executor.isShutdown()){
                executor.shutdown();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
