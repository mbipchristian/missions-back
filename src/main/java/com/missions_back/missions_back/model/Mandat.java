package com.missions_back.missions_back.model;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Where(clause = "actif = true")
@Table(name = "mandats")
public class Mandat {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false, nullable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String reference;

    @Column(nullable = false)
    private String objectif;

    @Column(nullable = false)
    private boolean missionDeControle;

    @Column(nullable = false)
    private boolean interieur;

    @Column(nullable = false)
    private String pieceJointe;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime created_at;

    @UpdateTimestamp
    @Column
    private LocalDateTime updated_at;

    @Column
    private LocalDateTime deleted_at;

    @Column
    private boolean actif = true;

    //------------------------------------RELATIONS AVEC LES AUTRES TABLES--------------------------

    @ManyToMany(mappedBy = "mandats")
    private List<User> users;

    // @OneToMany(mappedBy = "mandat")
    // private List<OrdreMission> ordresMission;

    @ManyToMany()
    @JoinTable(name = "mandat_ville",
            joinColumns = @JoinColumn(name = "mandat_id"),
            inverseJoinColumns = @JoinColumn(name = "ville_id"))
    private List<Ville> villes;

    @ManyToMany()
    @JoinTable(name = "mandat_ressource",
            joinColumns = @JoinColumn(name = "mandat_id"),
            inverseJoinColumns = @JoinColumn(name = "ressource_id"))
    private List<Ressource> ressources;

    @OneToOne
    @JoinColumn(name = "rapport_id")
    private Rapport rapport;
}
