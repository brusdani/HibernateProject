package com.example.databaseapplication.dao;

import com.example.databaseapplication.model.GameCharacter;
import com.example.databaseapplication.model.User;

import javax.persistence.EntityManager;

public class GameCharacterDao {
    public long countByUser(User user, EntityManager em) {
        String jpql = "SELECT COUNT(c) FROM GameCharacter c WHERE c.user = :user";
        return em.createQuery(jpql, Long.class)
                .setParameter("user", user)
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


}
