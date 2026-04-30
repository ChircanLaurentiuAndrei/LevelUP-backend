package com.levelup.backend.service;

import com.levelup.backend.dto.AdminUpdateRequest;
import com.levelup.backend.entity.User;
import com.levelup.backend.exception.BusinessException;
import com.levelup.backend.repository.AchievementRepository;
import com.levelup.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceLogicTest {

    @Mock private UserRepository userRepo;
    @Mock private AchievementRepository achievementRepo;

    @InjectMocks private UserService userService;

    @Test
    void updateUser_ShouldThrowBusinessException_OnInvalidRoleString() {
        User user = new User();
        user.setId(1L);
        
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));

        AdminUpdateRequest updates = new AdminUpdateRequest(null, null, null, null, "INVALID_ROLE");

        BusinessException exception = assertThrows(BusinessException.class, () -> 
            userService.updateUser(1L, updates)
        );

        assertEquals("Invalid role. Must be 'USER' or 'ADMIN'.", exception.getMessage());
    }
}
