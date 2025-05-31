package com.missions_back.missions_back.model;

import java.time.LocalDateTime;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Where(clause = "actif = true")
@Table(name = "fonctions")
public class Fonction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false, nullable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nom;

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

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rang_id", nullable = false)
    private Rang rang;

    @OneToMany(mappedBy = "fonction", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<User> users;

    // Constructeurs
    public Fonction() {}

    public Fonction(String nom, Rang rang) {
        this.nom = nom;
        this.rang = rang;
    }

    // Méthodes utilitaires
    public Fonction setNom(String nom) {
        this.nom = nom;
        return this;
    }

    public Fonction setRang(Rang rang) {
        this.rang = rang;
        return this;
    }

    public Fonction setActif(boolean actif) {
        this.actif = actif;
        return this;
    }

    public void setDeletedAt(LocalDateTime deleted_at) {
        this.deleted_at = deleted_at;
    }
}