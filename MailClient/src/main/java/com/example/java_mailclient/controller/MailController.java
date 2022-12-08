package com.example.java_mailclient.controller;

import com.example.java_mailclient.MailApplication;
import com.example.java_mailclient.model.ClientConnection;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import org.example.Email;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MailController {
    @FXML
    private BorderPane borderPane;
    @FXML
    TableView<Email> tableView;
    @FXML
    TableColumn<Email, String> colSender;
    @FXML
    TableColumn<Email, String> colSubject;
    @FXML
    TableColumn<Email, String> colDate;
    @FXML
    TableColumn<Email, String> colBody;
    @FXML
    Label userLabel;
    @FXML
    Label serverStatusLabel;

    FXMLLoader mailReaderLoader;
    FXMLLoader mailSenderLoader;
    MailReaderController readerController;
    MailSenderController senderController;

    ClientConnection clientConnection;

    private ObservableList<Email> items;
    private ArrayList<Email> emailsInbox, emailsSent;
    private short lastSeen = 0; // 0 if the user clicked on 'inbox' last, 1 if the user clicked on 'sent' last
    private Stage stage;
    private final ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);

    @FXML
    public void initialize() throws IOException{
        userLabel.setText(MailApplication.getCurrUser());
        serverStatusLabel.setText("");
        tableView.setPlaceholder(new Label("No mail to visualize"));

        clientConnection = new ClientConnection();

        emailsInbox = clientConnection.getInbox(MailApplication.getCurrUser());

        items =  FXCollections.observableArrayList();

        if(emailsInbox != null){
            items.addAll(emailsInbox);
        }


        colSender.setCellValueFactory(new PropertyValueFactory<>("sender"));
        colSubject.setCellValueFactory(new PropertyValueFactory<>("subject"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colBody.setCellValueFactory(new PropertyValueFactory<>("body"));
        tableView.setItems(items);

        colSender.setReorderable(false);
        colSubject.setReorderable(false);
        colBody.setReorderable(false);
        colDate.setReorderable(false);

        mailReaderLoader = new FXMLLoader(MailApplication.class.getResource("emailTextView.fxml"));
        mailReaderLoader.load();

        readerController = mailReaderLoader.getController();
        readerController.setMailController(this);

        mailSenderLoader = new FXMLLoader(MailApplication.class.getResource("emailSendView.fxml"));
        mailSenderLoader.load();
        senderController = mailSenderLoader.getController();
        senderController.setMailController(this);
        senderController.setClientConnection(clientConnection);

        tableView.setOnMouseClicked(this::onListDoubleClick);

        emailsInbox = clientConnection.getInbox(MailApplication.getCurrUser());
        emailsSent = clientConnection.getSent(MailApplication.getCurrUser());
        exec.scheduleAtFixedRate(this::controlNewInbox, 5, 5, TimeUnit.SECONDS);
    }

    private void onListDoubleClick(MouseEvent event){
        if(event.getClickCount() == 2 && !tableView.getSelectionModel().getSelectedItems().isEmpty()){
            readerController.setClickedMail(tableView.getSelectionModel().getSelectedItems().get(0));
            borderPane.setCenter(mailReaderLoader.getRoot());
        }
    }

    @FXML
    protected void onInboxButtonClick(){
        lastSeen = 0;

        colSender.setText("Sender");
        colSender.setCellValueFactory(new PropertyValueFactory<>("sender"));

        if(emailsInbox != null){
            items.setAll(emailsInbox);
        }
        borderPane.setCenter(tableView);
    }

    @FXML
    protected void onSentButtonClick(){
        lastSeen = 1;

        colSender.setText("Receivers");
        colSender.setCellValueFactory(new PropertyValueFactory<>("receiverString"));

        if(emailsSent != null){
            items.setAll(emailsSent);
        }
        borderPane.setCenter(tableView);
    }

    @FXML
    protected void onWriteNewButtonClick() {
        ((MailSenderController) mailSenderLoader.getController()).clearText();
        borderPane.setCenter(mailSenderLoader.getRoot());
    }

    @FXML
    protected void onDeleteButtonClick() {
        if(tableView.getSelectionModel().getSelectedItems() != null &&
                tableView.getSelectionModel().getSelectedIndex() >= 0 &&
                clientConnection.deleteMail(tableView.getSelectionModel().getSelectedItems().get(0))){

            emailsInbox = clientConnection.getInbox(MailApplication.getCurrUser());
            removeSentEmail(tableView.getSelectionModel().getSelectedItems().get(0));
        }
        if(lastSeen == 0){
            items.setAll(emailsInbox);
        }else{
            items.setAll(emailsSent);
        }
    }

    @FXML
    protected void onLogInButtonClick(ActionEvent event) throws IOException {
        exec.shutdown();

        Parent root = FXMLLoader.load(MailApplication.class.getResource("logInView.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * set as the borderPane center the tableView, which is containing either the inbox or the sent emails
     */
    public void viewEmails(){
        borderPane.setCenter(tableView);
    }

    /**
     * set the fields in the sendView (view used to send emails) to match the one of the mail
     * @param email used to fill the fields in the view
     */
    public void sendFormedEmail(Email email){
        ((MailSenderController) mailSenderLoader.getController()).setEmailText(email);
        borderPane.setCenter(mailSenderLoader.getRoot());
    }

    /**
     * add an email to the list of the sent emails
     * @param e email to add
     */
    public void addSentEmail(Email e){
        emailsSent.add(e);
    }

    /**
     * remove an email to the list of the sent emails
     * @param e email to remove
     */
    public void removeSentEmail(Email e){
        emailsSent.remove(e);
    }

    /**
     * stop the thread used to check for new incoming emails
     */
    public void shutDown(){
        exec.shutdown();
    }

    /**
     * check the server if there are new emails and also control if the server is online
     */
    private void controlNewInbox(){
        ArrayList<Email> tmp = clientConnection.getInbox(MailApplication.getCurrUser());
        if(tmp == null){
            Platform.runLater(() -> serverStatusLabel.setText("No server connection"));
        }else{
            Platform.runLater(() -> serverStatusLabel.setText(""));
        }

        if(tmp != null && tmp.size() > emailsInbox.size()){
            emailsInbox = tmp;

            Platform.runLater(() -> {
                if (borderPane.getCenter() == tableView && lastSeen == 0) {
                    items.setAll(emailsInbox);
                } else{
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "A mail has arrived", ButtonType.OK);
                    alert.showAndWait();
                 }
            });
        }
    }
}
