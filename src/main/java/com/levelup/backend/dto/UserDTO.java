package com.levelup.backend.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record UserDTO(
    UUID id,
    String username,
    String email,
    Integer currentLevel,
    Integer currentXp,
    Integer streak,
    String studyProgram,
    LocalDate lastLoginDate,
    List<Long> unlockedAchievementIds
) {}
