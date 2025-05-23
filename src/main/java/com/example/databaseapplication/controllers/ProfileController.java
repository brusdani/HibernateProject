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
    private Label infoLabel;
    @FXML
    private Button saveButton;
    @FXML
    private Button deleteButton;
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
        //passwordField.setText(currentUser.getPassword());
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
                    LOG.info("User deleted");
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
        final String login = usernameField.getText().trim();
        final String pwd   = passwordField.getText().trim();
        final String email = emailField.getText().trim();
        infoLabel.setText("");

        String validationError = validateInput(login, pwd, email);
        if (validationError != null) {
            infoLabel.setText(validationError);
            return;
        }

        User sessionUser = Session.getUser();
        if (sessionUser == null) return;

        User userCopy = new User();
        userCopy.setId(sessionUser.getId());
        userCopy.setLogin(login);
        userCopy.setEmail(email);
        if(!pwd.isEmpty()){
            userCopy.setPassword(pwd);
        }


        Task<User> saveTask = new Task<>() {
            @Override
            protected User call() throws Exception {
                Thread.sleep(5000);
                EntityManager em = null;
                try {
                    em = HelloApplication.createEM();
                    return userService.updateUser(userCopy, em);
                } finally {
                    if (em != null) em.close();
                }
            }
            @Override
            protected void succeeded() {
                User result = getValue();
                if (result != null) {
                    infoLabel.setText("Update successful");
                    Session.setUser(result);
                    LOG.info("User Data updated");
                } else {
                    // null means create/update failed silently
                    LOG.error("User save returned null");
                    infoLabel.setText("Save failed");
                }
            }
            @Override
            protected void failed() {
                LOG.error("Exception occurred while saving world", getException());
                infoLabel.setText("Error: " + getException().getMessage());
            }
        };
        handleTaskFailure(saveTask);
        FXUtils.bindUiToTask(
                saveTask,
                overlay,
                progressIndicator,
                cancelButton,
                deleteButton,
                saveButton
        );
        executorService.submit(saveTask);
    }

    private String validateInput(String login, String pwd, String email) {
        if (login.isEmpty() || email.isEmpty()) {
            return "All fields apart from password need to be filled";
        }
        if (login.length() < 3 || login.length() > 20) {
            return "Username must be 3–20 characters long.";
        }
        if (!login.matches("[A-Za-z0-9_]+")) {
            return "Username may only contain letters, digits, and underscores.";
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            return "Please enter a valid email address.";
        }
        if (!pwd.isEmpty() && pwd.length() < 8) {
            return "Password must be at least 8 characters.";
        }
        return null;
    }
}
