module com.example.mailserver {
    requires javafx.controls;
    requires javafx.fxml;
    requires Common;
    requires com.fasterxml.jackson.databind;


    opens com.example.mailserver to javafx.fxml;
    exports com.example.mailserver;
    exports com.example.mailserver.controller;
    opens com.example.mailserver.controller to javafx.fxml;
}