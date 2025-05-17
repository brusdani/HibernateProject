package com.example.databaseapplication.controllers;

import com.example.databaseapplication.HelloApplication;
import com.example.databaseapplication.dao.GameCharacterDao;
import com.example.databaseapplication.model.GameCharacter;
import com.example.databaseapplication.model.GameWorld;
import com.example.databaseapplication.model.User;
import com.example.databaseapplication.service.GameCharacterService;
import com.example.databaseapplication.service.UserService;
import com.example.databaseapplication.session.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SelectionController {
    @FXML
    private Label welcomeLabel;
    @FXML
    private ListView<GameCharacter> characterPanel;
    @FXML
    private Button selectButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button createButton;

    private UserService userService;

    private GameCharacterService gameCharacterService;

    private ExecutorService executorService;

    private SceneController sceneController = new SceneController();
    private static final Logger LOG = LoggerFactory.getLogger(SelectionController.class);

    @FXML
    private void initialize() {
        gameCharacterService = new GameCharacterService(new GameCharacterDao());
        executorService = Executors.newSingleThreadExecutor();
        User currentUser = Session.getUser();
        welcomeLabel.setText("Welcome " + currentUser.getLogin());

        loadCharacters();

        characterPanel.setCellFactory(list -> new ListCellCharacter());
        characterPanel.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        selectButton.disableProperty().bind(
                characterPanel.getSelectionModel().selectedItemProperty().isNull());
        deleteButton.disableProperty().bind(
                characterPanel.getSelectionModel().selectedItemProperty().isNull());
    }
    @FXML
    private void onCreateButtonClick(ActionEvent event) throws IOException {
        sceneController.changeScene(event,"character-creation.fxml");
    }
    @FXML
    private void onSelectButtonClick(ActionEvent event) throws IOException {
        GameCharacter picked = characterPanel.getSelectionModel().getSelectedItem();

        if(picked != null) {
            Session.setCurrentGameCharacter(picked);
            GameWorld currentGameWorld = picked.getGameWorld();
            Session.setCurrentGameWorld(currentGameWorld);
            sceneController.changeScene(event,"game-interface.fxml");
        }

    }
    @FXML
    private void onDeleteButtonClick(){
        GameCharacter picked = characterPanel.getSelectionModel().getSelectedItem();
        EntityManager em = null;

        try{
            em = HelloApplication.createEM();
            gameCharacterService.deleteCharacter(picked, em);
            characterPanel.refresh();
        } catch (Exception e) {
            LOG.error("Exception occured while reloading data", e);
        } finally {
            if (em != null) em.close();
        }
        loadCharacters();
    }
    private void loadCharacters() {
        User currentUser = Session.getUser();
        EntityManager em = null;
        try {
            em = HelloApplication.createEM();
            List<GameCharacter> roster = gameCharacterService.getCharactersForUser(currentUser, em);
            currentUser.setUserCharacters(roster);
            ObservableList<GameCharacter> items = FXCollections.observableArrayList(roster);
            characterPanel.setItems(items);
            createButton.setDisable(roster.size() >= 3);
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
