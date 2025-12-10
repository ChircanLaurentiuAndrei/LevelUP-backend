package com.levelup.backend.controller;

import com.levelup.backend.dto.AuthResponse;
import com.levelup.backend.dto.LoginRequest;
import com.levelup.backend.dto.RegisterRequest;
import com.levelup.backend.service.AuthService;
import com.levelup.backend.repository.StudyProgramRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private StudyProgramRepository studyProgramRepo;

    @GetMapping("/study-programs")
    public ResponseEntity<?> getStudyPrograms() {
        return ResponseEntity.ok(studyProgramRepo.findAll());
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        try {
            AuthResponse resp = authService.register(req);
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("Server error");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        try {
            AuthResponse resp = authService.login(req);
            return ResponseEntity.ok(resp);
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).body("Invalid credentials");
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("Server error");
        }
    }
}