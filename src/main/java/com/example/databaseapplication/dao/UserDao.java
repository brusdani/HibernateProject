package com.example.databaseapplication.dao;

import com.example.databaseapplication.model.GameCharacter;
import com.example.databaseapplication.model.User;

import javax.persistence.EntityManager;
import java.util.List;

public class UserDao {
    public List<User> getAllUsers(EntityManager em){
        return em.createQuery("from User", User.class)
                .getResultList();
    }
    public List<User> getUsers(String loginFilterValue, EntityManager em){
        return em.createQuery("from User where login like :login", User.class)
                .setParameter("login", loginFilterValue)
                .getResultList();
    }
    public boolean existsByUsername(String login, EntityManager em) {
        String jpql = "SELECT COUNT(u)>0 FROM User u WHERE u.login = :login";
        return em.createQuery(jpql, Boolean.class)
                .setParameter("login", login)
                .getSingleResult();
    }

    public User saveUser(User user, EntityManager em){
        if(user.getId() == null) {
            em.persist(user);
        } else {
            user = em.merge(user);
        }
        return user;
    }
    public User findByLogin(String login, EntityManager em) {
        String jpql = "FROM User u WHERE u.login = :login";
        return em.createQuery(jpql, User.class)
                .setParameter("login", login)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }
    public void deleteUser(User user, EntityManager em) {
        User managed = em.find(User.class, user.getId());
        if (managed != null) {
            em.remove(managed);
        }
    }
    public User getUserById(Long id, EntityManager em) {
        return em.find(User.class, id);
    }


}
