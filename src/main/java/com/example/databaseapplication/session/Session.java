package com.example.databaseapplication.session;

import com.example.databaseapplication.model.GameCharacter;
import com.example.databaseapplication.model.GameWorld;
import com.example.databaseapplication.model.User;

public class Session {
    private static User currentUser;

    private static GameCharacter currentGameCharacter;

    private static GameWorld currentGameWorld;

    private Session() {

    }

    public static void setUser(User user) {
        currentUser = user;
    }

    public static User getUser() {
        return currentUser;
    }

    public static void setCurrentGameCharacter(GameCharacter gameCharacter)
    {
        currentGameCharacter = gameCharacter;
    }

    public static GameCharacter getCurrentGameCharacter() {
        return currentGameCharacter;
    }

    public static GameWorld getCurrentGameWorld() {
        return currentGameWorld;
    }

    public static void setCurrentGameWorld(GameWorld currentGameWorld) {
        Session.currentGameWorld = currentGameWorld;
    }
    public static void clear() {
        currentUser = null;
        currentGameCharacter = null;
        currentGameWorld = null;
    }
}
