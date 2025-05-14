package com.example.databaseapplication.service;

import com.example.databaseapplication.dao.UserDao;
import com.example.databaseapplication.model.User;
import com.example.databaseapplication.model.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

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


            em.getTransaction().begin();
            if (!userDao.existsByUsername(login, em)){
                userDao.saveUser(user, em);
                em.getTransaction().commit();
                return user;
            }
            else {
                em.getTransaction().rollback();
                LOG.error("User with login: {} already exists", login);
                return null;
            }
    }
    public User authenticate(String login, String candidatePassword, EntityManager em) {
        User user = userDao.findByLogin(login, em);
        if (user == null) {
            LOG.info("Username doesn't exist");
            return null;    // no such username
        }
        if (user.getPassword().equals(candidatePassword)) {
            return user;    // success
        } else {
            return null;    // wrong password
        }
    }
}


