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
        user.setLastLoginDate(LocalDate.now());
        user = userRepo.save(user);

        List<Task> initialTasks = taskRepo.findByStudyProgramIdOrStudyProgramIsNull(sp.getId());

        for (Task task : initialTasks) {
            UserTask assignment = new UserTask();
            assignment.setUser(user);
            assignment.setTask(task);
            assignment.setStatus("PENDING");
            userTaskRepo.save(assignment);
        }

        String token = jwtUtils.generateToken(user.getUsername());
        return new AuthResponse(token, user.getUsername(), user.getId());
    }

    @Transactional
    public AuthResponse login(LoginRequest req) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );

        User user = userRepo.findByUsername(req.getUsername()).orElseThrow();

        if (user.getLastLoginDate() != null && user.getLastLoginDate().isBefore(LocalDate.now())) {

            List<UserTask> tasks = userTaskRepo.findByUserId(user.getId());
            for (UserTask ut : tasks) {
                ut.setStatus("PENDING");
                ut.setCompletedAt(null);
                userTaskRepo.save(ut);
            }

            if (user.getLastLoginDate().isBefore(LocalDate.now().minusDays(1))) {
                user.setStreak(0);
            }
        }

        user.setLastLoginDate(LocalDate.now());
        userRepo.save(user);

        String token = jwtUtils.generateToken(user.getUsername());
        return new AuthResponse(token, user.getUsername(), user.getId());
    }
}