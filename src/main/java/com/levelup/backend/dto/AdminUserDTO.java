package com.levelup.backend.dto;

import java.time.LocalDateTime;

public record AdminUserDTO(
    Long id,
    String username,
    String email,
    Integer currentLevel,
    Integer currentXp,
    Integer streak,
    String role,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime lastLoginAt
) {}
