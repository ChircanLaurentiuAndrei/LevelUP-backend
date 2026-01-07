package com.levelup.backend.service;

import com.levelup.backend.entity.Achievement;
import com.levelup.backend.entity.User;
import com.levelup.backend.enums.TaskStatus;
import com.levelup.backend.repository.AchievementRepository;
import com.levelup.backend.repository.UserRepository;
import com.levelup.backend.repository.UserTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GamificationService {

    public static final int XP_PER_LEVEL = 100;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private AchievementRepository achievementRepo;
    @Autowired
    private UserTaskRepository userTaskRepo;

    @Transactional
    public void processRewards(Long userId, Integer xpGained) {
        User user = userRepo.findByIdWithLock(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setCurrentXp(user.getCurrentXp() + xpGained);

        int newLevel = (user.getCurrentXp() / XP_PER_LEVEL) + 1;
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

            boolean unlocked = false;
            switch (ach.getCriteriaType()) {
                case TASK_COUNT:
                    if (completedTasksCount >= ach.getConditionValue()) unlocked = true;
                    break;
                case LEVEL_THRESHOLD:
                    if (user.getCurrentLevel() >= ach.getConditionValue()) unlocked = true;
                    break;
                case XP_TOTAL:
                    if (user.getCurrentXp() >= ach.getConditionValue()) unlocked = true;
                    break;
                case STREAK_DAYS:
                    if (user.getStreak() >= ach.getConditionValue()) unlocked = true;
                    break;
            }

            if (unlocked) {
                user.getUnlockedAchievements().add(ach);
            }
        }
    }
}