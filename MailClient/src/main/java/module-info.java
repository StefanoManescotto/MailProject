module com.example.java_mailclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires Common;

    opens com.example.java_mailclient to javafx.fxml;
    exports com.example.java_mailclient;
    exports com.example.java_mailclient.controller;
    opens com.example.java_mailclient.controller to javafx.fxml;
}