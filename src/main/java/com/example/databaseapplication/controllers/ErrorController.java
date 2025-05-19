package com.example.databaseapplication.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class ErrorController {
    @FXML
    private Button quitButton;

    @FXML
    private void initialize() {
    }
    @FXML
    private void onQuitButtonClick(ActionEvent event){
        Platform.exit();
        System.exit(0);
    }
}
