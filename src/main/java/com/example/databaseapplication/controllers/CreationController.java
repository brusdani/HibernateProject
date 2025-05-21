package com.example.databaseapplication.controllers;

import com.example.databaseapplication.HelloApplication;
import com.example.databaseapplication.dao.GameCharacterDao;
import com.example.databaseapplication.dao.GameWorldDao;
import com.example.databaseapplication.dao.UserDao;
import com.example.databaseapplication.model.CharacterJob;
import com.example.databaseapplication.model.GameCharacter;
import com.example.databaseapplication.model.GameWorld;
import com.example.databaseapplication.model.User;
import com.example.databaseapplication.service.GameCharacterService;
import com.example.databaseapplication.service.GameWorldService;
import com.example.databaseapplication.service.UserService;
import com.example.databaseapplication.session.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.shape.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreationController extends BaseController {
    private static final Logger LOG = LoggerFactory.getLogger(CreationController.class);

    @FXML
    private TextField nameField;
    @FXML
    private ToggleGroup characterJobChoice;
    @FXML
    private Label infoLabel;
    @FXML
    private ListView<GameWorld> gameWorldPanel;
    @FXML
    private Button createButton;
    @FXML
    private Button backButton;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Rectangle overlay;

    private GameCharacterService gameCharacterService;
    private GameWorldService gameWorldService;
    private ExecutorService executorService;
    private SceneController sceneController = new SceneController();


    @FXML
    private void initialize() {
        gameCharacterService = new GameCharacterService(new GameCharacterDao());
        gameWorldService = new GameWorldService(new GameWorldDao(), new GameCharacterDao());
        executorService = Executors.newSingleThreadExecutor();

        gameWorldPanel.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        loadGameWorlds();

        createButton.disableProperty().bind(
                gameWorldPanel.getSelectionModel().selectedItemProperty().isNull());
        nameField.requestFocus();
    }
    @FXML
    private void onCreateButtonClick(ActionEvent event) {
        final String characterName = nameField.getText().trim();
        if (characterName.isEmpty()) {
            infoLabel.setText("You must enter character name");
            return;
        }
        if (characterName.length() < 3 || characterName.length() > 12) {
            infoLabel.setText("Username must be 3–12 characters long.");
            return;
        }
        if (!characterName.matches("[A-Za-z0-9_]+")) {
            infoLabel.setText("Username may only contain letters, digits, and underscores.");
            return;
        }

        RadioButton selectedRb = (RadioButton) characterJobChoice.getSelectedToggle();
        final CharacterJob job = CharacterJob.valueOf(selectedRb.getText().toUpperCase());

        final GameWorld selectedWorld =
                gameWorldPanel.getSelectionModel().getSelectedItem();
        if (selectedWorld == null) {
            infoLabel.setText("You must pick a world");
            return;
        }
        infoLabel.setText("");
        Task<GameCharacter> createTask = new Task<>() {
            @Override
            protected GameCharacter call() throws Exception {
                //Thread.sleep(3000);
                EntityManager em = null;
                try {
                    em = HelloApplication.createEM();
                    return gameCharacterService.createNewCharacter(
                            characterName, job, selectedWorld, em
                    );
                } finally {
                    if (em != null) em.close();
                }
            }
            @Override
            protected void succeeded() {
                GameCharacter created = getValue();
                if (created != null) {
                    try {
                        sceneController.changeScene(event, "character-selection.fxml");
                    } catch (IOException ex) {
                        LOG.error("Navigation error after create", ex);
                        infoLabel.setText("Unexpected navigation error");
                    }
                } else {
                    LOG.error("Character was not created");
                    infoLabel.setText("Creation failed");
                }
            }
            @Override
            protected void failed() {
                LOG.error("Exception occurred while creating character", getException());
                infoLabel.setText("Error: " + getException().getMessage());
            }
        };
        handleTaskFailure(createTask);
        FXUtils.bindUiToTask(
                createTask,
                overlay,
                progressIndicator,
                List.of(nameField, gameWorldPanel),
                List.of(createButton, backButton),
                gameWorldPanel.getSelectionModel().selectedItemProperty()
        );
        executorService.submit(createTask);
    }
    @FXML
    private void onBackButtonClick(ActionEvent event) throws IOException {
        sceneController.changeScene(event,"character-selection.fxml");
    }
    private void loadGameWorlds() {
        Task<ObservableList<GameWorld>> loadTask = new Task<>() {
            @Override
            protected ObservableList<GameWorld> call() throws Exception {
                //Thread.sleep(5000);
                EntityManager em = null;
                try {
                    em = HelloApplication.createEM();
                    List<GameWorld> universe = gameWorldService.getAllGameWorlds(em);
                    return FXCollections.observableArrayList(universe);
                } finally {
                    if (em != null) em.close();
                }
            }

            @Override
            protected void succeeded() {
                gameWorldPanel.setItems(getValue());
            }

            @Override
            protected void failed() {
                LOG.error("Exception occurred while loading game worlds", getException());
            }
        };
        handleTaskFailure(loadTask);
        FXUtils.bindUiToTask(
                loadTask,
                overlay,
                progressIndicator,
                gameWorldPanel    // disable this control during load
        );

        executorService.submit(loadTask);
    }
    @FXML
    private void onReloadButtonClick(ActionEvent event){
        loadGameWorlds();
    }



}
