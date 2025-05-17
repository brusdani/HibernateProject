package com.example.databaseapplication.controllers;

import com.example.databaseapplication.HelloApplication;
import com.example.databaseapplication.dao.GameCharacterDao;
import com.example.databaseapplication.dao.GameWorldDao;
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
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.shape.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminController {

    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button createButton;
    @FXML
    private ListView<GameWorld> gameWorldPanel;
    @FXML
    private Label welcomeLabel;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML private Rectangle overlay;
    @FXML
    private Label pleaseWaitLabel;

    private GameWorldService gameWorldService;

    private ExecutorService executorService;

    private SceneController sceneController = new SceneController();
    private static final Logger LOG = LoggerFactory.getLogger(AdminController.class);



    @FXML
    private void initialize() {
        gameWorldService = new GameWorldService(new GameWorldDao());
        executorService = Executors.newSingleThreadExecutor();
        User currentUser = Session.getUser();
        welcomeLabel.setText("Welcome " + currentUser.getLogin());

        gameWorldPanel.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        loadGameWorlds();

        editButton.disableProperty().bind(
                gameWorldPanel.getSelectionModel().selectedItemProperty().isNull());
        deleteButton.disableProperty().bind(
                gameWorldPanel.getSelectionModel().selectedItemProperty().isNull());
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
        FXUtils.bindUiToTask(
                task,
                overlay,
                progressIndicator,
                List.of(createButton, gameWorldPanel),
                List.of(editButton, deleteButton),
                gameWorldPanel.getSelectionModel().selectedItemProperty()
        );
        executorService.submit(task);
    }
    @FXML
    private void onCreateButtonClick(ActionEvent event) throws IOException {
        sceneController.changeScene(event,"gameworld-form.fxml");
    }
    @FXML
    private void onEditButtonClick(ActionEvent event) throws IOException {
        GameWorld picked = gameWorldPanel.getSelectionModel().getSelectedItem();
        Session.setCurrentGameWorld(picked);
        sceneController.changeScene(event,"gameworld-form.fxml");
    }


    @FXML
    private void onDeleteButtonClick(ActionEvent event) {
        // Grab selection on FX thread
        final GameWorld picked = gameWorldPanel.getSelectionModel().getSelectedItem();
        if (picked == null) {
            // optionally show a warning: “Please select something to delete”
            return;
        }
        Task<Void> deleteTask = new Task<>() {

            @Override
            protected Void call() throws Exception {
                Thread.sleep(15000);
                EntityManager em = null;
                try {
                    em = HelloApplication.createEM();
                    gameWorldService.deleteGameWorld(picked, em);
                } finally {
                    if (em != null) em.close();
                }
                return null;
            }
            @Override
            protected void succeeded() {
                super.succeeded();
                // This runs on the FX thread:
                gameWorldPanel.refresh();
                loadGameWorlds();   // reload list after delete
            }
            @Override
            protected void failed() {
                super.failed();
                // Also on FX thread: log and show alert
                Throwable ex = getException();
                LOG.error("Exception occurred while deleting game world", ex);
            }
        };
        FXUtils.bindUiToTask(
                deleteTask,
                overlay,
                progressIndicator,
                List.of(createButton, gameWorldPanel),
                List.of(editButton, deleteButton),
                gameWorldPanel.getSelectionModel().selectedItemProperty()
        );
        executorService.submit(deleteTask);
    }

    @FXML
    private void onLogoutButtonClick(ActionEvent event) throws IOException {
        Session.clear();
        sceneController.changeScene(event,"login.fxml");
    }
}
