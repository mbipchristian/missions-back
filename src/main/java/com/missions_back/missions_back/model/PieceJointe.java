package com.missions_back.missions_back.model;

import java.time.LocalDateTime;

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
@Table(name = "pieces_jointes")
public class PieceJointe {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false, nullable = false)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String nomOriginal;

    @Column(nullable = false)
    private String cheminFichier;

    @Column(nullable = false)
    private String typeMime;

    @Column(nullable = false)
    private Long taille;

    @Column
    private String description;

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

    @ManyToOne
    @JoinColumn(name = "mandat_id")
    private Mandat mandat;

    @ManyToOne
    @JoinColumn(name = "ordre_mission_id")
    private OrdreMission ordreMission;

    @ManyToOne
    @JoinColumn(name = "rapport_id")
    private Rapport rapport;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}