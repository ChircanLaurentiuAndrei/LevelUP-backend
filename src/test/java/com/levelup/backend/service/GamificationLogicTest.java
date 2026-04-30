package com.levelup.backend.service;

import com.levelup.backend.config.GamificationProperties;
import com.levelup.backend.entity.User;
import com.levelup.backend.repository.AchievementRepository;
import com.levelup.backend.repository.UserRepository;
import com.levelup.backend.repository.UserTaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import java.util.HashSet;
import java.util.Optional;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GamificationLogicTest {

    private final Long TEST_USER_ID = 1L;

    @Mock
    private UserRepository userRepo;

    @Mock
    private AchievementRepository achievementRepo;

    @Mock
    private UserTaskRepository userTaskRepo;

    @Mock
    private GamificationProperties props;

    @Mock
    private ApplicationContext applicationContext;

    @InjectMocks
    private GamificationService gamificationService;

    @BeforeEach
    void setup() {
        lenient().when(props.xpPerLevel()).thenReturn(100);
        lenient().when(applicationContext.getBean(GamificationService.class)).thenReturn(gamificationService);
    }

    @Test
    void testXpGainAndLevelUp() {
        User user = new User();
        user.setId(TEST_USER_ID);
        user.setCurrentLevel(1);
        user.setCurrentXp(80);
        user.setUnlockedAchievements(new HashSet<>());

        when(userRepo.findByIdWithLock(TEST_USER_ID)).thenReturn(Optional.of(user));
        when(achievementRepo.findAll()).thenReturn(new ArrayList<>());

        gamificationService.processRewards(TEST_USER_ID, 30);

        assertEquals(110, user.getCurrentXp());
        assertEquals(2, user.getCurrentLevel(), "User should level up to Level 2 after crossing 100 XP");

        verify(userRepo, times(1)).save(user);
    }

    @Test
    void testXpGainNoLevelUp() {
        User user = new User();
        user.setId(TEST_USER_ID);
        user.setCurrentLevel(1);
        user.setCurrentXp(50);
        user.setUnlockedAchievements(new HashSet<>());

        when(userRepo.findByIdWithLock(TEST_USER_ID)).thenReturn(Optional.of(user));
        when(achievementRepo.findAll()).thenReturn(new ArrayList<>());

        gamificationService.processRewards(TEST_USER_ID, 20);

        assertEquals(70, user.getCurrentXp());
        assertEquals(1, user.getCurrentLevel(), "User should remain at Level 1");
    }
}
