package com.example.databaseapplication.model;

import javax.persistence.*;
import java.util.List;
@Entity
@Table(name = "GAME_WORLD")
public class GameWorld {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @Column(name = "WORLD_NAME", nullable = false, unique = true)
    private String worldName;
    @Column(name = "WORLD_DESCRIPTION")
    private String worldDescription;
    @OneToMany(
            mappedBy = "gameWorld",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<GameCharacter> gameCharacters;
    @Version
    @Column(name = "VERSION")
    private Integer version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public String getWorldDescription() {
        return worldDescription;
    }

    public void setWorldDescription(String worldDescription) {
        this.worldDescription = worldDescription;
    }

    public List<GameCharacter> getGameCharacters() {
        return gameCharacters;
    }

    public void setGameCharacters(List<GameCharacter> gameCharacters) {
        this.gameCharacters = gameCharacters;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "GameWorld{" +
                worldName +
                '}';
    }
}

