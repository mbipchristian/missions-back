package com.missions_back.missions_back.dto;

import java.time.LocalDateTime;
import java.util.Date;

import com.missions_back.missions_back.model.OrdreMissionStatut;

public record OrdreMissionResponseDto(
    Long id,
    String reference,
    String objectif,
    String modePaiement,
    String devise,
    Date dateDebut,
    Date dateFin,
    Long duree,
    Long tauxAvance,
    Long decompteTotal,
    Long decompteAvance,
    Long decompteRelicat,
    OrdreMissionStatut statut,
    LocalDateTime created_at,
    LocalDateTime updated_at,
    LocalDateTime confirmele
) {
    
}
