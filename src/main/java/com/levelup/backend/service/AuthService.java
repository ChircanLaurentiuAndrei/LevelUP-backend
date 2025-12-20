package com.levelup.backend.service;

import com.levelup.backend.dto.AuthResponse;
import com.levelup.backend.dto.LoginRequest;
import com.levelup.backend.dto.RegisterRequest;
import com.levelup.backend.entity.User;
import com.levelup.backend.entity.StudyProgram;
import com.levelup.backend.repository.UserRepository;
import com.levelup.backend.repository.StudyProgramRepository;
import com.levelup.backend.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private StudyProgramRepository studyProgramRepo;

    @Autowired
    private TaskService taskService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtils jwtUtils;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepo.findByUsername(req.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (userRepo.findByEmail(req.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already used");
        }

        StudyProgram sp = studyProgramRepo.findById(req.getStudyProgramId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid study program id"));

        User user = new User();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setStudyProgram(sp);
        user.setCurrentXp(0);
        user.setCurrentLevel(1);
        user.setLastLoginAt(LocalDateTime.now());

        user = userRepo.save(user);

        taskService.assignDailyTasks(user);

        String token = jwtUtils.generateToken(user.getUsername());
        return new AuthResponse(token, user.getUsername(), user.getId());
    }

    @Transactional
    public AuthResponse login(LoginRequest req) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );

        User user = userRepo.findByUsername(req.getUsername()).orElseThrow();

        LocalDateTime lastLoginTs = user.getLastLoginAt();
        LocalDate today = LocalDate.now();

        if (lastLoginTs != null && lastLoginTs.toLocalDate().isBefore(today)) {

            taskService.cleanupPendingTasks(user.getId());
            taskService.assignDailyTasks(user);

            LocalDate lastLoginDate = lastLoginTs.toLocalDate();
            if (lastLoginDate.isBefore(today.minusDays(1))) {
                user.setStreak(1);
            } else {
                user.setStreak(user.getStreak() + 1);
            }
        }
        else if (lastLoginTs == null) {
            user.setStreak(1);
        }

        user.setLastLoginAt(LocalDateTime.now());
        userRepo.save(user);

        String token = jwtUtils.generateToken(user.getUsername());
        return new AuthResponse(token, user.getUsername(), user.getId());
    }
}