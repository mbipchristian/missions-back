package com.missions_back.missions_back.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PieceJointeResponseDto {
    
    private Long id;
    
    private String nom;
    
    private String nomOriginal;
    
    private String cheminFichier;
    
    private String typeMime;
    
    private Long taille;
    
    private String description;
    
    private LocalDateTime created_at;
    
    private LocalDateTime updated_at;
    
    private boolean actif;
    
    // Relations simplifiées pour éviter la récursion
    private Long mandatId;
    
    private String mandatReference;
    
    private Long ordreMissionId;
    
    private String ordreMissionReference;
    
    private Long rapportId;
    
    private String rapportReference;
    
    private Long userId;
    
    private String userName;
}