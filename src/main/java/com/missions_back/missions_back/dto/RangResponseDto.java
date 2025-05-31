package com.missions_back.missions_back.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RangResponseDto {
    
    private Long id;
    private String nom;
    private String code;
    private BigDecimal fraisInterne;
    private BigDecimal fraisExterne;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private boolean actif;
    
    // Informations des fonctions associées
    private List<FonctionSummaryDto> fonctions;
}

