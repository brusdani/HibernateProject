package com.example.databaseapplication.model;

import javax.persistence.*;
import java.math.BigDecimal;

public class Character {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;
    @Enumerated(EnumType.STRING)
    private CharacterJob job;
    private String name;
    @ManyToOne
    @JoinColumn(name = "user_id")  // This column will store the foreign key
    private User user;  // Foreign key reference to the User entity
    @Version
    @Column(name = "VERSION")
    private Integer version;

    public Character(long id, CharacterJob job, String name, User user) {
        this.id = id;
        this.job = job;
        this.name = name;
        this.user = user;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public CharacterJob getJob() {
        return job;
    }

    public void setJob(CharacterJob job) {
        this.job = job;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
