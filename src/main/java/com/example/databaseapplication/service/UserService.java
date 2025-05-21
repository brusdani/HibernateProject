package com.example.databaseapplication.service;

import com.example.databaseapplication.dao.UserDao;
import com.example.databaseapplication.exceptions.BusinessException;
import com.example.databaseapplication.exceptions.DataAccessException;
import com.example.databaseapplication.model.GameCharacter;
import com.example.databaseapplication.model.User;
import com.example.databaseapplication.model.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;

public class UserService {
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private UserDao userDao;

    public UserService(UserDao userDao){
        this.userDao = userDao;
    }

    public User registerNewUser(String login, String password, String email, EntityManager em) {
        User user = new User();
        user.setLogin(login);
        user.setPassword(password);
        user.setEmail(email);
        user.setType(UserType.REGULAR);
        user.setActive(false);

        try {
            em.getTransaction().begin();
            if (!userDao.existsByUsername(login, em)) {
                userDao.saveUser(user, em);
                em.getTransaction().commit();
                return user;
            } else {
                em.getTransaction().rollback();
                LOG.error("User with login: {} already exists", login);
                return null;
            }
        } catch (PersistenceException ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new DataAccessException("Database down", ex);
        }
    }
    public User authenticate(String login, String candidatePassword, EntityManager em) {
        try {
            User user = userDao.findByLogin(login, em);
            if (user == null) {
                LOG.info("Username doesn't exist");
                return null;    // no such username
            }
            if (!user.getPassword().equals(candidatePassword)) {
                return null;
            }
            if (user.isActive()) {
                LOG.warn("User '{}' was already marked active; overriding stale session", login);
                throw new BusinessException("User is already logged in elsewhere");
            }
            //em.getTransaction().begin();
            //user.setActive(true);
            //em.getTransaction().commit();
            return user;
        } catch (PersistenceException ex) {
            throw new DataAccessException("Database down", ex);
        }
    }
    public User updateUser(User user, EntityManager em) {
        try {
            em.getTransaction().begin();

            User managed = userDao.getUserById(user.getId(), em);
            if (managed == null) {
                em.getTransaction().rollback();
                throw new BusinessException("Cannot update: user not found.");
            }
            String newLogin = user.getLogin();
            if (newLogin != null && !newLogin.equals(managed.getLogin())) {
                if (userDao.existsByUsername(newLogin, em)) {
                    em.getTransaction().rollback();
                    throw new BusinessException("Username '" + newLogin + "' is already taken.");
                }
                managed.setLogin(newLogin);
            }

            String newEmail = user.getEmail();
            if (newEmail != null) {
                managed.setEmail(newEmail);
            }
            String newPassword = user.getPassword();
            if (newPassword != null && !newPassword.isEmpty()) {
                managed.setPassword(newPassword);
            }
            User updated = userDao.saveUser(managed, em);

            em.getTransaction().commit();
            return updated;

        } catch (PersistenceException pe) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new DataAccessException("Database error during user update", pe);
        }
    }


    public void deleteUser(User user, EntityManager em) {
        try {
            em.getTransaction().begin();
            userDao.deleteUser(user, em);
            em.getTransaction().commit();
        } catch (PersistenceException ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new DataAccessException("Database down", ex);
        }
    }
    public void logout(User user, EntityManager em) {
        try {
            em.getTransaction().begin();
            User managed = em.find(User.class, user.getId());
            if (managed == null) {
                em.getTransaction().rollback();
                return;
            }
            managed.setActive(false);
            em.merge(managed);
            em.getTransaction().commit();

        } catch (PersistenceException pe) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new DataAccessException("Database error during logout", pe);
        }
    }
}


