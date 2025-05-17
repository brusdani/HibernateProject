package com.example.databaseapplication.dao;

import com.example.databaseapplication.model.GameCharacter;
import com.example.databaseapplication.model.GameWorld;
import com.example.databaseapplication.model.User;

import javax.persistence.EntityManager;
import java.util.List;

public class GameWorldDao {
    public GameWorld saveGameWorld(GameWorld gameWorld, EntityManager em){
        if(gameWorld.getId() == null) {
            em.persist(gameWorld);
        } else {
            gameWorld = em.merge(gameWorld);
        }
        return gameWorld;
    }
    public List<GameWorld> getAllGameWorld(EntityManager em){
        return em.createQuery("from GameWorld ", GameWorld.class)
                .getResultList();
    }
    public void deleteGameWorld(GameWorld gameWorld, EntityManager em) {
        GameWorld managed = em.find(GameWorld.class, gameWorld.getId());
        if (managed != null) {
            em.remove(managed);
        }
    }

}
