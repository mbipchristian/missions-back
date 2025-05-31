package com.missions_back.missions_back.dto;

import java.util.Date;

public record OrdreMissionDto(
    String reference,
    String objectif,
    String modePaiement,
    String devise,
    Long tauxAvance,
    Date dateDebut,
    Date dateFin,
    Long duree,
    Long decompteTotal,
    Long decompteAvance,
    Long decompteRelicat,
    Long mandatId
) {
    
}
