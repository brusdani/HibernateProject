package com.example.databaseapplication.controllers;

import com.example.databaseapplication.exceptions.DataAccessException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class BaseController {
    protected final ExecutorService executorService =
            Executors.newSingleThreadExecutor();


    protected <T> void handleTaskFailure(Task<T> task) {
        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            // Ensure UI work happens on the FX thread
            Platform.runLater(() -> {
                if (ex instanceof DataAccessException) {
                    showAlert("Database Error",
                            "Lost connection to the database. Please try again later.");
                } else {
                    showAlert("Unexpected Error", ex.getMessage());
                }
            });
        });
    }

    /** Utility to show a simple alert dialog */
    protected void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}

