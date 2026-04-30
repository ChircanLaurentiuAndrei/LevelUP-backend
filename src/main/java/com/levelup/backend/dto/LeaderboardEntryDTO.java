package com.levelup.backend.dto;

public record LeaderboardEntryDTO(
    Long id,
    String username,
    Integer currentLevel,
    Integer currentXp,
    Integer streak
) {}
