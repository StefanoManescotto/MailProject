package com.example.mailserver.server.datamanager;

import com.example.mailserver.ServerMain;
import org.example.Email;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MailManager {
    private ReadWriteLock readWriteLock;

    public MailManager(){
        readWriteLock = new ReentrantReadWriteLock();
    }

    public boolean sendMail(Email email){

        if(!checkUsersMail(email)) {
            return false;
        }

        email.setMailId(getEmailId());

        addMailToFile(email, "MailFiles/Inbox/" + email.getSender() + ".csv");
        for(String receiver : email.getReceiver()){
            if(receiver.compareTo(email.getSender()) != 0) {
                addMailToFile(email, "MailFiles/Inbox/" + receiver + ".csv");
            }
        }
        return true;
    }

    public ArrayList<Email> getInbox(String mail){
        ArrayList<Email> inbox = new ArrayList<>();

        File f = new File("MailFiles/Inbox/" + mail + ".csv");
        if(f.exists()){
            try {
                readWriteLock.readLock().lock();

                String line;
                BufferedReader bufferedReader = new BufferedReader(new FileReader(f));
                while((line = bufferedReader.readLine()) != null){
                    Email e = Email.emailFromCsv(line);
                    if(e != null && e.isReceiverIn(mail)) {
                        inbox.add(e);
                    }
                }
                bufferedReader.close();
            } catch (IOException e) {
                ServerMain.addLog(e.getMessage());
            } finally {
                readWriteLock.readLock().unlock();
            }
        }
        return inbox;
    }

    public ArrayList<Email> getSent(String mail){
        ArrayList<Email> sent = new ArrayList<>();

        File f = new File("MailFiles/Inbox/" + mail + ".csv");
        if(f.exists()){
            try {
                readWriteLock.readLock().lock();

                String line;
                BufferedReader bufferedReader = new BufferedReader(new FileReader(f));
                while((line = bufferedReader.readLine()) != null){
                    Email e = Email.emailFromCsv(line);
                    if(e != null && e.getSender().compareTo(mail) == 0) {
                        sent.add(e);
                    }
                }
                bufferedReader.close();
            } catch (IOException e) {
                ServerMain.addLog(e.getMessage());
            } finally {
                readWriteLock.readLock().unlock();
            }
        }
        return sent;
    }

    public void removeMail(Email email, String from){
        File inFile = new File("MailFiles/Inbox/" + from + ".csv");

        if(inFile.exists()) {
            try {
                readWriteLock.writeLock().lock();

                File tempFile = new File(inFile.getAbsolutePath() + ".tmp");

                BufferedReader br = new BufferedReader(new FileReader(inFile));
                PrintWriter pw = new PrintWriter(new FileWriter(tempFile));

                String line;
                while ((line = br.readLine()) != null) {
                    Email e = Email.emailFromCsv(line);
                    if (!line.isEmpty() && e != null && !e.equals(email)) {
                        pw.println(line);
                        pw.flush();
                    }
                }

                pw.close();
                br.close();

                if (!inFile.delete()) {
                    ServerMain.addLog("removeMail Err: Could not delete file");
                    return;
                }
                if (!tempFile.renameTo(inFile)) {
                    ServerMain.addLog("removeMail Err: Could not rename file");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                readWriteLock.writeLock().unlock();
            }
        }else{
            ServerMain.addLog("removeMail Err: file not exist");
        }
    }

    private void addMailToFile(Email email, String path){
        readWriteLock.writeLock().lock();

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
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    /**
     * control that the sender and all the receivers of a mail are users
     * @param email to control
     * @return true if sender and receivers are users, false otherwise
     */
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

    public boolean authenticateUser(String user){
        return Email.emailVerifier(user) && userExist(user);
    }

    /**
     * control if a user exist in the file 'mailList.csv'
     * @param mail user to control
     * @return true if it exists, false otherwise
     */
    private boolean userExist(String mail){
        File f = new File("MailFiles/mailList.csv");
        BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(new FileReader(f));
            String line;
            while((line = bufferedReader.readLine()) != null){
                if(line.equals(mail)){
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * get an available mail id and update the file
     * @return the available id
     */
    private synchronized int getEmailId(){
        int id = -1;

        try {
            File inFile = new File("MailFiles/lastID");
            File tempFile = new File(inFile.getAbsolutePath() + ".tmp");

            BufferedReader br = new BufferedReader(new FileReader(inFile));
            PrintWriter pw = new PrintWriter(new FileWriter(tempFile));

            String line = br.readLine();
            id = Integer.parseInt(line);

            pw.println(id + 1);
            pw.flush();

            pw.close();
            br.close();

            if(!inFile.delete()){
                ServerMain.addLog("getEmailId Err: Could not delete file");
                return -1;
            }
            if(!tempFile.renameTo(inFile)) {
                ServerMain.addLog("getEmailId Err: Could not rename file");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return id;
    }
}
