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

    @Transactional
    public void processRewards(UUID userId, Integer xpGained) {
        User user = userRepo.findByIdWithLock(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        user.setCurrentXp(user.getCurrentXp() + xpGained);

        int newLevel = (user.getCurrentXp() / props.xpPerLevel()) + 1;
        if (newLevel > user.getCurrentLevel()) {
            user.setCurrentLevel(newLevel);
        }

        checkAchievements(user);

        userRepo.save(user);
    }

    private void checkAchievements(User user) {
        List<Achievement> allAchievements = achievementRepo.findAll();
        long completedTasksCount = userTaskRepo.findByUserIdAndStatus(user.getId(), TaskStatus.COMPLETED).size();

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
