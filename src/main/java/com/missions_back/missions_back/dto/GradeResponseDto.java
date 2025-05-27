package com.missions_back.missions_back.dto;

import java.time.LocalDateTime;

public record GradeResponseDto(
    Long id,
    String name,
    String code,
    Long fraisExterne,
    Long fraisInterne,
    LocalDateTime created_at,
    LocalDateTime updated_at
) {
    
}
