package com.missions_back.missions_back.dto;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import com.missions_back.missions_back.model.Rapport;
import com.missions_back.missions_back.model.Ressource;
import com.missions_back.missions_back.model.User;
import com.missions_back.missions_back.model.Ville;

public record MandatResponseDto(
        Long id,
        String reference,
        String objectif,
        boolean missionDeControle,
        Date dateDebut,
        Date dateFin,
        int duree,
        String pieceJointe,
        LocalDateTime created_at,
        LocalDateTime updated_at,
            // Relations
     List<UserResponseDto> users,
     List<VilleResponseDto> villes,
     List<RessourceResponseDto> ressources,
     RapportResponseDto rapport

        
) {
    
}
