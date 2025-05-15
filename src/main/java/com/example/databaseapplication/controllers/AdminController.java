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
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
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
}
