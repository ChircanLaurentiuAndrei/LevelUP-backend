package com.levelup.backend.service;

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
import java.util.UUID;

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

        LocalDate loginDate = (user.getUpdatedAt() != null)
                ? user.getUpdatedAt().toLocalDate()
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
    public List<UserDTO> getLeaderboard() {
        return userRepo.findLeaderboard("ADMIN").stream()
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
                .toList();
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @Transactional
    public User updateUser(UUID id, User updates) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        if (updates.getUsername() != null && !updates.getUsername().equals(user.getUsername())) {
            Optional<User> existing = userRepo.findByUsername(updates.getUsername());
            if (existing.isPresent()) {
                throw new BusinessException("Username '" + updates.getUsername() + "' is already taken.");
            }
            user.setUsername(updates.getUsername());
        }

        if (updates.getCurrentLevel() != null) {
            if (updates.getCurrentLevel() < 1) {
                throw new BusinessException("Level cannot be less than 1.");
            }
            user.setCurrentLevel(updates.getCurrentLevel());
        }

        if (updates.getCurrentXp() != null) {
            if (updates.getCurrentXp() < 0) {
                throw new BusinessException("XP cannot be negative.");
            }
            user.setCurrentXp(updates.getCurrentXp());
        }

        if (updates.getStreak() != null) {
            if (updates.getStreak() < 0) {
                throw new BusinessException("Streak cannot be negative.");
            }
            user.setStreak(updates.getStreak());
        }

        if (updates.getRole() != null) {
            String newRole = updates.getRole().toUpperCase();
            if (!newRole.equals("USER") && !newRole.equals("ADMIN")) {
                throw new BusinessException("Invalid role. Must be 'USER' or 'ADMIN'.");
            }
            user.setRole(newRole);
        }

        return userRepo.save(user);
    }

    @Transactional
    public void deleteUser(UUID id) {
        if (!userRepo.existsById(id)) {
            throw new ResourceNotFoundException("User not found with ID: " + id);
        }
        userRepo.deleteById(id);
    }
}
