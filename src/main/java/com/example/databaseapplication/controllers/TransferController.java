package com.example.databaseapplication.controllers;

import com.example.databaseapplication.HelloApplication;
import com.example.databaseapplication.dao.GameCharacterDao;
import com.example.databaseapplication.dao.GameWorldDao;
import com.example.databaseapplication.exceptions.BusinessException;
import com.example.databaseapplication.model.GameCharacter;
import com.example.databaseapplication.model.GameWorld;
import com.example.databaseapplication.model.User;
import com.example.databaseapplication.service.GameCharacterService;
import com.example.databaseapplication.service.GameWorldService;
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

public class TransferController extends BaseController {
    @FXML
    private Button transferButton;
    @FXML
    private Button backButton;
    @FXML
    private ListView<GameWorld> gameWorldPanel;
    @FXML
    private ListView<GameCharacter> gameCharactersPanel;

    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Rectangle overlay;
    @FXML
    private Label infoLabel;

    private GameWorldService gameWorldService;
    private GameCharacterService gameCharacterService;

    private ExecutorService executorService;

    private SceneController sceneController = new SceneController();
    private static final Logger LOG = LoggerFactory.getLogger(TransferController.class);


    @FXML
    private void initialize() {
        gameWorldService = new GameWorldService(new GameWorldDao(),new GameCharacterDao());
        gameCharacterService = new GameCharacterService(new GameCharacterDao());
        executorService = Executors.newSingleThreadExecutor();
        User currentUser = Session.getUser();

        gameWorldPanel.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        gameCharactersPanel.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        loadGameWorlds();
        loadCharacters();

        transferButton.disableProperty().bind(
                gameCharactersPanel.getSelectionModel().selectedItemProperty().isNull()
                        .or(gameWorldPanel.getSelectionModel().selectedItemProperty().isNull())
        );

    }
    private void loadGameWorlds() {
        Task<List<GameWorld>> task = new Task<List<GameWorld>>() {
            @Override
            protected List<GameWorld> call() throws Exception {
                EntityManager em = null;
                try {
                    em = HelloApplication.createEM();
                    return gameWorldService.getAllGameWorlds(em);
                } finally {
                    if (em != null) em.close();
                }
            }
            @Override
            protected void succeeded() {
                super.succeeded();
                ObservableList<GameWorld> items =
                        FXCollections.observableArrayList(getValue());
                gameWorldPanel.setItems(items);
            }

            @Override
            protected void failed() {
                super.failed();
                // Log & show an alert
                LOG.error("Exception occurred while loading game worlds", getException());
            }
        };
        handleTaskFailure(task);
        FXUtils.bindUiToTask(
                task,
                overlay,
                progressIndicator,
                List.of(gameWorldPanel),
                List.of(),
                gameWorldPanel.getSelectionModel().selectedItemProperty()
        );
        executorService.submit(task);
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
                            gameCharacterService.getAllCharacters(em);
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
    private void onTransferButtonClick(ActionEvent event) {
        GameCharacter selectedChar = gameCharactersPanel.getSelectionModel().getSelectedItem();
        if (selectedChar == null) {
            infoLabel.setText("You must select a character");
            return;
        }
        GameWorld selectedWorld = gameWorldPanel.getSelectionModel().getSelectedItem();
        if (selectedWorld == null) {
            infoLabel.setText("You must select a world");
            return;
        }
        infoLabel.setText("");
        GameCharacter characterCopy = selectedChar;
        Task<Void> transferTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(3000);
                EntityManager em = null;
                try {
                    // call your service; change to match your method signature
                    em = HelloApplication.createEM();
                    gameCharacterService.transferCharacter(
                            characterCopy,
                            selectedWorld,
                            em
                    );
                } finally {
                    em.close();
                }
                return null;
            }

            @Override
            protected void succeeded() {
                infoLabel.setText("Transfer successful!");
                LOG.info("Transfer successful");
                loadCharacters();
                loadGameWorlds();
            }

            @Override
            protected void failed() {
                Throwable ex = getException();
                LOG.error("Error during transfer", ex);
                if (ex instanceof BusinessException) {
                    infoLabel.setText(ex.getMessage());
                    loadGameWorlds();
                    loadCharacters();
                } else {
                    // database down, etc.
                    infoLabel.setText("Error: " + ex.getMessage());
                }
            }
        };
        handleTaskFailure(transferTask);
        FXUtils.bindUiToTask(
                transferTask,
                overlay,
                progressIndicator,
                List.of(gameCharactersPanel, gameWorldPanel, backButton),
                List.of(transferButton),
                gameCharactersPanel.getSelectionModel().selectedItemProperty()
        );
        executorService.submit(transferTask);
    }
    @FXML
    private void onBackButtonClick(ActionEvent event) throws IOException {
        sceneController.changeScene(event, "admin-panel.fxml");
    }
    @FXML
    private void onReloadButtonClick(){
        loadCharacters();
        loadGameWorlds();
        LOG.info("Lists reloaded");
    }

}
