package com.example.databaseapplication.controllers;

import com.example.databaseapplication.dao.GameCharacterDao;
import com.example.databaseapplication.model.GameCharacter;
import com.example.databaseapplication.session.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class GameController {
    @FXML
    private Label worldLabel;
    @FXML private PlayerCharacterController playerPreviewController = new PlayerCharacterController();

    @FXML
    private void initialize(){
        GameCharacter currentCharacter = Session.getCurrentGameCharacter();

        playerPreviewController.setCharacter(currentCharacter);


    }
}
