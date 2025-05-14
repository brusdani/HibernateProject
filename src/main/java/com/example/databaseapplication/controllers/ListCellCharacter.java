package com.example.databaseapplication.controllers;

import com.example.databaseapplication.model.GameCharacter;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.InputStream;

public class ListCellCharacter extends ListCell<GameCharacter> {
    private final VBox content;
    private final Text nameText;
    private final ImageView portraitView;
    private final Text classText;

    public ListCellCharacter() {
        super();

        // 1) Create the controls
        nameText     = new Text();
        portraitView = new ImageView();
        classText    = new Text();

        // 2) Configure image size
        portraitView.setFitWidth(120);
        portraitView.setFitHeight(120);
        portraitView.setPreserveRatio(true);

        // 3) Put them in a VBox, center aligned, with spacing
        content = new VBox(8, nameText, portraitView, classText);
        content.getStyleClass().add("character-cell");   // CSS hook
        content.setStyle("-fx-alignment: center;");      // center all children
    }

    @Override
    protected void updateItem(GameCharacter character, boolean empty) {
        super.updateItem(character, empty);

        if (empty || character == null) {
            setGraphic(null);
            return;
        }

        // Fill in the data
        nameText.setText(character.getName());
        classText.setText("Class: " + character.getJob());

        // Load portrait (falling back to placeholder)
        String path = character.getImageURL();  // should be "images/foo.png"
        Image img = loadImage(path, "/images/placeholder.png");
        portraitView.setImage(img);

        setGraphic(content);
    }

    private Image loadImage(String dbPath, String fallbackResource) {
        Image img = null;
        if (dbPath != null && !dbPath.isBlank()) {
            InputStream is = getClass().getResourceAsStream("/" + dbPath);
            if (is != null) {
                img = new Image(is, 100, 100, true, true);
            }
        }
        if (img == null) {
            InputStream is = getClass().getResourceAsStream(fallbackResource);
            img = new Image(is, 100, 100, true, true);
        }
        return img;
    }
}
