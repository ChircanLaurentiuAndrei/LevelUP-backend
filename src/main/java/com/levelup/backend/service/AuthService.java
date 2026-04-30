package com.levelup.backend.service;

import com.levelup.backend.dto.AuthResponse;
import com.levelup.backend.dto.LoginRequest;
import com.levelup.backend.dto.RegisterRequest;
import com.levelup.backend.entity.StudyProgram;
import com.levelup.backend.entity.User;
import com.levelup.backend.exception.BusinessException;
import com.levelup.backend.exception.ResourceNotFoundException;
import com.levelup.backend.repository.StudyProgramRepository;
import com.levelup.backend.repository.UserRepository;
import com.levelup.backend.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final StudyProgramRepository studyProgramRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final TaskService taskService;
    private final GamificationService gamificationService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new BusinessException("Username already exists");
        }
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new BusinessException("Email already exists");
        }

        StudyProgram studyProgram = studyProgramRepository.findById(request.studyProgramId())
                .orElseThrow(() -> new ResourceNotFoundException("Study program not found"));

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setFullName(request.username()); // Default to username
        user.setAvatarUrl("https://api.dicebear.com/7.x/avataaars/svg?seed=" + request.username());
        user.setRole("USER");
        user.setStudyProgram(studyProgram);
        
        User savedUser = userRepository.save(user);
        
        // Assign initial tasks
        taskService.assignDailyTasks(savedUser);

        // Handle initial login gamification
        gamificationService.processLogin(savedUser.getId());

        String token = jwtUtils.generateToken(savedUser);
        return new AuthResponse(token, savedUser.getUsername(), savedUser.getId(), savedUser.getRole());
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        // Process login (updates streak, lastLoginAt, and checks achievements)
        gamificationService.processLogin(user.getId());

        String token = jwtUtils.generateToken(user);
        return new AuthResponse(token, user.getUsername(), user.getId(), user.getRole());
    }
}
