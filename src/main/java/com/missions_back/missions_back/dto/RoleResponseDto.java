package com.missions_back.missions_back.dto;

import java.time.LocalDateTime;
import java.util.List;

public record RoleResponseDto(
   Long id, 
   String code, 
   String name, 
   LocalDateTime createdAt, 
                          
   LocalDateTime updatedAt, 
   boolean actif,
   List<PermissionResponseDto> permissions
) {
    
}
