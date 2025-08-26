package com.missions_back.missions_back.dto;

// DecomptesPreCreationRequest.java
import java.time.LocalDateTime;
import java.util.List;

public class DecomptesPreCreationRequest {
    private Long mandatId;
    private Long userId;
    private Double tauxAvance;
    private Integer duree;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private List<Long> villeIds;
    
    // Constructeurs
    public DecomptesPreCreationRequest() {}
    
    public DecomptesPreCreationRequest(Long mandatId, Long userId, Double tauxAvance, 
                                     Integer duree, LocalDateTime dateDebut, 
                                     LocalDateTime dateFin, List<Long> villeIds) {
        this.mandatId = mandatId;
        this.userId = userId;
        this.tauxAvance = tauxAvance;
        this.duree = duree;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.villeIds = villeIds;
    }
    
    // Getters et Setters
    public Long getMandatId() {
        return mandatId;
    }
    
    public void setMandatId(Long mandatId) {
        this.mandatId = mandatId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Double getTauxAvance() {
        return tauxAvance;
    }
    
    public void setTauxAvance(Double tauxAvance) {
        this.tauxAvance = tauxAvance;
    }
    
    public Integer getDuree() {
        return duree;
    }
    
    public void setDuree(Integer duree) {
        this.duree = duree;
    }
    
    public LocalDateTime getDateDebut() {
        return dateDebut;
    }
    
    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }
    
    public LocalDateTime getDateFin() {
        return dateFin;
    }
    
    public void setDateFin(LocalDateTime dateFin) {
        this.dateFin = dateFin;
    }
    
    public List<Long> getVilleIds() {
        return villeIds;
    }
    
    public void setVilleIds(List<Long> villeIds) {
        this.villeIds = villeIds;
    }
}
