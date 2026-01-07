package com.levelup.backend.service;

import com.levelup.backend.entity.User;
import com.levelup.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepo;

    @Transactional
    public User updateUser(Long id, User updates) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));

        if (updates.getUsername() != null && !updates.getUsername().equals(user.getUsername())) {
            Optional<User> existing = userRepo.findByUsername(updates.getUsername());
            if (existing.isPresent()) {
                throw new IllegalArgumentException("Username '" + updates.getUsername() + "' is already taken.");
            }
            user.setUsername(updates.getUsername());
        }

        if (updates.getCurrentLevel() != null) {
            if (updates.getCurrentLevel() < 1) {
                throw new IllegalArgumentException("Level cannot be less than 1.");
            }
            user.setCurrentLevel(updates.getCurrentLevel());
        }

        if (updates.getCurrentXp() != null) {
            if (updates.getCurrentXp() < 0) {
                throw new IllegalArgumentException("XP cannot be negative.");
            }
            user.setCurrentXp(updates.getCurrentXp());
        }

        if (updates.getStreak() != null) {
            if (updates.getStreak() < 0) {
                throw new IllegalArgumentException("Streak cannot be negative.");
            }
            user.setStreak(updates.getStreak());
        }

        if (updates.getRole() != null) {
            String newRole = updates.getRole().toUpperCase();
            if (!newRole.equals("USER") && !newRole.equals("ADMIN")) {
                throw new IllegalArgumentException("Invalid role. Must be 'USER' or 'ADMIN'.");
            }
            user.setRole(newRole);
        }

        return userRepo.save(user);
    }
}