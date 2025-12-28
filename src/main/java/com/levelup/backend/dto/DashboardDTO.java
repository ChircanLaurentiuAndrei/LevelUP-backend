package com.levelup.backend.dto;

import com.levelup.backend.entity.Achievement;
import com.levelup.backend.entity.User;
import com.levelup.backend.entity.UserTask;
import com.levelup.backend.service.GamificationService;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.stream.Collectors;

@Value
@Builder
public class DashboardDTO {
    String username;
    Integer level;
    Integer currentXp;
    Integer xpToNextLevel;
    Integer streak;
    String studyProgramName;
    List<UserTaskDTO> tasks;
    List<Long> unlockedAchievementIds;

    public static DashboardDTO fromUserAndTasks(User user, List<UserTask> userTasks) {
        final int xpRequired = GamificationService.XP_PER_LEVEL;
        final int currentXp = user.getCurrentXp();

        int xpSinceLastLevel = currentXp % xpRequired;
        int xpToNext = xpRequired - xpSinceLastLevel;
        if (xpToNext == 0 && currentXp > 0) {
            xpToNext = xpRequired;
        }

        return DashboardDTO.builder()
                .username(user.getUsername())
                .level(user.getCurrentLevel())
                .currentXp(currentXp)
                .xpToNextLevel(Math.max(1, xpToNext))
                .streak(user.getStreak())
                .studyProgramName(user.getStudyProgram() != null ? user.getStudyProgram().getName() : "N/A")
                .tasks(userTasks.stream()
                        .map(UserTaskDTO::fromEntity)
                        .collect(Collectors.toList()))
                .unlockedAchievementIds(user.getUnlockedAchievements().stream()
                        .map(Achievement::getId)
                        .collect(Collectors.toList()))
                .build();
    }
}