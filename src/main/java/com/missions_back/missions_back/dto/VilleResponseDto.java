package com.missions_back.missions_back.dto;

import java.time.LocalDateTime;

public record VilleResponseDto(
    Long id,
    String name,
    String code,
    boolean interieur,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    
}
