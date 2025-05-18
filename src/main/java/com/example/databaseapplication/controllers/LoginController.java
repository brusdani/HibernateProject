package com.example.databaseapplication.controllers;

import com.example.databaseapplication.HelloApplication;
import com.example.databaseapplication.dao.UserDao;
import com.example.databaseapplication.model.User;
import com.example.databaseapplication.model.UserType;
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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginController extends BaseController {
    private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);

    @FXML
    private TextField loginField;
    @FXML
    private TextField passwordField;
    @FXML
    private Label errorLabel;
    @FXML
    private Button loginButton;
    @FXML
    private Button registrationButton;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Rectangle overlay;

    private UserService userService;

    private ExecutorService executorService;

    private SceneController sceneController = new SceneController();
    @FXML
    private void initialize(){
        userService = new UserService(new UserDao());
        executorService = Executors.newSingleThreadExecutor();
    }
    @FXML
    private void registrationButtonClick(ActionEvent event) throws IOException {
        sceneController.changeScene(event,"registration.fxml");
    }

    @FXML
    private void loginButtonClick(ActionEvent event) {
        final String username = loginField.getText().trim();
        final String pwd      = passwordField.getText().trim();
        errorLabel.setText("");
        Task<User> loginTask = new Task<>() {
            @Override
            protected User call() throws Exception {
                Thread.sleep(3000);
                EntityManager em = null;
                try {
                    em = HelloApplication.createEM();
                    return userService.authenticate(username, pwd, em);
                } finally {
                    if (em != null) em.close();
                }
            }
            @Override
            protected void succeeded() {
                User authenticated = getValue();
                if (authenticated != null) {
                    Session.setUser(authenticated);
                    try {
                        String fxml = (authenticated.getType() == UserType.REGULAR)
                                ? "character-selection.fxml"
                                : "admin-panel.fxml";
                        sceneController.changeScene(event, fxml);
                    } catch (IOException ex) {
                        errorLabel.setText("Unexpected navigation error");
                    }
                } else {
                    errorLabel.setText("Wrong username or password");
                }
            }
            @Override
            protected void failed() {
                errorLabel.setText("Task hasn't gone through");
            }
        };
        handleTaskFailure(loginTask);
        FXUtils.bindUiToTask(
                loginTask,
                overlay,
                progressIndicator,
                loginButton,
                registrationButton
        );
        executorService.submit(loginTask);
    }
}
