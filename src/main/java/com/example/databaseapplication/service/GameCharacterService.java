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
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;
import java.util.ConcurrentModificationException;
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
                                            User currentUser,
                                            EntityManager em) {

        try {
            em.getTransaction().begin();
            if (gameCharacterDao.countByUser(currentUser, em) >= 4){
                LOG.info("Character limit reached");
                throw new BusinessException(
                  "You have reached the character limit"
                );
            }

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
    public GameCharacter transferCharacter(GameCharacter detachedCopy, GameWorld gameWorld, EntityManager em){
        try {
            em.getTransaction().begin();

            GameCharacter currentlyInDb = em.find(GameCharacter.class, detachedCopy.getId());
            if (currentlyInDb == null) {
                em.getTransaction().rollback();
                throw new ConcurrentModificationException(
                        "This character was deleted."
                );
            }
            GameCharacter managedChar = em.merge(detachedCopy);

            if (managedChar == null) {
                throw new BusinessException(
                        "Character not found (id=" + detachedCopy.getId() + ")"
                );
            }
            GameWorld managedWorld = em.find(
                    GameWorld.class,
                    gameWorld.getId()
            );
            if (managedWorld == null) {
                throw new BusinessException(
                        "Target world not found (name=" + gameWorld.getWorldName() + ")"
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

        } catch (OptimisticLockException ole) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new ConcurrentModificationException(
                    "This character was transfered by someone else.",
                    ole
            );
        } catch (BusinessException be) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw be;

        } catch (PersistenceException pe) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new DataAccessException("Database down during transfer", pe);
        }
    }

}
