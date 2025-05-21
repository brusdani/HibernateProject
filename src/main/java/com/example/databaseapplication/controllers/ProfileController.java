package com.example.databaseapplication.controllers;

import com.example.databaseapplication.HelloApplication;
import com.example.databaseapplication.dao.GameCharacterDao;
import com.example.databaseapplication.dao.GameWorldDao;
import com.example.databaseapplication.dao.UserDao;
import com.example.databaseapplication.model.GameCharacter;
import com.example.databaseapplication.model.GameWorld;
import com.example.databaseapplication.model.User;
import com.example.databaseapplication.service.GameWorldService;
import com.example.databaseapplication.service.UserService;
import com.example.databaseapplication.session.Session;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.shape.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileController extends BaseController {

    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField emailField;
    @FXML
    private Button cancelButton;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Rectangle overlay;
    @FXML
    private Label errorLabel;
    @FXML
    private Button saveButton;
    private static final Logger LOG = LoggerFactory.getLogger(ProfileController.class);


    private SceneController sceneController = new SceneController();

    private UserService userService;

    private ExecutorService executorService;

    @FXML
    private void initialize(){
        userService = new UserService(new UserDao());
        executorService = Executors.newSingleThreadExecutor();

        User currentUser = Session.getUser();
        usernameField.setText(currentUser.getLogin());
        passwordField.setText(currentUser.getPassword());
        emailField.setText(currentUser.getEmail());
    }

    @FXML
    private void onCancelButtonClick(ActionEvent event) throws IOException {
        sceneController.changeScene(event, "character-selection.fxml");
    }
    @FXML
    private void onDeleteButtonClick(ActionEvent event){
            User picked = Session.getUser();
            if (picked == null) return;
            Task<Void> deleteTask = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    Thread.sleep(5000);
                    EntityManager em = null;
                    try {
                        em = HelloApplication.createEM();
                        userService.deleteUser(picked, em);
                    } finally {
                        if (em != null) em.close();
                    }
                    return null;
                }
                @Override
                protected void succeeded() {
                    try {
                        sceneController.changeScene(event, "login.fxml");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
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
                    progressIndicator
            );
            executorService.submit(deleteTask);
        }

    @FXML
    private void onSaveButtonClick(){

    }
}
