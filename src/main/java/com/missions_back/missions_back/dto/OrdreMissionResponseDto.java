package com.missions_back.missions_back.dto;

import java.time.LocalDateTime;
import java.util.Date;

public record OrdreMissionResponseDto(
    Long id,
    String reference,
    String objectif,
    String modePaiement,
    String devise,
    Date dateDebut,
    Date dateFin,
    Long durée,
    Long tauxAvance,
    Long decompteTotal,
    Long decompteAvance,
    Long decompteRelicat,
    String pieceJointe,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    
}
