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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreationController {
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

    private GameCharacterService gameCharacterService;
    private GameWorldService gameWorldService;
    private ExecutorService executorService;
    private SceneController sceneController = new SceneController();


    @FXML
    private void initialize() {
        gameCharacterService = new GameCharacterService(new GameCharacterDao());
        gameWorldService = new GameWorldService(new GameWorldDao());
        executorService = Executors.newSingleThreadExecutor();

        gameWorldPanel.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        loadGameWorlds();

        createButton.disableProperty().bind(
                gameWorldPanel.getSelectionModel().selectedItemProperty().isNull());
    }
    @FXML
    private void onCreateButtonClick(ActionEvent event){
        String characterName = nameField.getText().trim();
        if (characterName.isEmpty()) {
            infoLabel.setText("You must enter character name");
            return;
        }
        RadioButton selected = (RadioButton) characterJobChoice.getSelectedToggle();
        CharacterJob job = CharacterJob.valueOf(selected.getText().toUpperCase());

        GameWorld selectedWorld = gameWorldPanel
                .getSelectionModel()
                .getSelectedItem();
        if (selectedWorld == null) {
            infoLabel.setText("You must pick a world");
            return;
        }

        EntityManager em = null;

        try {
            em = HelloApplication.createEM();

            //GameWorld defaultWorld = em.find(GameWorld.class, 1L);
            GameCharacter newGameCharacter = gameCharacterService.createNewCharacter(characterName, job, selectedWorld, em);
            if (newGameCharacter != null) {
                sceneController.changeScene(event,"character-selection.fxml");
            } else {
                LOG.error("Character was not created");
            }
        } catch (Exception e) {
            LOG.error("Exception occured while reloading data", e);
        }finally {
            if (em != null) em.close();
        }
    }
    @FXML
    private void onBackButtonClick(ActionEvent event) throws IOException {
        sceneController.changeScene(event,"character-selection.fxml");
    }
    private void loadGameWorlds() {
        EntityManager em = null;
        try {
            em = HelloApplication.createEM();
            List<GameWorld> universe = gameWorldService.getAllGameWorlds(em);
            ObservableList<GameWorld> items = FXCollections.observableArrayList(universe);
            gameWorldPanel.setItems(items);
        } catch (Exception e) {
            LOG.error("Exception occurred while loading characters", e);
        } finally {
            if (em != null) em.close();
        }
    }
    @FXML
    private void onLogoutButtonClick(ActionEvent event) throws IOException {
        Session.clear();
        sceneController.changeScene(event,"login.fxml");
    }



}
