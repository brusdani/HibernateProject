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
    private String worldName;
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
}

