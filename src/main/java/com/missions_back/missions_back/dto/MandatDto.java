package com.missions_back.missions_back.dto;

import java.util.Date;
import java.util.List;

public record MandatDto(
        String reference,
        String objectif,
        boolean missionDeControle,
        Date dateDebut,
        Date dateFin,
        int duree,
        String pieceJointe,
        Long rapportId,
        List<Long> userIds,
        List<Long> ressourceIds,
        List<Long> villeIds
) {
    
}
