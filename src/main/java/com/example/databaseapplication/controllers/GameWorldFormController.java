package com.example.databaseapplication.controllers;

import com.example.databaseapplication.HelloApplication;
import com.example.databaseapplication.dao.GameCharacterDao;
import com.example.databaseapplication.dao.GameWorldDao;
import com.example.databaseapplication.model.GameCharacter;
import com.example.databaseapplication.model.GameWorld;
import com.example.databaseapplication.service.GameWorldService;
import com.example.databaseapplication.session.Session;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.shape.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameWorldFormController extends BaseController {
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
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Rectangle overlay;

    private GameWorld workingCopy;
    private boolean editMode;
    private GameWorldService gameWorldService;
    private ExecutorService executorService;
    private SceneController sceneController = new SceneController();

    @FXML
    private void initialize(){
        gameWorldService = new GameWorldService(new GameWorldDao(), new GameCharacterDao());
        executorService = Executors.newSingleThreadExecutor();

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
    private void onSaveButtonclick(ActionEvent event) {
        errorLabel.setText("");
        workingCopy.setWorldName(worldNameField.getText().trim());
        workingCopy.setWorldDescription(descriptionArea.getText().trim());
        if (workingCopy.getWorldName().isEmpty()) {
            errorLabel.setText("Name cannot be blank");
            return;
        }
        Task<GameWorld> saveTask = new Task<>() {
            @Override
            protected GameWorld call() throws Exception {
                EntityManager em = null;
                try {
                    em = HelloApplication.createEM();
                    if (editMode) {
                        return gameWorldService.updateWorld(workingCopy, em);
                    } else {
                        return gameWorldService.createNewWorld(
                                workingCopy.getWorldName(),
                                workingCopy.getWorldDescription(),
                                em
                        );
                    }
                } finally {
                    if (em != null) em.close();
                }
            }
            @Override
            protected void succeeded() {
                GameWorld result = getValue();
                if (result != null) {
                    try {
                        sceneController.changeScene(event, "admin-panel.fxml");
                    } catch (IOException ex) {
                        LOG.error("Navigation error after save", ex);
                        errorLabel.setText("Could not switch back to admin panel");
                    }
                } else {
                    // null means create/update failed silently
                    LOG.error("GameWorld save returned null");
                    errorLabel.setText("Save failed");
                }
            }

            @Override
            protected void failed() {
                LOG.error("Exception occurred while saving world", getException());
                errorLabel.setText("Error: " + getException().getMessage());
            }
        };
        handleTaskFailure(saveTask);
        FXUtils.bindUiToTask(
                saveTask,
                overlay,
                progressIndicator,
                worldNameField,
                descriptionArea,
                saveButton
        );
        executorService.submit(saveTask);
    }

    @FXML
    private void onCancelButtonClick(ActionEvent event) throws IOException {
            Session.setCurrentGameWorld(null);
            sceneController.changeScene(event,"admin-panel.fxml");
    }


}
