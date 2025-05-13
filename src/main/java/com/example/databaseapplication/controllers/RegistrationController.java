package com.example.databaseapplication.controllers;

import com.example.databaseapplication.HelloApplication;
import com.example.databaseapplication.dao.UserDao;
import com.example.databaseapplication.model.User;
import com.example.databaseapplication.service.UserService;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * JavaFX controller for the registration form.
 */
public class RegistrationController {
    private static final Logger LOG = LoggerFactory.getLogger(RegistrationController.class);

    @FXML private TextField usernameField;
    @FXML private TextField passwordField;
    @FXML private TextField emailField;
    @FXML private Button registerNewUserButton;

    private SceneController sceneController = new SceneController();

    private UserService userService;

    private ExecutorService executorService;

    public RegistrationController() {

    }
    // setter for your UserService
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    // setter for the ExecutorService
    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    /**
     * Initializes the controller, grabbing the App singleton.
     */
    @FXML
    private void initialize() {
        userService = new UserService(new UserDao());
        executorService = Executors.newSingleThreadExecutor();
    }

    /**
     * Called when the user clicks the \"Register\" button.
     * Reads the fields and attempts to persist a new User.
     */
    @FXML
    public void registerNewUser(ActionEvent event) {
        String login = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String email = emailField.getText().trim();

        // basic non‐empty validation
        if (login.isEmpty() || password.isEmpty() || email.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "All fields are required.");
            return;
        }

        EntityManager em = null;

        try {
            em = HelloApplication.createEM();
            User newUser = userService.registerNewUser(login, password, email, em);
            if (newUser != null) {
                showAlert(Alert.AlertType.INFORMATION, "User registered successfully!");
                usernameField.clear();
                passwordField.clear();
                emailField.clear();

                sceneController.changeScene(event,"login.fxml");
            } else {
                showAlert(Alert.AlertType.ERROR,
                        "Registration failed: username may already exist.");
            }
        } catch (Exception e) {
            LOG.error("Exception occured while reloading data", e);
        }
    }
    @FXML
    private void onLoginButtonClick(ActionEvent event) throws IOException {
        sceneController.changeScene(event,"login.fxml");
    }



    /**
     * Utility method to pop up a simple alert.
     */
    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}