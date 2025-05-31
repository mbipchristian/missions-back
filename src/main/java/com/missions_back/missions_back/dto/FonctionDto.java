package com.missions_back.missions_back.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FonctionDto {
    
    @NotBlank(message = "Le nom de la fonction est obligatoire")
    private String nom;

    @NotNull(message = "L'ID du rang est obligatoire")
    private Long rangId;
}