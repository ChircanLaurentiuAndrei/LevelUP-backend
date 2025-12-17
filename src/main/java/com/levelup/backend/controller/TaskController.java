package com.levelup.backend.controller;

import com.levelup.backend.dto.DashboardDTO;
import com.levelup.backend.entity.User;
import com.levelup.backend.entity.UserTask;
import com.levelup.backend.repository.UserRepository;
import com.levelup.backend.repository.UserTaskRepository;
import com.levelup.backend.service.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TaskController {

    @Autowired
    private UserTaskRepository userTaskRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private VerificationService verificationService;

    @GetMapping("/dashboard")
    @Transactional(readOnly = true)
    public ResponseEntity<DashboardDTO> getDashboard(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepo.findByUsernameWithStudyProgram(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<UserTask> tasks = userTaskRepo.findByUserId(user.getId());
        return ResponseEntity.ok(DashboardDTO.fromUserAndTasks(user, tasks));
    }

    @PostMapping("/tasks/{userTaskId}/complete")
    @Transactional
    public ResponseEntity<?> completeTask(@PathVariable Long userTaskId,
                                          @AuthenticationPrincipal UserDetails userDetails) {

        UserTask ut = userTaskRepo.findById(userTaskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        if (!ut.getUser().getUsername().equals(userDetails.getUsername())) {
            return ResponseEntity.status(403).body("This task does not belong to you");
        }

        int updatedRows = userTaskRepo.updateStatusIfPending(userTaskId, "VERIFYING", LocalDateTime.now());

        if (updatedRows == 0) {
            return ResponseEntity.badRequest().body("Task is already completed or under verification");
        }

        verificationService.verifyTaskInBackground(ut.getId());

        return ResponseEntity.ok(Map.of(
                "message", "Task submitted for verification",
                "status", "VERIFYING"
        ));
    }
}