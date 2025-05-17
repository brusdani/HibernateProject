package com.example.databaseapplication.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "USERS",
        uniqueConstraints = @UniqueConstraint(columnNames = "login")
)

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    protected Long id;
    @Enumerated(EnumType.STRING)
    private UserType type;
    @Column(name = "LOGIN", nullable = false, unique = true)
    protected String login;
    private String password;
    private String email;

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<GameCharacter> userCharacters = new ArrayList<>();

    @Version
    @Column(name = "VERSION")
    private Integer version;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public List<GameCharacter> getUserCharacters() {
        return userCharacters;
    }

    public void addCharacter(GameCharacter gameCharacter){
        userCharacters.add(gameCharacter);
        gameCharacter.setUser(this);
    }
    public void removeCharacter(GameCharacter gameCharacter){
        userCharacters.remove(gameCharacter);
        gameCharacter.setUser(null);
    }
    public void setUserCharacters(List<GameCharacter> userCharacters) {
        this.userCharacters = userCharacters;
    }
}
