package com.levelup.backend.controller;

import com.levelup.backend.dto.DashboardDTO; // NEW: Import the correct DTO
import com.levelup.backend.entity.User;
import com.levelup.backend.entity.UserTask;
import com.levelup.backend.repository.UserRepository;
import com.levelup.backend.repository.UserTaskRepository;
import com.levelup.backend.service.GamificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class TaskController {

    @Autowired
    private UserTaskRepository userTaskRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private GamificationService gamificationService;

    @GetMapping("/dashboard")
    @Transactional
    public ResponseEntity<DashboardDTO> getDashboard(@AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepo.findByUsernameWithStudyProgram(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<UserTask> tasks = userTaskRepo.findByUserId(user.getId());

        DashboardDTO dashboardData = DashboardDTO.fromUserAndTasks(user, tasks);

        return ResponseEntity.ok(dashboardData);
    }

    @PostMapping("/tasks/{userTaskId}/complete")
    public ResponseEntity<String> completeTask(@PathVariable Long userTaskId,
                                               @AuthenticationPrincipal UserDetails userDetails) {

        UserTask ut = userTaskRepo.findById(userTaskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        if (!ut.getUser().getUsername().equals(userDetails.getUsername())) {
            return ResponseEntity.status(403).body("This task does not belong to you");
        }

        if ("COMPLETED".equals(ut.getStatus())) {
            return ResponseEntity.badRequest().body("Task already completed");
        }

        ut.setStatus("COMPLETED");
        ut.setCompletedAt(LocalDateTime.now());
        userTaskRepo.save(ut);

        gamificationService.processRewards(ut.getUser().getId(), ut.getTask().getXpReward());

        return ResponseEntity.ok("Task completed. Rewards processing in background.");
    }
}