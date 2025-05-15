package com.example.databaseapplication.service;

import com.example.databaseapplication.dao.GameCharacterDao;
import com.example.databaseapplication.dao.GameWorldDao;
import com.example.databaseapplication.model.CharacterJob;
import com.example.databaseapplication.model.GameCharacter;
import com.example.databaseapplication.model.GameWorld;
import com.example.databaseapplication.model.User;
import com.example.databaseapplication.session.Session;

import javax.persistence.EntityManager;
import java.util.List;

public class GameWorldService {

    private GameWorldDao gameWorldDao;

    public GameWorldService(GameWorldDao gameWorldDao) {
        this.gameWorldDao = gameWorldDao;
    }

    public GameWorld createNewWorld(String worldName, String description, EntityManager em) {
        GameWorld gameWorld = new GameWorld();

        gameWorld.setWorldName(worldName);
        gameWorld.setWorldDescription(description);

        em.getTransaction().begin();
        gameWorldDao.saveGameWorld(gameWorld, em);
        em.getTransaction().commit();
        return gameWorld;
    }
    public List<GameWorld> getAllGameWorlds(EntityManager em) {
        return gameWorldDao.getAllGameWorld(em);
    }
}
