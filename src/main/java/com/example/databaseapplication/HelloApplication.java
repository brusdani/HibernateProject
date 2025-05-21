package com.example.databaseapplication;

import com.example.databaseapplication.config.Configuration;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import java.io.IOException;

public class HelloApplication extends Application {
    private static final Logger LOG = LoggerFactory.getLogger(HelloApplication.class);

    private static EntityManagerFactory EMF;
    private boolean dbUp;


    public static EntityManager createEM(){
        return EMF.createEntityManager();
    }
    @Override
    public void init() {
        try {
            Configuration configuration = new Configuration();
            LOG.info("konfigurace a: {}", configuration.getValue("db.user"));
            EMF = Persistence.createEntityManagerFactory("punit", configuration.getDbCredentials());

            dbUp = true;
            LOG.info("Connected to database successfully.");
        } catch (PersistenceException ex) {
            LOG.error("Unable to connect to database; will show error screen.", ex);
            dbUp = false;
        }
    }
    @Override
    public void start(Stage stage) throws IOException {
        String fxml = dbUp ? "controllers/login.fxml" : "controllers/dbdown.fxml";
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxml));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.setOnCloseRequest((WindowEvent event) -> {
            Platform.exit();      // Shutdown JavaFX runtime
            System.exit(0);       // Stop JVM entirely
        });
        stage.show();
    }

    public static void main(String[] args) {
        launch();
        LOG.info("Application terminated.");
    }
    @Override
    public void stop() {
        if (EMF != null && EMF.isOpen()) {
            EMF.close();
            LOG.info("EntityManagerFactory closed.");
        }
        LOG.info("Application terminated.");
    }
}