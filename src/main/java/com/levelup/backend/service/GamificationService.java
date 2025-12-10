package com.levelup.backend.service;

import com.levelup.backend.entity.Achievement;
import com.levelup.backend.entity.User;
import com.levelup.backend.repository.AchievementRepository;
import com.levelup.backend.repository.UserRepository;
import com.levelup.backend.repository.UserTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GamificationService {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private AchievementRepository achievementRepo;
    @Autowired
    private UserTaskRepository userTaskRepo;

    @Transactional
    public void processRewards(Long userId, Integer xpGained) {
        User user = userRepo.findById(userId).orElseThrow();

        user.setCurrentXp(user.getCurrentXp() + xpGained);

        int newLevel = (user.getCurrentXp() / 100) + 1;
        if (newLevel > user.getCurrentLevel()) {
            user.setCurrentLevel(newLevel);
        }

        checkAchievements(user);

        userRepo.save(user);
    }

    private void checkAchievements(User user) {
        List<Achievement> allAchievements = achievementRepo.findAll();
        long completedTasksCount = userTaskRepo.findByUserIdAndStatus(user.getId(), "COMPLETED").size();

        for (Achievement ach : allAchievements) {
            if (user.getUnlockedAchievements().contains(ach)) continue;

            boolean unlocked = false;
            String desc = ach.getDescription().toLowerCase();

            if (desc.contains("task") || desc.contains("tasks")) {
                if (completedTasksCount >= ach.getConditionValue()) unlocked = true;
            }
            else if (desc.contains("level")) {
                if (user.getCurrentLevel() >= ach.getConditionValue()) unlocked = true;
            }
            else if (desc.contains("xp")) {
                if (user.getCurrentXp() >= ach.getConditionValue()) unlocked = true;
            }

            if (unlocked) {
                user.getUnlockedAchievements().add(ach);
            }
        }
    }
}