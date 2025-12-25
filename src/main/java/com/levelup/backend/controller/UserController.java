package com.levelup.backend.controller;

import com.levelup.backend.dto.UserDTO;
import com.levelup.backend.entity.Achievement;
import com.levelup.backend.entity.User;
import com.levelup.backend.repository.AchievementRepository;
import com.levelup.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate; // Import needed for conversion
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private AchievementRepository achievementRepo;

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        User user = userRepo.findByUsernameWithAchievements(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Long> unlockedIds = user.getUnlockedAchievements().stream()
                .map(Achievement::getId)
                .collect(Collectors.toList());

        LocalDate loginDate = (user.getLastLoginAt() != null)
                ? user.getLastLoginAt().toLocalDate()
                : null;

        UserDTO dto = new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCurrentLevel(),
                user.getCurrentXp(),
                user.getStreak(),
                user.getStudyProgram() != null ? user.getStudyProgram().getName() : null,
                loginDate,
                unlockedIds
        );

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/achievements")
    public ResponseEntity<?> getAllAchievements() {
        return ResponseEntity.ok(achievementRepo.findAll());
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<UserDTO>> getLeaderboard() {
        return ResponseEntity.ok(userRepo.findByRoleNotOrderByCurrentXpDesc("ADMIN").stream()
                .map(u -> new UserDTO(
                        u.getId(),
                        u.getUsername(),
                        null,
                        u.getCurrentLevel(),
                        u.getCurrentXp(),
                        u.getStreak(),
                        null,
                        null,
                        null
                ))
                .toList());
    }
}