package com.levelup.backend.controller;

import com.levelup.backend.dto.DashboardDTO;
import com.levelup.backend.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDTO> getDashboard(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(taskService.getDashboard(userDetails.getUsername()));
    }

    @PostMapping("/tasks/{userTaskId}/complete")
    public ResponseEntity<?> completeTask(@PathVariable Long userTaskId,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        taskService.completeTask(userTaskId, userDetails.getUsername());
        return ResponseEntity.ok(Map.of(
                "message", "Task submitted for verification",
                "status", "VERIFYING"
        ));
    }
}
