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

public class SelectionController extends BaseController {
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
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Rectangle overlay;

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
    private void onDeleteButtonClick() {
        GameCharacter picked = characterPanel.getSelectionModel().getSelectedItem();
        if (picked == null) return;
        Task<Void> deleteTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                //Thread.sleep(5000);
                EntityManager em = null;
                try {
                    em = HelloApplication.createEM();
                    gameCharacterService.deleteCharacter(picked, em);
                } finally {
                    if (em != null) em.close();
                }
                return null;
            }
            @Override
            protected void succeeded() {
                characterPanel.refresh();
                loadCharacters();
            }
            @Override
            protected void failed() {
                LOG.error("Exception occurred while deleting character", getException());
            }
        };
        handleTaskFailure(deleteTask);

        FXUtils.bindUiToTask(
                deleteTask,
                overlay,
                progressIndicator,
                List.of(characterPanel),
                List.of(selectButton, deleteButton),
                characterPanel.getSelectionModel().selectedItemProperty()
        );
        executorService.submit(deleteTask);
    }

    private void loadCharacters() {
        Task<ObservableList<GameCharacter>> loadTask = new Task<>() {
            @Override
            protected ObservableList<GameCharacter> call() throws Exception {
                //Thread.sleep(5000);
                EntityManager em = null;
                try {
                    em = HelloApplication.createEM();
                    User currentUser = Session.getUser();
                    List<GameCharacter> roster =
                            gameCharacterService.getCharactersForUser(currentUser, em);
                    currentUser.setUserCharacters(roster);
                    return FXCollections.observableArrayList(roster);
                } finally {
                    if (em != null) em.close();
                }
            }

            @Override
            protected void succeeded() {
                ObservableList<GameCharacter> items = getValue();
                characterPanel.setItems(items);
                createButton.setDisable(items.size() >= 3);
                welcomeLabel.setText("Welcome " + Session.getUser().getLogin());
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
                progressIndicator, List.of(characterPanel),
                        List.of(selectButton, deleteButton),
                characterPanel.getSelectionModel().selectedItemProperty()
        );
        executorService.submit(loadTask);
    }

    @FXML
    private void onLogoutButtonClick(ActionEvent event) throws IOException {
        Session.clear();
        sceneController.changeScene(event,"login.fxml");
    }


}
