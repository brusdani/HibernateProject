package com.example.databaseapplication.service;

import com.example.databaseapplication.dao.GameCharacterDao;
import com.example.databaseapplication.dao.GameWorldDao;
import com.example.databaseapplication.exceptions.DataAccessException;
import com.example.databaseapplication.model.GameCharacter;
import com.example.databaseapplication.model.GameWorld;

import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;
import org.hibernate.StaleObjectStateException;
import java.util.ConcurrentModificationException;
import java.util.List;

public class GameWorldService {

    private final GameWorldDao gameWorldDao;
    private final GameCharacterDao gameCharacterDao;

    public GameWorldService(GameWorldDao gameWorldDao, GameCharacterDao gameCharacterDao) {
        this.gameWorldDao = gameWorldDao;
        this.gameCharacterDao = gameCharacterDao;
    }

    public GameWorld createNewWorld(String worldName, String description, EntityManager em) {
        GameWorld gameWorld = new GameWorld();
        gameWorld.setWorldName(worldName);
        gameWorld.setWorldDescription(description);

        try {
            em.getTransaction().begin();
            gameWorldDao.saveGameWorld(gameWorld, em);
            em.getTransaction().commit();
            return gameWorld;
        } catch (PersistenceException ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new DataAccessException("Database down", ex);
        }
    }

    public GameWorld updateWorld(GameWorld detachedCopy, EntityManager em) {
        try {
            em.getTransaction().begin();

            GameWorld currentlyInDb = em.find(GameWorld.class, detachedCopy.getId());
            if (currentlyInDb == null) {
                em.getTransaction().rollback();
                throw new ConcurrentModificationException(
                        "This world was deleted by someone else."
                );
            }
            GameWorld managed = em.merge(detachedCopy);
            em.getTransaction().commit();
            return managed;

        } catch (OptimisticLockException ole) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new ConcurrentModificationException(
                    "This world was changed by someone else. Please reload and try again.",
                    ole
            );

        } catch (StaleObjectStateException sose) {
            // catch Hibernate-specific stale-state error
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new ConcurrentModificationException(
                    "This world was changed by someone else. Please reload and try again.",
                    sose
            );

        } catch (RollbackException rex) {
            // handle version-mismatch wrapped in a RollbackException
            if (rex.getCause() instanceof OptimisticLockException) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                throw new ConcurrentModificationException(
                        "This world was changed by someone else. Please reload and try again.",
                        rex.getCause()
                );
            }
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw rex;

        } catch (PersistenceException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new DataAccessException("Database is currently down", ex);
        }
    }
    public void deleteGameWorld(GameWorld gameWorld, EntityManager em) {
        try {
            em.getTransaction().begin();

            GameWorld managed = em.find(GameWorld.class, gameWorld.getId());
            if (managed == null) {
                throw new IllegalArgumentException(
                        "GameWorld not found (id=" + gameWorld.getId() + ")"
                );
            }

            long count = gameCharacterDao.countByWorld(managed, em);
            if (count > 0) {
                throw new IllegalStateException(
                        "Cannot delete GameWorld; it has " + count + " character(s)."
                );
            }

            gameWorldDao.deleteGameWorld(gameWorld, em);
            em.getTransaction().commit();
        } catch (PersistenceException ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new DataAccessException("Database Down", ex);
        }  catch (RuntimeException re) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw re;
        }
    }

    public List<GameWorld> getAllGameWorlds(EntityManager em) {
        try {
            return gameWorldDao.getAllGameWorld(em);
        } catch (PersistenceException ex) {
            throw new DataAccessException("Database down", ex);
        }
    }

}
