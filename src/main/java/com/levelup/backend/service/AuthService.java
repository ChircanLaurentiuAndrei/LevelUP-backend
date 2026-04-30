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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final StudyProgramRepository studyProgramRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final TaskService taskService;

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
        user.setId(UUID.randomUUID());
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setFullName(request.username()); // Default to username
        user.setAvatarUrl("https://api.dicebear.com/7.x/avataaars/svg?seed=" + request.username());
        user.setRole("USER");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setStudyProgram(studyProgram);
        
        User savedUser = userRepository.save(user);
        
        // Assign initial tasks
        taskService.assignDailyTasks(savedUser);

        String token = jwtUtils.generateToken(savedUser);
        return new AuthResponse(token, savedUser.getUsername(), savedUser.getId(), savedUser.getRole());
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BusinessException("Invalid username or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException("Invalid username or password");
        }

        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtUtils.generateToken(user);
        return new AuthResponse(token, user.getUsername(), user.getId(), user.getRole());
    }
}
