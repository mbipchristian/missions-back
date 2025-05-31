package com.missions_back.missions_back.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FonctionResponseDto {
    
    private Long id;
    private String nom;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    
    // Informations du rang associé
    private Long rangId;
    private String rangNom;
    private String rangCode;
}