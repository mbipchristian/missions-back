package com.missions_back.missions_back.model;

public enum RapportStatut {
    EN_ATTENTE_CONFIRMATION("En attente de confirmation"),
    CONFIRME("Confirmé"),
    REJETE("Rejeté");

    private final String libelle;

    RapportStatut(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
