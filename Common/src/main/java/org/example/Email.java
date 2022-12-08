package org.example;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Email implements Serializable {
    private int mailId;
    private String sender, body, subject, date, receiverString;
    private ArrayList<String> receiver;

    public Email(String sender, String receiver, String subject, String body){
        if(!emailVerifier(sender) || (!receiver.equals("") && !emailVerifier(receiver))){
            throw new IllegalArgumentException();
        }
        this.sender = sender;
        this.receiver = new ArrayList<>();
        this.receiver.add(receiver);
        this.subject = controlSubject(subject);
        this.body = body;

        receiverString = receiverToString();
        date = getCurrentDate();
    }

    public Email(String sender, List<String> receivers, String subject, String body){
        if(!emailVerifier(sender) || !emailVerifier(receivers)){
            throw new IllegalArgumentException();
        }
        this.sender = sender;
        this.receiver = (ArrayList) receivers;
        this.subject = controlSubject(subject);
        this.body = body;

        receiverString = receiverToString();
        date = getCurrentDate();
    }

    private String controlSubject(String subject){
        if(subject.equals("")){
            return "<No Subject>";
        }
        return subject;
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

    public String getReceiverString(){
        return receiverToString();
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Convert a Email obj to a corresponding csv format
     * @return the csv format of the obj
     */
    public String getCsvFormat(){
        String s = receiver.stream().map(Object::toString).collect(Collectors.joining(".."));
        return getMailId()+","+getSender()+","+s+","+getSubject()+","+getBody()+","+getDate();
    }

    /**
     * Takes a csv line that represent an Email and return the corresponding Email obj
     * @param csvLine line from which take the parameter to form the new Email obj
     * @return the new Email
     */
    public static Email emailFromCsv(String csvLine){
        if(csvLine.compareTo("") == 0){
            return null;
        }

        String[] s = csvLine.split(",");
        String[] rcvs = s[2].split("\\.\\.");

        ArrayList<String> arrayList = new ArrayList<>();
        Collections.addAll(arrayList, rcvs);

        Email e = new Email(s[1], arrayList, s[3], s[4]);
        e.setDate(s[5]);
        return e;
    }

    /**
     * control if an email is in the list of receivers
     * @param rec email to control
     * @return true if it's in, false otherwise
     */
    public boolean isReceiverIn(String rec){
        for(String r : receiver){
            if(r.compareTo(rec) == 0){
                return true;
            }
        }
        return false;
    }

    public String receiverToString(){
        return receiver.stream().map(Object::toString).collect(Collectors.joining(" "));
    }

    private String getCurrentDate(){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        return formatter.format(date);
    }

    @Override
    public Email clone() {
        return new Email(this.sender, this.getReceiver(), this.subject, this.body);
    }

    @Override
    public String toString() {
        return "SND: " + sender + "\nRCV: " + receiver;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj.getClass() == getClass()) {
            return ((Email) obj).getMailId() == mailId &&
                    ((Email) obj).sender.equals(sender) &&
                    ((Email) obj).getReceiver().equals(getReceiver()) &&
                    ((Email) obj).getBody().equals(body);
        }
        return false;
    }
}
