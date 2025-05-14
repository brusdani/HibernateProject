package com.example.databaseapplication;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.IOException;

public class HelloApplication extends Application {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    private static EntityManagerFactory EMF;


    public static EntityManager createEM(){
        return EMF.createEntityManager();
    }
    public static App app;
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("controllers/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        EMF = Persistence.createEntityManagerFactory("punit");
        launch();
        EMF.close();
        LOG.info("Application terminated.");
    }
}