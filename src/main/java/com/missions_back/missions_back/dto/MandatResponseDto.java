package com.missions_back.missions_back.dto;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public record MandatResponseDto(
        Long id,
        String reference,
        String objectif,
        boolean missionDeControle,
        Date dateDebut,
        Date dateFin,
        int duree,
        LocalDateTime created_at,
        LocalDateTime updated_at,
            // Relations
     List<UserResponseDto> users,
     List<VilleResponseDto> villes,
     List<RessourceResponseDto> ressources,
     RapportResponseDto rapport

        
) {
    
}
