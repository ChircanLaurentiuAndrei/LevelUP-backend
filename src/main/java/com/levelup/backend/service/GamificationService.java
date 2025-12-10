package com.levelup.backend.service;

import com.levelup.backend.entity.Achievement;
import com.levelup.backend.entity.User;
import com.levelup.backend.repository.AchievementRepository;
import com.levelup.backend.repository.UserRepository;
import com.levelup.backend.repository.UserTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
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

    @Async
    @Transactional
    public void processRewards(Long userId, Integer xpGained) {
        User user = userRepo.findById(userId).orElseThrow();

        user.setCurrentXp(user.getCurrentXp() + xpGained);

        int newLevel = (user.getCurrentXp() / 100) + 1;
        if (newLevel > user.getCurrentLevel()) {
            user.setCurrentLevel(newLevel);
            // Might add a notification here
        }

        checkAchievements(user);

        userRepo.save(user);

        System.out.println("Async processing complete for User " + user.getUsername()
                + ". New XP: " + user.getCurrentXp());
    }

    private void checkAchievements(User user) {
        List<Achievement> allAchievements = achievementRepo.findAll();
        long completedTasksCount = userTaskRepo.findByUserIdAndStatus(user.getId(), "COMPLETED").size();

        for (Achievement ach : allAchievements) {
            if (user.getUnlockedAchievements().contains(ach)) continue;

            boolean unlocked = false;

            if (ach.getDescription().toLowerCase().contains("tasks") || ach.getDescription().toLowerCase().contains("task")) {
                if (completedTasksCount >= ach.getConditionValue()) unlocked = true;
            }
            else if (ach.getDescription().toLowerCase().contains("level") || ach.getDescription().toLowerCase().contains("levels")) {
                if (user.getCurrentLevel() >= ach.getConditionValue()) unlocked = true;
            }
            else if (ach.getDescription().toLowerCase().contains("xp")) {
                if (user.getCurrentXp() >= ach.getConditionValue()) unlocked = true;
            }
            if (unlocked) {
                user.getUnlockedAchievements().add(ach);
            }
        }
    }
}