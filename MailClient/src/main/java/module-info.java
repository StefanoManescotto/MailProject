module com.example.java_mailclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires Common;

    opens com.example.java_mailclient to javafx.fxml;
    exports com.example.java_mailclient;
    exports com.example.java_mailclient.Model;
    opens com.example.java_mailclient.Model to javafx.fxml;
}