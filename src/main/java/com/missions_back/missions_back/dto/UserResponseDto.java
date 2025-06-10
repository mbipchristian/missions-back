package com.missions_back.missions_back.dto;

import java.time.LocalDateTime;

public record UserResponseDto(
    Long id, 
    String username, 
    String email, 
    String matricule, 
    Long quotaAnnuel,
    RoleResponseDto role, 
    String fonction,
    LocalDateTime created_at, 
    LocalDateTime updated_at
    ) {
    
}
