package com.missions_back.missions_back.dto;

import java.time.LocalDateTime;

public record RapportResponseDto(
    Long id,
    String reference,
    String pieceJointe,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    
}
