package com.example.databaseapplication.service;

import com.example.databaseapplication.dao.GameCharacterDao;
import com.example.databaseapplication.dao.UserDao;
import com.example.databaseapplication.exceptions.BusinessException;
import com.example.databaseapplication.exceptions.DataAccessException;
import com.example.databaseapplication.model.*;
import com.example.databaseapplication.session.Session;
import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.util.List;

public class GameCharacterService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private GameCharacterDao gameCharacterDao;

    public GameCharacterService(GameCharacterDao gameCharacterDao){
        this.gameCharacterDao = gameCharacterDao;
    }

    public GameCharacter createNewCharacter(String nickname,
                                            CharacterJob characterJob,
                                            GameWorld world,
                                            EntityManager em) {

        try {
            em.getTransaction().begin();

            GameWorld managedWorld = em.find(GameWorld.class, world.getId());
            if (managedWorld == null) {
                LOG.info("GameWorld no longer exists");
                throw new BusinessException(
                        "Selected world no longer exists. Please choose another."
                );
            }

            GameCharacter gameCharacter = new GameCharacter();
            gameCharacter.setName(nickname);
            gameCharacter.setJob(characterJob);
            gameCharacter.setImageURL(characterJob.getImagePath());
            gameCharacter.setUser(Session.getUser());
            gameCharacter.setGameWorld(managedWorld);


            gameCharacterDao.saveCharacter(gameCharacter, em);
            em.getTransaction().commit();
            return gameCharacter;
        } catch (BusinessException be) {
            LOG.info("BusinessException caught");
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw be;
        } catch (PersistenceException ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new DataAccessException("Database down", ex);
        }
    }
    public List<GameCharacter> getCharactersForUser(User user, EntityManager em) {
        try {
            return gameCharacterDao.findByUser(user, em);
        } catch (PersistenceException ex) {
            throw new DataAccessException("Database down", ex);
        }
    }
    public List<GameCharacter> getCharactersForGameWorld(GameWorld world, EntityManager em) {
        try {
            return gameCharacterDao.findByGameWorld(world, em);
        } catch (PersistenceException ex) {
            throw new DataAccessException("Database down", ex);
        }
    }
    public List<GameCharacter> getAllCharacters(EntityManager em) {
        try {
            return gameCharacterDao.findAll(em);
        } catch (PersistenceException ex) {
            throw new DataAccessException("Database down", ex);
        }
    }
    public void deleteCharacter(GameCharacter character, EntityManager em) {
        try {
            em.getTransaction().begin();
            gameCharacterDao.deleteCharacter(character, em);
            em.getTransaction().commit();
        } catch (PersistenceException ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new DataAccessException("Database down", ex);
        }
    }
    public GameCharacter transferCharacter(GameCharacter gameCharacter, GameWorld gameWorld, EntityManager em){
        try {
            em.getTransaction().begin();

            GameCharacter managedChar = em.find(
                    GameCharacter.class,
                    gameCharacter.getId()
            );
            if (managedChar == null) {
                throw new BusinessException(
                        "Character not found (id=" + gameCharacter.getId() + ")"
                );
            }
            GameWorld managedWorld = em.find(
                    GameWorld.class,
                    gameWorld.getId()
            );
            if (managedWorld == null) {
                throw new BusinessException(
                        "Target world not found (id=" + gameWorld.getId() + ")"
                );
            }
            if (managedChar.getGameWorld().getId().equals(managedWorld.getId())) {
                throw new BusinessException(
                        String.format(
                                "Character '%s' is already in world '%s'",
                                managedChar.getName(),
                                managedWorld.getWorldName()
                        )
                );
            }
            managedChar.setGameWorld(managedWorld);
            gameCharacterDao.saveCharacter(managedChar, em);
            em.getTransaction().commit();
            return managedChar;

        } catch (BusinessException be) {
            // rollback on domain-level problems so we don’t leave the TX open
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw be;

        } catch (PersistenceException pe) {
            // rollback & wrap DB-level errors
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new DataAccessException("Database down during transfer", pe);
        }
    }

}
