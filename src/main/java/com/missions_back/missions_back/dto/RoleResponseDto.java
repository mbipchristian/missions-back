package com.missions_back.missions_back.dto;

import java.time.LocalDateTime;

import com.missions_back.missions_back.model.RoleEnum;

public record RoleResponseDto(
   Long id, 
   RoleEnum name, 
   String description, 
   LocalDateTime createdAt,              
   LocalDateTime updatedAt
) {
    
}
