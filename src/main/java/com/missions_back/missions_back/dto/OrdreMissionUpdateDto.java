package com.missions_back.missions_back.dto;

import java.util.Date;

import com.missions_back.missions_back.model.OrdreMissionStatut;

public class OrdreMissionUpdateDto {
    private String objectif;
    private String modePaiement;
    private String devise;
    private Date dateDebut;
    private Date dateFin;
    private Long duree;
    private Long tauxAvance;
    private Long decompteTotal;
    private Long decompteAvance;
    private Long decompteRelicat;
    private OrdreMissionStatut statut;

    // Getters and Setters
    public String getObjectif() {
        return objectif;
    }
    public void setObjectif(String objectif) {
        this.objectif = objectif;
    }
    public String getModePaiement() {
        return modePaiement;
    }
    public void setModePaiement(String modePaiement) {
        this.modePaiement = modePaiement;
    }
    public String getDevise() {
        return devise;
    }
    public void setDevise(String devise) {
        this.devise = devise;
    }
    public Date getDateDebut() {
        return dateDebut;
    }
    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }
    public Date getDateFin() {
        return dateFin;
    }
    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }
    public Long getDuree() {
        return duree;
    }
    public void setDuree(Long duree) {
        this.duree = duree;
    }
    public Long getTauxAvance() {
        return tauxAvance;
    }
    public void setTauxAvance(Long tauxAvance) {
        this.tauxAvance = tauxAvance;
    }
    public Long getDecompteTotal() {
        return decompteTotal;
    }
    public void setDecompteTotal(Long decompteTotal) {
        this.decompteTotal = decompteTotal;
    }
    public Long getDecompteAvance() {
        return decompteAvance;
    }
    public void setDecompteAvance(Long decompteAvance) {
        this.decompteAvance = decompteAvance;
    }
    public Long getDecompteRelicat() {
        return decompteRelicat;
    }
    public void setDecompteRelicat(Long decompteRelicat) {
        this.decompteRelicat = decompteRelicat;
    }
    public OrdreMissionStatut getStatut() {
        return statut;
    }
    public void setStatut(OrdreMissionStatut statut) {
        this.statut = statut;
    }

}
