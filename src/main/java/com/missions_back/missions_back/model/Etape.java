package com.missions_back.missions_back.model;

import java.time.LocalDateTime;
import java.util.Date;
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
@Table(name = "etapes")
public class Etape {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false, nullable = false)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private Date dateDebut;

    @Column(nullable = false)
    private Date dateFin;

    @Column(nullable = false)
    private int duree;

    @Column(nullable = false)
    private int ordre;

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

    //-------------------------------RELATIONS AVEC LES AUTRES TABLES---------------------------------

    @ManyToOne
    @JoinColumn(name = "mandat_id", nullable = false)
    private Mandat mandat;

    @ManyToMany()
    @JoinTable(name = "etape_user",
            joinColumns = @JoinColumn(name = "etape_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> users;

    @ManyToMany()
    @JoinTable(name = "etape_ville",
            joinColumns = @JoinColumn(name = "etape_id"),
            inverseJoinColumns = @JoinColumn(name = "ville_id"))
    private List<Ville> villes;

    @ManyToMany()
    @JoinTable(name = "etape_ressource",
            joinColumns = @JoinColumn(name = "etape_id"),
            inverseJoinColumns = @JoinColumn(name = "ressource_id"))
    private List<Ressource> ressources;
}