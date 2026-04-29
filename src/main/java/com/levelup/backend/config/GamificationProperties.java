package com.levelup.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.gamification")
public record GamificationProperties(
        int xpPerLevel,
        int dailyTaskLimit,
        int minProgramTasks
) {}
