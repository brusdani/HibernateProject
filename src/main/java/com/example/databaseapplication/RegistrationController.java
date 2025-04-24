package com.example.databaseapplication;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 * JavaFX controller for the registration form.
 */
public class RegistrationController {
    @FXML private TextField usernameField;
    @FXML private TextField passwordField;
    @FXML private TextField emailField;
    @FXML private Button registerNewUserButton;

    private final App app = App.getInstance();

    /**
     * Initializes the controller, grabbing the App singleton.
     */
    @FXML
    private void initialize() {

    }

    /**
     * Called when the user clicks the \"Register\" button.
     * Reads the fields and attempts to persist a new User.
     */
    @FXML
    public void registerNewUser() {
        String login    = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String email    = emailField.getText().trim();

        // basic non‐empty validation
        if (login.isEmpty() || password.isEmpty() || email.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "All fields are required.");
            return;
        }

        // delegate to the App service
        boolean success = app.registerNewUser(login, password, email);
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "User registered successfully!");
            usernameField.clear();
            passwordField.clear();
            emailField.clear();
        } else {
            showAlert(Alert.AlertType.ERROR,
                    "Registration failed: username may already exist.");
        }
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