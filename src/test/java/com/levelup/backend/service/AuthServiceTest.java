package com.levelup.backend.service;

import com.levelup.backend.dto.LoginRequest;
import com.levelup.backend.dto.RegisterRequest;
import com.levelup.backend.entity.User;
import com.levelup.backend.exception.BusinessException;
import com.levelup.backend.repository.StudyProgramRepository;
import com.levelup.backend.repository.UserRepository;
import com.levelup.backend.security.JwtUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private StudyProgramRepository studyProgramRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtils jwtUtils;
    @Mock private TaskService taskService;

    @InjectMocks private AuthService authService;

    @Test
    void login_ShouldThrowBadCredentialsException_WhenWrongPassword() {
        User user = new User();
        user.setPassword("encodedPassword");
        
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        LoginRequest request = new LoginRequest("testuser", "wrongPassword");

        assertThrows(BadCredentialsException.class, () -> 
            authService.login(request)
        );
    }

    @Test
    void register_ShouldThrowBusinessException_WhenDuplicateUsername() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(new User()));

        RegisterRequest request = new RegisterRequest("testuser", "email@test.com", "password", 1L);

        assertThrows(BusinessException.class, () -> 
            authService.register(request)
        );
    }
}
