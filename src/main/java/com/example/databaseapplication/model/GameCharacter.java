package com.example.databaseapplication.model;

import javax.persistence.*;
@Entity
@Table(name = "CHARACTERS")
public class GameCharacter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;
    @Enumerated(EnumType.STRING)
    private CharacterJob job;
    private String name;
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location currentLocation;

    @Version
    @Column(name = "VERSION")
    private Integer version;

    public GameCharacter() {

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
