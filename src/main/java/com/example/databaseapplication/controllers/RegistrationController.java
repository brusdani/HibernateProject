package com.example.databaseapplication.controllers;

import com.example.databaseapplication.HelloApplication;
import com.example.databaseapplication.dao.UserDao;
import com.example.databaseapplication.exceptions.DataAccessException;
import com.example.databaseapplication.model.User;
import com.example.databaseapplication.service.UserService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.shape.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * JavaFX controller for the registration form.
 */
public class RegistrationController extends BaseController {
    private static final Logger LOG = LoggerFactory.getLogger(RegistrationController.class);

    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField emailField;
    @FXML
    private Button registerNewUserButton;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Rectangle overlay;
    @FXML
    private Label errorLabel;
    @FXML
    private Button loginButton;

    private SceneController sceneController = new SceneController();

    private UserService userService;

    private ExecutorService executorService;

    public RegistrationController() {

    }

    /**
     * Initializes the controller, grabbing the App singleton.
     */
    @FXML
    private void initialize() {
        userService = new UserService(new UserDao());
        executorService = Executors.newSingleThreadExecutor();
    }

    @FXML
    public void registerNewUser(ActionEvent event) {
        final String login = usernameField.getText().trim();
        final String pwd   = passwordField.getText().trim();
        final String email = emailField.getText().trim();
        errorLabel.setText("");

        if (login.isEmpty() || pwd.isEmpty() || email.isEmpty()) {
            errorLabel.setText("All fields need to be filled");
            return;
        }

        Task<User> registerTask = new Task<>() {
            @Override
            protected User call() throws Exception {
                Thread.sleep(3000);
                EntityManager em = null;
                try {
                    em = HelloApplication.createEM();
                    return userService.registerNewUser(login, pwd, email, em);
                } finally {
                    if (em != null) em.close();
                }
            }
            @Override
            protected void succeeded() {
                User created = getValue();
                if (created != null) {
                    Alert info = new Alert(Alert.AlertType.INFORMATION,
                            "User registered successfully!",
                            ButtonType.OK);
                    info.showAndWait();
                    try {
                        sceneController.changeScene(event, "login.fxml");
                    } catch (Exception ex) {
                        errorLabel.setText("Navigation error");
                    }
                } else {
                    errorLabel.setText("Registration failed: username may already exist");
                }
            }
            @Override
            protected void failed() {
                Throwable ex = getException();
                if (!(ex instanceof DataAccessException)) {
                    errorLabel.setText("Registration error: " + ex.getMessage());
                }
            }
        };
        handleTaskFailure(registerTask);
        FXUtils.bindUiToTask(
                registerTask,
                overlay,
                progressIndicator,
                registerNewUserButton,
                loginButton
        );
        executorService.submit(registerTask);
    }

    private void showAlert(Alert.AlertType type, String msg) {
        Alert a = new Alert(type, msg, ButtonType.OK);
        a.showAndWait();
    }
    @FXML
    private void onLoginButtonClick(ActionEvent event) throws IOException {
        sceneController.changeScene(event,"login.fxml");
    }

}