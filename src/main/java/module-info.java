module com.example.databaseapplication {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.databaseapplication to javafx.fxml;
    exports com.example.databaseapplication;
}