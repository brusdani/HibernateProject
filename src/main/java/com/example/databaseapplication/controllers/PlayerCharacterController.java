package com.example.databaseapplication.controllers;

import com.example.databaseapplication.model.GameCharacter;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;

public class PlayerCharacterController {
    @FXML
    private ImageView characterImage;
    @FXML
    private Label characterNameLabel;
    @FXML
    private Label characterJobLabel;



    public void setCharacter(GameCharacter character) {
        setImage(character.getImageURL());
        characterNameLabel.setText(character.getName());
        characterJobLabel.setText(character.getJob().toString());
    }


    public void setImage(String imagePath) {
        String resource = imagePath.startsWith("/") ? imagePath : "/" + imagePath;
        URL url = getClass().getResource(resource);
        System.out.println(imagePath);
        System.out.println(resource);
        if (url != null) {
            characterImage.setImage(new Image(url.toExternalForm()));
        } else {
            characterImage.setImage(null);
        }
    }
}
