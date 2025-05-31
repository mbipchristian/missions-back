package com.missions_back.missions_back.model;

import java.math.BigDecimal;
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
@Table(name = "rangs")
public class Rang {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false, nullable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nom;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal fraisInterne;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal fraisExterne;

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
    @OneToMany(mappedBy = "rang", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Fonction> fonctions;

    // Constructeurs
    public Rang() {}

    public Rang(String nom, String code, BigDecimal fraisInterne, BigDecimal fraisExterne) {
        this.nom = nom;
        this.code = code;
        this.fraisInterne = fraisInterne;
        this.fraisExterne = fraisExterne;
    }

    // Méthodes utilitaires
    public Rang setNom(String nom) {
        this.nom = nom;
        return this;
    }

    public Rang setCode(String code) {
        this.code = code;
        return this;
    }

    public Rang setFraisInterne(BigDecimal fraisInterne) {
        this.fraisInterne = fraisInterne;
        return this;
    }

    public Rang setFraisExterne(BigDecimal fraisExterne) {
        this.fraisExterne = fraisExterne;
        return this;
    }

    public Rang setActif(boolean actif) {
        this.actif = actif;
        return this;
    }

    public void setDeletedAt(LocalDateTime deleted_at) {
        this.deleted_at = deleted_at;
    }
}