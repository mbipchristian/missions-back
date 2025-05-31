package com.missions_back.missions_back.dto;

import java.time.LocalDateTime;

public record RessourceResponseDto(
    Long id,
    String name,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    
}
