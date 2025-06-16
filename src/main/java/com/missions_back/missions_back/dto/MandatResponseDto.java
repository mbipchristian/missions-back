package com.missions_back.missions_back.dto;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import com.missions_back.missions_back.model.MandatStatut;

public record MandatResponseDto(
    Long id,
    String reference,
    String objectif,
    Boolean missionDeControle,
    Date dateDebut,
    Date dateFin,
    Integer duree,
    MandatStatut statut,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    List<UserResponseDto> users,
    List<VilleResponseDto> villes,
    List<RessourceResponseDto> ressources,
    RapportResponseDto rapport,
    String createdBy,
    Integer usersCount,
    Integer villesCount,
    Integer ressourcesCount
) {}