package com.levelup.backend.service;

import com.levelup.backend.config.GamificationProperties;
import com.levelup.backend.entity.Achievement;
import com.levelup.backend.entity.User;
import com.levelup.backend.enums.TaskStatus;
import com.levelup.backend.exception.ResourceNotFoundException;
import com.levelup.backend.repository.AchievementRepository;
import com.levelup.backend.repository.UserRepository;
import com.levelup.backend.repository.UserTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GamificationService {

    private final GamificationProperties props;
    private final UserRepository userRepo;
    private final AchievementRepository achievementRepo;
    private final UserTaskRepository userTaskRepo;
    private final ApplicationContext applicationContext;

    public void processRewards(Long userId, Integer xpGained) {
        // Pre-fetch non-locked data to minimize lock duration
        List<Achievement> allAchievements = achievementRepo.findAll();
        long completedTasksCount = userTaskRepo.countByUserIdAndStatus(userId, TaskStatus.COMPLETED);
        
        GamificationService proxy = applicationContext.getBean(GamificationService.class);
        proxy.applyRewardsWithLock(userId, xpGained, allAchievements, completedTasksCount);
    }

    public void processLogin(Long userId) {
        List<Achievement> allAchievements = achievementRepo.findAll();
        long completedTasksCount = userTaskRepo.countByUserIdAndStatus(userId, TaskStatus.COMPLETED);

        GamificationService proxy = applicationContext.getBean(GamificationService.class);
        proxy.updateStreakAndCheckAchievements(userId, allAchievements, completedTasksCount);
    }

    @Transactional
    public void updateStreakAndCheckAchievements(Long userId, List<Achievement> allAchievements, long completedTasksCount) {
        User user = userRepo.findByIdWithLock(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();

        if (user.getLastLoginAt() != null) {
            LocalDate lastLoginDate = user.getLastLoginAt().toLocalDate();
            if (lastLoginDate.isBefore(today)) {
                if (lastLoginDate.equals(today.minusDays(1))) {
                    user.setStreak(user.getStreak() + 1);
                } else {
                    user.setStreak(1);
                }
            }
            // If already logged in today, do nothing to streak
        } else {
            user.setStreak(1);
        }

        user.setLastLoginAt(now);
        checkAchievements(user, allAchievements, completedTasksCount);
        userRepo.save(user);
    }

    @Transactional
    public void applyRewardsWithLock(Long userId, Integer xpGained, List<Achievement> allAchievements, long completedTasksCount) {
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
                case TASK_COUNT -> completedTasksCount >= ach.getConditionValue();
                case LEVEL_THRESHOLD -> user.getCurrentLevel() >= ach.getConditionValue();
                case XP_TOTAL -> user.getCurrentXp() >= ach.getConditionValue();
                case STREAK_DAYS -> user.getStreak() >= ach.getConditionValue();
            };

            if (unlocked) {
                user.getUnlockedAchievements().add(ach);
            }
        }
    }
}
