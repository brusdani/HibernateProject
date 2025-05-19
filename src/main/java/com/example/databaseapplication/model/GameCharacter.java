package com.example.databaseapplication.model;

import javax.persistence.*;
@Entity
@Table(name = "CHARACTERS")
public class GameCharacter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @Enumerated(EnumType.STRING)
    private CharacterJob job;
    private String name;

    private String imageURL;
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    public GameWorld getGameWorld() {
        return gameWorld;
    }

    public void setGameWorld(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    @ManyToOne
    @JoinColumn(name = "gameworld_id")
    private GameWorld gameWorld;

    @Version
    @Column(name = "VERSION")
    private Integer version;

    public GameCharacter() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return name + "(" + job + ")(" + gameWorld + ")" ;
    }
}
