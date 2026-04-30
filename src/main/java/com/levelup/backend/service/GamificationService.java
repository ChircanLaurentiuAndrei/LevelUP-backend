package com.levelup.backend.service;

import com.levelup.backend.config.GamificationProperties;
import com.levelup.backend.entity.Achievement;
import com.levelup.backend.entity.User;
import com.levelup.backend.enums.AchievementType;
import com.levelup.backend.enums.TaskStatus;
import com.levelup.backend.exception.ResourceNotFoundException;
import com.levelup.backend.repository.AchievementRepository;
import com.levelup.backend.repository.UserRepository;
import com.levelup.backend.repository.UserTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GamificationService {

    private final GamificationProperties props;
    private final UserRepository userRepo;
    private final AchievementRepository achievementRepo;
    private final UserTaskRepository userTaskRepo;
    private final ApplicationContext applicationContext;

    public void processRewards(UUID userId, Integer xpGained) {
        // Pre-fetch non-locked data to minimize lock duration
        List<Achievement> allAchievements = achievementRepo.findAll();
        long completedTasksCount = userTaskRepo.findByUserIdAndStatus(userId, TaskStatus.COMPLETED).size();
        
        GamificationService proxy = applicationContext.getBean(GamificationService.class);
        proxy.applyRewardsWithLock(userId, xpGained, allAchievements, completedTasksCount);
    }

    @Transactional
    public void applyRewardsWithLock(UUID userId, Integer xpGained, List<Achievement> allAchievements, long completedTasksCount) {
        User user = userRepo.findByIdWithLock(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        user.setCurrentXp(user.getCurrentXp() + xpGained);

        int newLevel = (user.getCurrentXp() / props.xpPerLevel()) + 1;
        if (newLevel > user.getCurrentLevel()) {
            user.setCurrentLevel(newLevel);
        }

        checkAchievements(user, allAchievements, completedTasksCount);

        userRepo.save(user);
    }

    private void checkAchievements(User user, List<Achievement> allAchievements, long completedTasksCount) {
        for (Achievement ach : allAchievements) {
            if (user.getUnlockedAchievements().contains(ach)) continue;

            boolean unlocked = switch (ach.getCriteriaType()) {
                case AchievementType.TaskCount tc -> completedTasksCount >= ach.getConditionValue();
                case AchievementType.LevelThreshold lt -> user.getCurrentLevel() >= ach.getConditionValue();
                case AchievementType.XpTotal xt -> user.getCurrentXp() >= ach.getConditionValue();
                case AchievementType.StreakDays sd -> user.getStreak() >= ach.getConditionValue();
            };

            if (unlocked) {
                user.getUnlockedAchievements().add(ach);
            }
        }
    }
}
