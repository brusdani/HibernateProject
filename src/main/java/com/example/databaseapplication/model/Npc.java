package com.example.databaseapplication.model;

import javax.persistence.*;

@Entity
@Table(name = "NPC")
public class Npc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;
    private String npcName;

    private String npcDescription;

    private String npcSpeech;

    @ManyToOne(optional = false)
    @JoinColumn(name = "location_id")
    private Location npcLocation;

}
