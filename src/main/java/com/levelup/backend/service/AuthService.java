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

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private StudyProgramRepository studyProgramRepo;

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
        System.out.println("All study programs in DB:");
        studyProgramRepo.findAll().forEach(sp -> System.out.println(sp.getId() + " -> " + sp.getName()));
        System.out.println("Looking for StudyProgram ID: " + req.getStudyProgramId());
        StudyProgram sp = studyProgramRepo.findById(req.getStudyProgramId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid study program id"));

        User user = new User();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setStudyProgram(sp);
        user.setCurrentXp(0);
        user.setCurrentLevel(1);
        user = userRepo.save(user);

        String token = jwtUtils.generateToken(user.getUsername());
        return new AuthResponse(token, user.getUsername(), user.getId());
    }

    public AuthResponse login(LoginRequest req) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );
        // authentication succeeded
        User u = userRepo.findByUsername(req.getUsername()).orElseThrow();
        String token = jwtUtils.generateToken(u.getUsername());
        return new AuthResponse(token, u.getUsername(), u.getId());
    }
}
