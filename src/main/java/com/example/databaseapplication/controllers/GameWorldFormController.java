package com.example.databaseapplication.controllers;

import com.example.databaseapplication.HelloApplication;
import com.example.databaseapplication.dao.GameWorldDao;
import com.example.databaseapplication.model.GameCharacter;
import com.example.databaseapplication.model.GameWorld;
import com.example.databaseapplication.service.GameWorldService;
import com.example.databaseapplication.session.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.concurrent.ExecutorService;

public class GameWorldFormController {
    private static final Logger LOG = LoggerFactory.getLogger(GameWorldFormController.class);

    @FXML
    private TextField worldNameField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private Button cancelButton;
    @FXML
    private Button saveButton;
    @FXML
    private Label errorLabel;

    private GameWorld workingCopy;
    private boolean editMode;
    private GameWorldService gameWorldService;
    private ExecutorService executorService;
    private SceneController sceneController = new SceneController();

    @FXML
    private void initialize(){
        gameWorldService = new GameWorldService(new GameWorldDao());

        GameWorld selected = Session.getCurrentGameWorld();
        if (selected != null){
            editMode = true;
            workingCopy = selected;
            worldNameField.setText(selected.getWorldName());
            descriptionArea.setText(selected.getWorldDescription());
            saveButton.setText("Update");
        } else {
            editMode = false;
            workingCopy = new GameWorld();
            saveButton.setText("Create");
        }

    }
    @FXML
    private void onSaveButtonclick(ActionEvent event){
        // clear any previous error
        errorLabel.setText("");

        workingCopy.setWorldName(   worldNameField.getText().trim());
        workingCopy.setWorldDescription(descriptionArea.getText().trim());

        EntityManager em = null;
        try {
            em = HelloApplication.createEM();

            if (editMode) {
                try {
                    GameWorld editedWorld = gameWorldService.updateWorld(workingCopy, em);
                    if (editedWorld != null) {
                        sceneController.changeScene(event, "admin-panel.fxml");
                    } else {
                        LOG.error("Game World was not edited");
                        errorLabel.setText("Unexpected error: world was not updated.");
                    }
                } catch (ConcurrentModificationException cme) {
                    // version conflict: inform the user
                    errorLabel.setText(cme.getMessage());
                }
            } else {
                GameWorld newGameworld = gameWorldService.createNewWorld(
                        workingCopy.getWorldName(),
                        workingCopy.getWorldDescription(),
                        em
                );
                if (newGameworld != null) {
                    sceneController.changeScene(event,"admin-panel.fxml");
                } else {
                    LOG.error("Game World was not created");
                    errorLabel.setText("Unexpected error: world was not created.");
                }
            }
        } catch (Exception e) {
            LOG.error("Exception occurred while saving world", e);
        } finally {
            if (em != null) em.close();
        }
    }
    @FXML
    private void onCancelButtonClick(ActionEvent event) throws IOException {
            Session.setCurrentGameWorld(null);
            sceneController.changeScene(event,"admin-panel.fxml");
    }


}
