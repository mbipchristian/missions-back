package com.missions_back.missions_back.dto;

import java.util.Date;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EtapeDto {
    
    @NotBlank(message = "Le nom de l'étape est obligatoire")
    private String nom;
    
    @NotNull(message = "La date de début est obligatoire")
    private Date dateDebut;
    
    @NotNull(message = "La date de fin est obligatoire")
    private Date dateFin;
    
    @Positive(message = "La durée doit être positive")
    private int duree;
    
    @Positive(message = "L'ordre doit être positif")
    private int ordre;
    
    @NotNull(message = "L'ID du mandat est obligatoire")
    private Long mandatId;
    
    private List<Long> userIds;
    
    private List<Long> villeIds;
    
    private List<Long> ressourceIds;
}