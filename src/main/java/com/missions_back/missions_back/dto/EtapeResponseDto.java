package com.missions_back.missions_back.dto;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public record EtapeResponseDto (
     Long id,
    
     String nom,
        
     Date dateDebut,
    
     Date dateFin,
    
     int duree,
    
     int ordre,
    
     LocalDateTime created_at,
    
     LocalDateTime updated_at,
    
    // Informations du mandat associé
    //  Long mandatId,
    
    // Listes des entités associées
     List<UserResponseDto> users,
    
     List<VilleResponseDto> villes,
    
     List<RessourceResponseDto> ressources
) {
}