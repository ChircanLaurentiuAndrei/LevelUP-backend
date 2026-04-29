package com.levelup.backend.service;

import com.levelup.backend.entity.UserTask;
import com.levelup.backend.enums.TaskStatus;
import com.levelup.backend.repository.UserTaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationService {

    private final UserTaskRepository userTaskRepo;
    private final GamificationService gamificationService;

    @Async
    @Transactional
    public void verifyTaskInBackground(Long userTaskId) {
        try {
            log.info("Started verifying Task ID: {}", userTaskId);

            // Simulate verification logic
            Thread.sleep(3000);

            UserTask userTask = userTaskRepo.findById(userTaskId)
                    .orElseThrow(() -> new IllegalArgumentException("Task not found during verification: " + userTaskId));

            userTask.setStatus(TaskStatus.COMPLETED);
            userTaskRepo.save(userTask);

            gamificationService.processRewards(userTask.getUser().getId(), userTask.getTask().getXpReward());

            log.info("Verification complete for Task ID: {}. XP Awarded.", userTaskId);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Verification thread interrupted for Task ID: {}", userTaskId);
            resetTaskStatus(userTaskId);
        } catch (Exception e) {
            log.error("Error during async verification for Task ID: {}: {}", userTaskId, e.getMessage());
            resetTaskStatus(userTaskId);
        }
    }

    private void resetTaskStatus(Long userTaskId) {
        userTaskRepo.findById(userTaskId).ifPresent(ut -> {
            if (ut.getStatus() == TaskStatus.VERIFYING) {
                ut.setStatus(TaskStatus.PENDING);
                userTaskRepo.save(ut);
                log.info("Reset Task ID: {} back to PENDING due to verification failure.", userTaskId);
            }
        });
    }
}
