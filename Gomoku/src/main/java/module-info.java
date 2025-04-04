module com.example.gomokuexample {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.gomokuexample to javafx.fxml;
    exports com.example.gomokuexample;
}