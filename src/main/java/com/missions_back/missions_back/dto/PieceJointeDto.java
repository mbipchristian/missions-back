package com.missions_back.missions_back.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PieceJointeDto {
    
    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 255, message = "Le nom ne peut pas dépasser 255 caractères")
    private String nom;

    @NotBlank(message = "Le nom original est obligatoire")
    @Size(max = 255, message = "Le nom original ne peut pas dépasser 255 caractères")
    private String nomOriginal;

    @NotBlank(message = "Le chemin du fichier est obligatoire")
    @Size(max = 500, message = "Le chemin du fichier ne peut pas dépasser 500 caractères")
    private String cheminFichier;

    @NotBlank(message = "Le type MIME est obligatoire")
    @Size(max = 100, message = "Le type MIME ne peut pas dépasser 100 caractères")
    private String typeMime;

    @NotNull(message = "La taille est obligatoire")
    private Long taille;

    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
    private String description;

    private Long mandatId;
    
    private Long ordreMissionId;
    
    private Long rapportId;
    
    @NotNull(message = "L'ID utilisateur est obligatoire")
    private Long userId;
}