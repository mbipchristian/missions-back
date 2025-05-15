package com.missions_back.missions_back.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
@Table(name = "ordresmission")
public class OrdreMission {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false, nullable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String reference;

    @Column(nullable = false)
    private String objectif;

    @Column(nullable = false)
    private String modePaiement;

    @Column(nullable = false)
    private String devise;

    @Column(nullable = false)
    private int tauxAvance;

    @Column(nullable = false)
    private int duree;

    @Column(nullable = false)
    private int decompteTotal;

    @Column(nullable = false)
    private int decompteAvance;

    @Column(nullable = false)
    private int decompteRelicat;

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

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "mandat_id")
    private Mandat mandat;
}
