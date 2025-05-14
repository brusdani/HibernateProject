package com.example.databaseapplication.controllers;

import com.example.databaseapplication.model.GameCharacter;
import com.example.databaseapplication.model.User;
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

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;

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

    private ExecutorService executorService;

    private SceneController sceneController = new SceneController();

    @FXML
    private void initialize(){
        User currentUser = Session.getUser();
        welcomeLabel.setText("Welcome "+ currentUser.getLogin());

        List<GameCharacter> roster = currentUser.getUserCharacters();

        ObservableList<GameCharacter> items =
                FXCollections.observableArrayList(roster);

        characterPanel.setItems(items);
        characterPanel.setCellFactory(list -> new ListCellCharacter());
        characterPanel.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // 2) enable the “Start” button whenever there’s a selection
        selectButton.disableProperty().bind(
                characterPanel.getSelectionModel().selectedItemProperty().isNull());
        deleteButton.disableProperty().bind(
                characterPanel.getSelectionModel().selectedItemProperty().isNull());

        if (currentUser.getUserCharacters().size() == 3){
            createButton.setDisable(true);
        }

    }
    @FXML
    private void onCreateButtonClick(ActionEvent event) throws IOException {
        sceneController.changeScene(event,"character-creation.fxml");
    }
    @FXML
    private void setSelectButtonClick(ActionEvent event) throws IOException {
        GameCharacter picked = characterPanel.getSelectionModel().getSelectedItem();

        if(picked != null) {
            Session.setCurrentGameCharacter(picked);
            sceneController.changeScene(event,"game-interface.fxml");
        }

    }


}
