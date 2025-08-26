package com.missions_back.missions_back.dto;

// DecomptesResponse.java
public class DecomptesResponse {
    private Long decompteTotal;
    private Long decompteAvance;
    private Long decompteRelicat;
    
    // Constructeurs
    public DecomptesResponse() {}
    
    public DecomptesResponse(Long decompteTotal, Long decompteAvance, Long decompteRelicat) {
        this.decompteTotal = decompteTotal;
        this.decompteAvance = decompteAvance;
        this.decompteRelicat = decompteRelicat;
    }
    
    // Getters et Setters
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
}