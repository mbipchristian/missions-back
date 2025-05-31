package com.missions_back.missions_back.dto;

public record RegisterUserDto(
    String matricule, 
    String email, 
    String password, 
    String username, 
    Long quotaAnnuel,
    Long roleId, 
    Long fonctionId,
    Long rangId
    ) {
    
}
