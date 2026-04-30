package com.levelup.backend.enums;

public sealed interface AchievementType permits AchievementType.TaskCount, AchievementType.LevelThreshold, AchievementType.XpTotal, AchievementType.StreakDays {
    record TaskCount() implements AchievementType {}
    record LevelThreshold() implements AchievementType {}
    record XpTotal() implements AchievementType {}
    record StreakDays() implements AchievementType {}
    
    AchievementType TASK_COUNT = new TaskCount();
    AchievementType LEVEL_THRESHOLD = new LevelThreshold();
    AchievementType XP_TOTAL = new XpTotal();
    AchievementType STREAK_DAYS = new StreakDays();
}
