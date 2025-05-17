package com.example.databaseapplication.service;

import com.example.databaseapplication.dao.GameWorldDao;
import com.example.databaseapplication.model.GameCharacter;
import com.example.databaseapplication.model.GameWorld;

import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.RollbackException;
import org.hibernate.StaleObjectStateException;
import java.util.ConcurrentModificationException;
import java.util.List;

public class GameWorldService {

    private final GameWorldDao gameWorldDao;

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

    public GameWorld updateWorld(GameWorld detachedCopy, EntityManager em) {
        try {
            em.getTransaction().begin();

            // 1) Check that the row is still there
            GameWorld currentlyInDb = em.find(GameWorld.class, detachedCopy.getId());
            if (currentlyInDb == null) {
                em.getTransaction().rollback();
                throw new ConcurrentModificationException(
                        "This world was deleted by someone else."
                );
            }
            // merge the detached copy (which carries its old @Version)
            GameWorld managed = em.merge(detachedCopy);
            em.getTransaction().commit();
            return managed;

        } catch (OptimisticLockException ole) {
            // catch JPA optimistic lock failure during merge()
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

        } catch (RuntimeException rex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw rex;
        }
    }
    public void deleteGameWorld(GameWorld gameWorld, EntityManager em) {
        em.getTransaction().begin();
        gameWorldDao.deleteGameWorld(gameWorld, em);
        em.getTransaction().commit();
    }

    public List<GameWorld> getAllGameWorlds(EntityManager em) {
        return gameWorldDao.getAllGameWorld(em);
    }

}
