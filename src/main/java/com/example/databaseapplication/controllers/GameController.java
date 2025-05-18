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
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameController extends BaseController {
    @FXML
    private Label worldLabel;
    @FXML
    private ListView<GameCharacter> gameCharactersPanel;
    @FXML
    private PlayerCharacterController playerPreviewController = new PlayerCharacterController();
    @FXML
    private Button characterSelectionButton;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Rectangle overlay;

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
        Task<ObservableList<GameCharacter>> loadTask = new Task<>() {
            @Override
            protected ObservableList<GameCharacter> call() throws Exception {
                //Thread.sleep(3000);
                EntityManager em = null;
                try {
                    em = HelloApplication.createEM();
                    List<GameCharacter> gamers =
                            gameCharacterService.getCharactersForGameWorld(currentGameWorld, em);
                    return FXCollections.observableArrayList(gamers);
                } finally {
                    if (em != null) em.close();
                }
            }
            @Override
            protected void succeeded() {
                gameCharactersPanel.setItems(getValue());
            }
            @Override
            protected void failed() {
                LOG.error("Exception occurred while loading characters", getException());
            }
        };
        handleTaskFailure(loadTask);
        FXUtils.bindUiToTask(
                loadTask,
                overlay,
                progressIndicator,
                gameCharactersPanel // disable this control while the task runs
        );

        executorService.submit(loadTask);
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
