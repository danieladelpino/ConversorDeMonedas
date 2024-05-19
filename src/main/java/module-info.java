module com.example.conversormonedas {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.net.http;

    opens com.example.conversormonedas to javafx.fxml, com.google.gson;
    exports com.example.conversormonedas;
}
