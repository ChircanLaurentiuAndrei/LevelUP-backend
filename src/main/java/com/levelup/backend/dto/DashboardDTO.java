package com.levelup.backend.dto;

import com.levelup.backend.entity.Achievement;
import com.levelup.backend.entity.User;
import com.levelup.backend.entity.UserTask;
import java.util.List;

public record DashboardDTO(
    String username,
    Integer level,
    Integer currentXp,
    Integer xpToNextLevel,
    Integer streak,
    String studyProgramName,
    List<UserTaskDTO> tasks,
    List<Long> unlockedAchievementIds
) {
    public static DashboardDTO fromUserAndTasks(User user, List<UserTask> userTasks, int xpPerLevel) {
        final int currentXp = user.getCurrentXp();
        int xpSinceLastLevel = currentXp % xpPerLevel;
        int xpToNext = xpPerLevel - xpSinceLastLevel;
        
        return new DashboardDTO(
            user.getUsername(),
            user.getCurrentLevel(),
            currentXp,
            Math.max(1, xpToNext),
            user.getStreak(),
            user.getStudyProgram() != null ? user.getStudyProgram().getName() : "N/A",
            userTasks.stream().map(UserTaskDTO::fromEntity).toList(),
            user.getUnlockedAchievements().stream().map(Achievement::getId).toList()
        );
    }
}
