package com.example.databaseapplication.controllers;

import com.example.databaseapplication.HelloApplication;
import com.example.databaseapplication.dao.GameCharacterDao;
import com.example.databaseapplication.dao.GameWorldDao;
import com.example.databaseapplication.model.GameCharacter;
import com.example.databaseapplication.model.GameWorld;
import com.example.databaseapplication.service.GameCharacterService;
import com.example.databaseapplication.service.GameWorldService;
import com.example.databaseapplication.session.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameController {
    @FXML
    private Label worldLabel;
    @FXML
    private ListView<GameCharacter> gameCharactersPanel;
    @FXML private PlayerCharacterController playerPreviewController = new PlayerCharacterController();
    @FXML
    private Button characterSelectionButton;

    private GameCharacterService gameCharacterService;
    private ExecutorService executorService;
    private SceneController sceneController = new SceneController();

    private GameCharacter currentCharacter = Session.getCurrentGameCharacter();
    private GameWorld currentGameWorld;
    private static final Logger LOG = LoggerFactory.getLogger(GameController.class);


    @FXML
    private void initialize(){
        gameCharacterService = new GameCharacterService(new GameCharacterDao());
        executorService = Executors.newSingleThreadExecutor();

        currentGameWorld = currentCharacter.getGameWorld();
        playerPreviewController.setCharacter(currentCharacter);
        worldLabel.setText(currentGameWorld.toString());

        loadCharacters();

    }
    private void loadCharacters() {
        EntityManager em = null;
        try {
            em = HelloApplication.createEM();
            List<GameCharacter> gamers = gameCharacterService.getCharactersForGameWorld(currentGameWorld, em);
            ObservableList<GameCharacter> items = FXCollections.observableArrayList(gamers);
            gameCharactersPanel.setItems(items);
        } catch (Exception e) {
            LOG.error("Exception occurred while loading characters", e);
        } finally {
            if (em != null) em.close();
        }
    }

    @FXML
    private void onCharacterSelectionButtonClick(ActionEvent event) throws IOException {
        sceneController.changeScene(event,"character-selection.fxml");
    }
    @FXML
    private void onLogoutButtonClick(ActionEvent event) throws IOException {
        Session.clear();
        sceneController.changeScene(event,"login.fxml");
    }
}
