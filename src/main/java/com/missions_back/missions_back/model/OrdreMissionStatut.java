package com.missions_back.missions_back.model;

public enum OrdreMissionStatut {
    EN_ATTENTE_JUSTIFICATIF("En attente de justificatif"),
    EN_ATTENTE_CONFIRMATION("En attente de confirmation"),
    EN_ATTENTE_EXECUTION("En attente d'exécution"),
    EN_COURS("En cours"),
    ACHEVE("Achevé");

    private final String libelle;

    OrdreMissionStatut(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
