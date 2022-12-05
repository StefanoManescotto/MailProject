package org.example;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Email implements Serializable {
    private int mailId;
    private String sender, body, subject, date; // TODO: receiver as array (?)
    private ArrayList<String> receiver;

    public Email(String sender, String receiver, String body){
        if(!emailVerifier(sender) || !emailVerifier(receiver)){
            throw new IllegalArgumentException();
        }
        this.sender = sender;
        this.receiver = new ArrayList<>();
        this.receiver.add(receiver);
        this.subject = "<No Subject>";
        this.body = body;
    }

    public Email(String sender, List<String> receivers, String body){
        if(!emailVerifier(sender) || !emailVerifier(receivers)){
            throw new IllegalArgumentException();
        }
        this.sender = sender;
        this.receiver = (ArrayList) List.of(receivers);
        this.subject = "<No Subject>";
        this.body = body;
    }

    public Email(String sender, String receiver, String subject, String body){
        if(!emailVerifier(sender) || !emailVerifier(receiver)){
            throw new IllegalArgumentException();
        }
        this.sender = sender;
        this.receiver = new ArrayList<>();
        this.receiver.add(receiver);
        this.subject = subject;
        this.body = body;
    }

    public Email(String sender, List<String> receivers, String subject, String body){
        if(!emailVerifier(sender) || !emailVerifier(receivers)){
            throw new IllegalArgumentException();
        }
        this.sender = sender;
        this.receiver = (ArrayList) receivers;
        this.subject = subject;
        this.body = body;
    }

    /**
     * Verify that a string is an email address
     * @param emailAddress to control
     * @return true if it's an email address, false otherwise
     */
    public static boolean emailVerifier(String emailAddress){
        Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        Matcher mat = pattern.matcher(emailAddress);
        return mat.matches();
    }

    /**
     * Verify that a vector of string is an email address
     * @param emailAddresses, list of addresses to control
     * @return true if it's an email address, false otherwise
     */
    public static boolean emailVerifier(List<String> emailAddresses){
        Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        for(String s : emailAddresses){
            Matcher mat = pattern.matcher(s);
            if(!mat.matches()){
                return false;
            }
        }
        return true;
    }

    public int getMailId() {
        return mailId;
    }

    public void setMailId(int mailId) {
        if(mailId < 0){
            throw new IllegalArgumentException();
        }
        this.mailId = mailId;
    }

    public String getSender() {
        return sender;
    }

    public ArrayList<String> getReceiver() {
        return receiver;
    }

    public String getBody() {
        return body;
    }

    public String getSubject() {
        return subject;
    }

    public String getDate() {
        return date;
    }

    /**
     * Convert a Email obj to a corresponding csv format
     * @return the csv format of the obj
     */
    public String getCsvFormat(){
        return getMailId()+","+getSender()+","+getReceiver()+","+getSubject()+","+getBody();
    }

    /**
     * Takes a csv line that represent an Email and return the corresponding Email obj
     * @param csvLine line from which take the parameter to form the new Email obj
     * @return the new Email
     */
    public static Email emailFromCsv(String csvLine){
        String[] s = csvLine.split(",");
        String[] rcvs = s[2].split("\\.\\.");

        ArrayList<String> arrayList = new ArrayList<>();
        Collections.addAll(arrayList, rcvs);


        return new Email(s[1], arrayList, s[3], s[4]);
    }

    @Override
    public String toString() {
        return "SND: " + sender + "\nRCV: " + receiver;
    }
}
