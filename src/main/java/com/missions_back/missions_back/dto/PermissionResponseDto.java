package com.missions_back.missions_back.dto;

import java.time.LocalDateTime;

public record PermissionResponseDto(
    Long id,
    String name,
    String code,
    LocalDateTime created_at,
    LocalDateTime updated_at,
    boolean actif
) {}
