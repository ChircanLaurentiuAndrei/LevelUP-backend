package com.levelup.backend.service;

import com.levelup.backend.dto.AuthResponse;
import com.levelup.backend.dto.LoginRequest;
import com.levelup.backend.dto.RegisterRequest;
import com.levelup.backend.entity.Task;
import com.levelup.backend.entity.User;
import com.levelup.backend.entity.UserTask;
import com.levelup.backend.entity.StudyProgram;
import com.levelup.backend.repository.TaskRepository;
import com.levelup.backend.repository.UserRepository;
import com.levelup.backend.repository.UserTaskRepository;
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
import java.util.List;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private StudyProgramRepository studyProgramRepo;
    @Autowired
    private TaskRepository taskRepo;
    @Autowired
    private UserTaskRepository userTaskRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtils jwtUtils;

    // CONSTANT for the number of daily tasks
    private static final int DAILY_TASK_LIMIT = 8;

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

        assignRandomTasks(user, sp.getId());

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

        // CHECK NEW DAY LOGIC
        if (lastLoginTs != null && lastLoginTs.toLocalDate().isBefore(today)) {

            // 1. CLEAR YESTERDAY'S UNFINISHED TASKS
            // We keep "COMPLETED" tasks for history/achievements, but remove "PENDING"
            userTaskRepo.deletePendingTasksByUserId(user.getId());

            // 2. ASSIGN 8 NEW RANDOM TASKS FOR TODAY
            assignRandomTasks(user, user.getStudyProgram().getId());

            // 3. STREAK LOGIC
            LocalDate lastLoginDate = lastLoginTs.toLocalDate();
            if (lastLoginDate.isBefore(today.minusDays(1))) {
                user.setStreak(1); // Reset streak
            } else {
                user.setStreak(user.getStreak() + 1); // Increment streak
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

    // Helper method to keep code clean
    private void assignRandomTasks(User user, Long studyProgramId) {
        List<Task> randomTasks = taskRepo.findRandomTasks(studyProgramId, DAILY_TASK_LIMIT);

        for (Task task : randomTasks) {
            UserTask assignment = new UserTask();
            assignment.setUser(user);
            assignment.setTask(task);
            assignment.setStatus("PENDING");
            assignment.setAssignedDate(LocalDate.now());
            userTaskRepo.save(assignment);
        }
    }
}