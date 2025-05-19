package com.example.databaseapplication.dao;

import com.example.databaseapplication.model.GameCharacter;
import com.example.databaseapplication.model.GameWorld;
import com.example.databaseapplication.model.User;

import javax.persistence.EntityManager;
import java.util.List;

public class GameCharacterDao {
    public long countByUser(User user, EntityManager em) {
        String jpql = "SELECT COUNT(c) FROM GameCharacter c WHERE c.user = :user";
        return em.createQuery(jpql, Long.class)
                .setParameter("user", user)
                .getSingleResult();
    }
    public long countByWorld(GameWorld world, EntityManager em) {
        String jpql = "SELECT COUNT(c) FROM GameCharacter c WHERE c.gameWorld = :world";
        return em.createQuery(jpql, Long.class)
                .setParameter("world", world)
                .getSingleResult();
    }
    public GameCharacter saveCharacter(GameCharacter gameCharacter, EntityManager em){
        if(gameCharacter.getId() == null) {
            em.persist(gameCharacter);
        } else {
            gameCharacter = em.merge(gameCharacter);
        }
        return gameCharacter;
    }
    public List<GameCharacter> findByUser(User user, EntityManager em) {
        String jpql = "SELECT c FROM GameCharacter c WHERE c.user = :user";
        return em.createQuery(jpql, GameCharacter.class)
                .setParameter("user", user)
                .getResultList();
    }
    public List<GameCharacter> findAll(EntityManager em) {
        String jpql = "SELECT c FROM GameCharacter c";
        return em.createQuery(jpql, GameCharacter.class)
                .getResultList();
    }
    public List<GameCharacter> findByGameWorld(GameWorld world, EntityManager em) {
        String jpql = "SELECT c FROM GameCharacter c WHERE c.gameWorld = :world";
        return em.createQuery(jpql, GameCharacter.class)
                .setParameter("world", world)
                .getResultList();
    }
    public void deleteCharacter(GameCharacter character, EntityManager em) {
        GameCharacter managed = em.find(GameCharacter.class, character.getId());
        if (managed != null) {
            em.remove(managed);
        }
    }



}
