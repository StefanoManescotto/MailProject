module com.example.java_mailclient {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.java_mailclient to javafx.fxml;
    exports com.example.java_mailclient;
}