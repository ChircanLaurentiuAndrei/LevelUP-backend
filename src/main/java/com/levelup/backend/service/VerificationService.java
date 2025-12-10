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

    /**
     * THREAD IMPLEMENTATION:
     * This method is marked with @Async, meaning Spring Boot will execute it
     * in a separate thread from the main HTTP request pool.
     * * It simulates a "grading" process by sleeping, then updates the database.
     */
    @Async
    @Transactional
    public void verifyTaskInBackground(Long userTaskId) {
        try {
            System.out.println("🧵 [Thread-" + Thread.currentThread().getId() + "] Started verifying Task ID: " + userTaskId);

            // 1. Simulate "Heavy" Processing (e.g., compiling code, checking answers)
            // The thread sleeps for 3 seconds while the user sees "Verifying..."
            Thread.sleep(3000);

            // 2. Fetch the task again (it was saved as VERIFYING in the main thread)
            UserTask userTask = userTaskRepo.findById(userTaskId)
                    .orElseThrow(() -> new IllegalArgumentException("Task not found during verification"));

            // 3. Mark as COMPLETED
            userTask.setStatus("COMPLETED");
            userTaskRepo.save(userTask);

            // 4. Award XP (The Real Reward)
            // We call the synchronous gamification service here
            gamificationService.processRewards(userTask.getUser().getId(), userTask.getTask().getXpReward());

            System.out.println("✅ [Thread-" + Thread.currentThread().getId() + "] Verification complete. XP Awarded.");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Verification thread interrupted");
        } catch (Exception e) {
            System.err.println("Error during async verification: " + e.getMessage());
            // In a real app, we would set status to "FAILED" here
        }
    }
}