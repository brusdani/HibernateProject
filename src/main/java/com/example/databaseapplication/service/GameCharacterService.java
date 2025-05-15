package com.example.databaseapplication.service;

import com.example.databaseapplication.dao.GameCharacterDao;
import com.example.databaseapplication.dao.UserDao;
import com.example.databaseapplication.model.*;
import com.example.databaseapplication.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.util.List;

public class GameCharacterService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private GameCharacterDao gameCharacterDao;

    public GameCharacterService(GameCharacterDao gameCharacterDao){
        this.gameCharacterDao = gameCharacterDao;
    }

    public GameCharacter createNewCharacter(String nickname, CharacterJob characterJob, GameWorld world, EntityManager em) {
        GameCharacter gameCharacter = new GameCharacter();

        gameCharacter.setName(nickname);
        gameCharacter.setJob(characterJob);
        gameCharacter.setImageURL(characterJob.getImagePath());
        gameCharacter.setUser(Session.getUser());
        gameCharacter.setGameWorld(world);


        em.getTransaction().begin();
        gameCharacterDao.saveCharacter(gameCharacter, em);
        em.getTransaction().commit();
        return gameCharacter;
    }
    public List<GameCharacter> getCharactersForUser(User user, EntityManager em) {
        return gameCharacterDao.findByUser(user, em);
    }
    public List<GameCharacter> getCharactersForGameWorld(GameWorld world, EntityManager em) {
        return gameCharacterDao.findByGameWorld(world, em);
    }
    public void deleteCharacter(GameCharacter character, EntityManager em) {
        em.getTransaction().begin();
        gameCharacterDao.deleteCharacter(character, em);
        em.getTransaction().commit();
    }
}
