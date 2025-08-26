package com.missions_back.missions_back.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private Long tauxAvance;

    @Column(nullable = false)
    private Date dateDebut;

    @Column(nullable = false)
    private Date dateFin;

    @Column(nullable = false)
    private Long duree;

    @Column(nullable = false)
    private Long decompteTotal;

    @Column(nullable = false)
    private Long decompteAvance;

    @Column(nullable = false)
    private Long decompteRelicat;

    // Statut de l'ordre de mission par défaut
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrdreMissionStatut statut = OrdreMissionStatut.EN_ATTENTE_JUSTIFICATIF;

    @Column
    private Long confirmeParUserId;

    @Column
    private Long rejeteParUserId;

    @Column
    private LocalDateTime dateConfirmation;

    @Column
    private LocalDateTime dateRejet;

    @Column
    private LocalDateTime dateSoumission;

    @Column
    private boolean pieceJointeAjoutee = false;

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

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "mandat_id")
    private Mandat mandat;

    @OneToMany(mappedBy = "ordreMission", cascade = CascadeType.ALL)
    private List<PieceJointe> piecesJointes;

    @ManyToMany
    @JoinTable(
        name = "ordre_mission_villes",
        joinColumns = @JoinColumn(name = "ordre_mission_id"),
        inverseJoinColumns = @JoinColumn(name = "ville_id")
    )
    private List<Ville> villes = new ArrayList<>();
    
    // Getters et setters
    public List<Ville> getVilles() {
        return villes;
    }
    
    public void setVilles(List<Ville> villes) {
        this.villes = villes;
    }

    
}
