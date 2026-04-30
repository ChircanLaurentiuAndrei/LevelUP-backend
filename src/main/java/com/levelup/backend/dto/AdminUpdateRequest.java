package com.levelup.backend.dto;

public record AdminUpdateRequest(
    String username,
    Integer currentLevel,
    Integer currentXp,
    Integer streak,
    String role
) {}
