package com.levelup.backend.service;

import com.levelup.backend.entity.UserTask;
import com.levelup.backend.repository.UserTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VerificationService {

    @Autowired
    private UserTaskRepository userTaskRepo;

    @Autowired
    private GamificationService gamificationService;

    @Async
    @Transactional
    public void verifyTaskInBackground(Long userTaskId) {
        try {
            System.out.println("🧵 [Thread-" + Thread.currentThread().getId() + "] Started verifying Task ID: " + userTaskId);

            Thread.sleep(3000);

            UserTask userTask = userTaskRepo.findById(userTaskId)
                    .orElseThrow(() -> new IllegalArgumentException("Task not found during verification"));

            userTask.setStatus("COMPLETED");
            userTaskRepo.save(userTask);

            gamificationService.processRewards(userTask.getUser().getId(), userTask.getTask().getXpReward());

            System.out.println("✅ [Thread-" + Thread.currentThread().getId() + "] Verification complete. XP Awarded.");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Verification thread interrupted");
        } catch (Exception e) {
            System.err.println("Error during async verification: " + e.getMessage());
        }
    }
}