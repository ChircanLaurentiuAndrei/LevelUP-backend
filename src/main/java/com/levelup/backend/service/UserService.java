package com.levelup.backend.service;

import com.levelup.backend.dto.AdminUserDTO;
import com.levelup.backend.dto.LeaderboardEntryDTO;
import com.levelup.backend.dto.UserDTO;
import com.levelup.backend.entity.Achievement;
import com.levelup.backend.entity.User;
import com.levelup.backend.exception.BusinessException;
import com.levelup.backend.exception.ResourceNotFoundException;
import com.levelup.backend.repository.AchievementRepository;
import com.levelup.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;
    private final AchievementRepository achievementRepo;

    @Transactional(readOnly = true)
    public UserDTO getCurrentUser(String username) {
        User user = userRepo.findByUsernameWithAchievements(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        List<Long> unlockedIds = user.getUnlockedAchievements().stream()
                .map(Achievement::getId)
                .toList();

        LocalDate loginDate = (user.getLastLoginAt() != null)
                ? user.getLastLoginAt().toLocalDate()
                : null;

        return new UserDTO(
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
    }

    @Transactional(readOnly = true)
    public List<Achievement> getAllAchievements() {
        return achievementRepo.findAll();
    }

    @Transactional(readOnly = true)
    public List<LeaderboardEntryDTO> getLeaderboard() {
        return userRepo.findLeaderboard("ADMIN").stream()
                .map(u -> new LeaderboardEntryDTO(
                        u.getId(),
                        u.getUsername(),
                        u.getCurrentLevel(),
                        u.getCurrentXp(),
                        u.getStreak()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AdminUserDTO> getAllUsers() {
        return userRepo.findAll().stream()
                .map(this::mapToAdminDTO)
                .toList();
    }

    @Transactional
    public AdminUserDTO updateUser(Long id, AdminUserDTO updates) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        if (updates.username() != null && !updates.username().equals(user.getUsername())) {
            Optional<User> existing = userRepo.findByUsername(updates.username());
            if (existing.isPresent()) {
                throw new BusinessException("Username '" + updates.username() + "' is already taken.");
            }
            user.setUsername(updates.username());
        }

        if (updates.currentLevel() != null) {
            if (updates.currentLevel() < 1) {
                throw new BusinessException("Level cannot be less than 1.");
            }
            user.setCurrentLevel(updates.currentLevel());
        }

        if (updates.currentXp() != null) {
            if (updates.currentXp() < 0) {
                throw new BusinessException("XP cannot be negative.");
            }
            user.setCurrentXp(updates.currentXp());
        }

        if (updates.streak() != null) {
            if (updates.streak() < 0) {
                throw new BusinessException("Streak cannot be negative.");
            }
            user.setStreak(updates.streak());
        }

        if (updates.role() != null) {
            String newRole = updates.role().toUpperCase();
            if (!newRole.equals("USER") && !newRole.equals("ADMIN")) {
                throw new BusinessException("Invalid role. Must be 'USER' or 'ADMIN'.");
            }
            user.setRole(newRole);
        }

        return mapToAdminDTO(userRepo.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepo.existsById(id)) {
            throw new ResourceNotFoundException("User not found with ID: " + id);
        }
        userRepo.deleteById(id);
    }

    private AdminUserDTO mapToAdminDTO(User user) {
        return new AdminUserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCurrentLevel(),
                user.getCurrentXp(),
                user.getStreak(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getLastLoginAt()
        );
    }
}
