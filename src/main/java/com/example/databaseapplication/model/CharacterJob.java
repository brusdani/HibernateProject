package com.example.databaseapplication.model;

public enum CharacterJob {
    WARRIOR("images/warrior1f.jpg"),
    MAGE   ("images/mage1f.jpg"),
    ROGUE  ("images/rogue1f.png");

    private final String imagePath;
    CharacterJob(String imagePath) {
        this.imagePath = imagePath;
    }
    public String getImagePath() {
        return imagePath;
    }
}
