package com.levelup.backend.service;

import com.levelup.backend.entity.User;
import com.levelup.backend.repository.AchievementRepository;
import com.levelup.backend.repository.UserRepository;
import com.levelup.backend.repository.UserTaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GamificationLogicTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private AchievementRepository achievementRepo;

    @Mock
    private UserTaskRepository userTaskRepo;

    @InjectMocks
    private GamificationService gamificationService;

    @Test
    void testXpGainAndLevelUp() {
        User user = new User();
        user.setId(1L);
        user.setCurrentLevel(1);
        user.setCurrentXp(80);
        user.setUnlockedAchievements(new HashSet<>());

        when(userRepo.findByIdWithLock(1L)).thenReturn(Optional.of(user));
        when(achievementRepo.findAll()).thenReturn(new ArrayList<>());

        gamificationService.processRewards(1L, 30);

        assertEquals(110, user.getCurrentXp());
        assertEquals(2, user.getCurrentLevel(), "User should level up to Level 2 after crossing 100 XP");

        verify(userRepo, times(1)).save(user);
    }

    @Test
    void testXpGainNoLevelUp() {
        User user = new User();
        user.setId(1L);
        user.setCurrentLevel(1);
        user.setCurrentXp(50);
        user.setUnlockedAchievements(new HashSet<>());

        when(userRepo.findByIdWithLock(1L)).thenReturn(Optional.of(user));
        when(achievementRepo.findAll()).thenReturn(new ArrayList<>());

        gamificationService.processRewards(1L, 20);

        assertEquals(70, user.getCurrentXp());
        assertEquals(1, user.getCurrentLevel(), "User should remain at Level 1");
    }
}