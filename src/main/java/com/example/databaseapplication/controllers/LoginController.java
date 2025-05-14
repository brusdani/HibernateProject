package com.example.databaseapplication.controllers;

import com.example.databaseapplication.HelloApplication;
import com.example.databaseapplication.dao.UserDao;
import com.example.databaseapplication.model.User;
import com.example.databaseapplication.service.UserService;
import com.example.databaseapplication.session.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginController {
    private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);

    @FXML
    private TextField loginField;
    @FXML
    private TextField passwordField;
    @FXML
    private Label errorLabel;

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
    private void loginButtonClick(ActionEvent event) throws IOException {
        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();

        EntityManager em = null;

        try {
            em = HelloApplication.createEM();
            User authenticated = userService.authenticate(login, password, em);

            if(authenticated != null) {
                LOG.info("successful login");
                Session.setUser(authenticated);
                sceneController.changeScene(event, "character-selection.fxml");
            } else {
                LOG.info("wrong credentials");
                errorLabel.setText("Wrong username or password");
            }
        } catch (Exception e){
            LOG.error("Exception occured during sign in process ", e);
        }
    }

}
