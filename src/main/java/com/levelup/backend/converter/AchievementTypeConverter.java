package com.levelup.backend.converter;

import com.levelup.backend.enums.AchievementType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AchievementTypeConverter implements AttributeConverter<AchievementType, String> {
    @Override
    public String convertToDatabaseColumn(AchievementType attribute) {
        if (attribute == null) return null;
        return switch (attribute) {
            case AchievementType.TaskCount tc -> "TASK_COUNT";
            case AchievementType.LevelThreshold lt -> "LEVEL_THRESHOLD";
            case AchievementType.XpTotal xt -> "XP_TOTAL";
            case AchievementType.StreakDays sd -> "STREAK_DAYS";
        };
    }

    @Override
    public AchievementType convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return switch (dbData) {
            case "TASK_COUNT" -> AchievementType.TASK_COUNT;
            case "LEVEL_THRESHOLD" -> AchievementType.LEVEL_THRESHOLD;
            case "XP_TOTAL" -> AchievementType.XP_TOTAL;
            case "STREAK_DAYS" -> AchievementType.STREAK_DAYS;
            default -> throw new IllegalArgumentException("Unknown DB AchievementType: " + dbData);
        };
    }
}
