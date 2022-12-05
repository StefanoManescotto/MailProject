package com.example.mailserver.server.datamanager;

import com.example.mailserver.ServerMain;
import com.example.mailserver.server.Server;
import com.example.mailserver.server.ServerConnection;
import org.example.Email;
import org.example.ServerResponse;

import java.io.*;
import java.util.ArrayList;

public class MailManager {

    ServerConnection serverConnection;
    public MailManager(ServerConnection serverConnection){
        this.serverConnection = serverConnection;
    }

    public void sendMail(Email email){
        try {
            if(!checkUsersMail(email)) {
                serverConnection.sendResponse(new ServerResponse(email.getSender(), ServerResponse.Type.ADDRESS_UNKNOWN));
                return;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for(String receiver : email.getReceiver()){
            addMailToFile(email, "MailFiles/Inbox/" + receiver + ".csv");
        }
        addMailToFile(email, "MailFiles/Inbox/" + email.getSender() + ".csv");
    }

    public ArrayList<Email> getInbox(String mail){
        ArrayList<Email> inbox = new ArrayList<>();

        File f = new File("MailFiles/Inbox/" + mail + ".csv");
        if(f.exists()){
            try {
                String line;
                BufferedReader bufferedReader = new BufferedReader(new FileReader(f));
                while((line = bufferedReader.readLine()) != null){
                    inbox.add(Email.emailFromCsv(line));
                }
            } catch (IOException e) {
                ServerMain.addLog(e.getMessage());
            }
        }

        return inbox;
    }

    private void addMailToFile(Email email, String path){
        File f = new File(path);
        String lineSeparator = "\n";
        try {
            if(f.createNewFile()){
                lineSeparator = "";
            }
            FileWriter fw = new FileWriter(f, true);
            fw.append(lineSeparator).append(email.getCsvFormat());
            fw.flush();
            fw.close();
        } catch (IOException e) {
            ServerMain.addLog(e.getMessage());
        }
    }

    private boolean checkUsersMail(Email email){
        if(!userExist(email.getSender())){
            return false;
        }

        ArrayList<String> receivers = email.getReceiver();
        for(String r : receivers){
            if(!userExist(r)){
                return false;
            }
        }
        return true;
    }

    private boolean userExist(String mail){
        File f = new File("MailFiles/mailList.csv");
        BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(new FileReader(f));
            String line;
            while((line = bufferedReader.readLine()) != null){
                if(line.compareTo(mail) == 0){
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
