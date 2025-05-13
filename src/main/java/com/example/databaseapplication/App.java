package com.example.databaseapplication;

import com.example.databaseapplication.model.User;
import com.example.databaseapplication.model.UserType;

import javax.persistence.*;

/**
 * Application-level service for managing Users via JPA.
 * <p>
 * Implements a singleton pattern for EntityManagerFactory and
 * creates a new EntityManager per operation to ensure thread-safety.
 */
public class App {
    /** Singleton instance of App */
    private static final App INSTANCE = new App();

    /** Factory for creating EntityManager instances */
    private final EntityManagerFactory emf;

    /**
     * Private constructor initializes the EntityManagerFactory
     */
    private App() {
        emf = Persistence.createEntityManagerFactory("punit");
    }

    /**
     * Accessor for the singleton instance
     *
     * @return the single App instance
     */
    public static App getInstance() {
        return INSTANCE;
    }

    /**
     * Register (persist) a new User with the given credentials.
     * Opens a new EntityManager for each call to avoid threading issues.
     *
     * @param login    the username (must be unique)
     * @param password the password (stored as plain text for demonstration)
     * @param email    the email address
     * @return true if the user was created; false if the login already exists or on error
     */
    public boolean registerNewUser(String login, String password, String email) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (userExists(em, login)) {
                tx.rollback();
                return false;
            }
            User user = new User();
            user.setLogin(login);
            user.setPassword(password);
            user.setEmail(email);
            user.setType(UserType.REGULAR);

            em.persist(user);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    /**
     * Check whether a User with the given login already exists.
     * Uses the provided EntityManager.
     *
     * @param em    the EntityManager to use
     * @param login the username to check
     * @return true if a User with that login exists
     */
    private boolean userExists(EntityManager em, String login) {
        Long count = em.createQuery(
                        "SELECT COUNT(u) FROM User u WHERE u.login = :login", Long.class)
                .setParameter("login", login)
                .getSingleResult();
        return count > 0;
    }
    /**
     * Fetches a User by login.
     * @param login the username
     * @return the User entity, or null if not found
     */
    public User getUserByLogin(String login) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<User> q = em.createQuery(
                    "SELECT u FROM User u WHERE u.login = :login", User.class);
            q.setParameter("login", login);
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * Authenticates a user.
     * @param login the username
     * @param password the password to check
     * @return the authenticated User on success; null on failure
     */
    public User authenticate(String login, String password) {
        User user = getUserByLogin(login);
        if (user == null) {
            // user does not exist
            return null;
        }
        if (user.getPassword().equals(password)) {
            // password matches
            return user;
        }
        // wrong password
        return null;
    }

    /**
     * Convenience boolean version.
     * @return true if login + password are correct
     */
    public boolean login(String login, String password) {
        return authenticate(login, password) != null;
    }

    /**
     * Shutdown the EntityManagerFactory when the application stops.
     */
    public void shutdown() {
        if (emf.isOpen()) {
            emf.close();
        }
    }
}
