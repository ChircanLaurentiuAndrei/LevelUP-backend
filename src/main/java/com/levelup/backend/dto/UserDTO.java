package com.levelup.backend.dto;

import java.time.LocalDate;
import java.util.List;

public record UserDTO(
    Long id,
    String username,
    String email,
    Integer currentLevel,
    Integer currentXp,
    Integer streak,
    String studyProgram,
    LocalDate lastLoginDate,
    List<Long> unlockedAchievementIds
) {}
