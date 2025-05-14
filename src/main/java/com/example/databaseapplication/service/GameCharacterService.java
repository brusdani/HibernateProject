package com.example.databaseapplication.service;

import com.example.databaseapplication.dao.GameCharacterDao;
import com.example.databaseapplication.dao.UserDao;
import com.example.databaseapplication.model.CharacterJob;
import com.example.databaseapplication.model.GameCharacter;
import com.example.databaseapplication.model.User;
import com.example.databaseapplication.model.UserType;
import com.example.databaseapplication.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;

public class GameCharacterService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private GameCharacterDao gameCharacterDao;

    public GameCharacterService(GameCharacterDao gameCharacterDao){
        this.gameCharacterDao = gameCharacterDao;
    }

    public GameCharacter createNewCharacter(String nickname, CharacterJob characterJob, EntityManager em) {
        GameCharacter gameCharacter = new GameCharacter();

        gameCharacter.setName(nickname);
        gameCharacter.setJob(characterJob);
        gameCharacter.setImageURL("mage1f.jpg");
        gameCharacter.setUser(Session.getUser());


        em.getTransaction().begin();
        gameCharacterDao.saveCharacter(gameCharacter, em);
        em.getTransaction().commit();
        return gameCharacter;
    }
}
