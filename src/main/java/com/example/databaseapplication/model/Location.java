package com.example.databaseapplication.model;

import javax.persistence.*;
import java.util.List;
import java.util.Set;
@Entity
@Table(name = "LOCATIONS")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;
    private String locationName;
    private String locationDescription;
    @OneToMany(
            mappedBy = "npcLocation",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<Npc> npcs;
    @OneToMany(
            mappedBy = "currentLocation",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<GameCharacter> gameCharacters;
    private String email;
}
