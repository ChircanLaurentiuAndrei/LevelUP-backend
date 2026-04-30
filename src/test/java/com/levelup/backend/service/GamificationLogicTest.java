package com.levelup.backend.service;

import com.levelup.backend.config.GamificationProperties;
import com.levelup.backend.entity.Achievement;
import com.levelup.backend.entity.User;
import com.levelup.backend.enums.AchievementType;
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
import java.util.List;
import java.time.LocalDateTime;
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

    @Test
    void testFirstLoginStreak() {
        User user = new User();
        user.setId(TEST_USER_ID);
        user.setStreak(0);
        user.setLastLoginAt(null);
        user.setUnlockedAchievements(new HashSet<>());

        when(userRepo.findByIdWithLock(TEST_USER_ID)).thenReturn(Optional.of(user));
        when(achievementRepo.findAll()).thenReturn(new ArrayList<>());
        when(userTaskRepo.countByUserIdAndStatus(anyLong(), any())).thenReturn(0L);

        gamificationService.processLogin(TEST_USER_ID);

        assertEquals(1, user.getStreak());
        assertNotNull(user.getLastLoginAt());
        verify(userRepo).save(user);
    }

    @Test
    void testConsecutiveLoginStreak() {
        User user = new User();
        user.setId(TEST_USER_ID);
        user.setStreak(1);
        user.setLastLoginAt(LocalDateTime.now().minusDays(1));
        user.setUnlockedAchievements(new HashSet<>());

        when(userRepo.findByIdWithLock(TEST_USER_ID)).thenReturn(Optional.of(user));
        when(achievementRepo.findAll()).thenReturn(new ArrayList<>());
        when(userTaskRepo.countByUserIdAndStatus(anyLong(), any())).thenReturn(0L);

        gamificationService.processLogin(TEST_USER_ID);

        assertEquals(2, user.getStreak());
        verify(userRepo).save(user);
    }

    @Test
    void testSameDayLoginStreak() {
        User user = new User();
        user.setId(TEST_USER_ID);
        user.setStreak(2);
        user.setLastLoginAt(LocalDateTime.now());
        user.setUnlockedAchievements(new HashSet<>());

        when(userRepo.findByIdWithLock(TEST_USER_ID)).thenReturn(Optional.of(user));
        when(achievementRepo.findAll()).thenReturn(new ArrayList<>());
        when(userTaskRepo.countByUserIdAndStatus(anyLong(), any())).thenReturn(0L);

        gamificationService.processLogin(TEST_USER_ID);

        assertEquals(2, user.getStreak(), "Streak should not change on same day login");
        verify(userRepo).save(user);
    }

    @Test
    void testMissedDayLoginStreak() {
        User user = new User();
        user.setId(TEST_USER_ID);
        user.setStreak(5);
        user.setLastLoginAt(LocalDateTime.now().minusDays(2));
        user.setUnlockedAchievements(new HashSet<>());

        when(userRepo.findByIdWithLock(TEST_USER_ID)).thenReturn(Optional.of(user));
        when(achievementRepo.findAll()).thenReturn(new ArrayList<>());
        when(userTaskRepo.countByUserIdAndStatus(anyLong(), any())).thenReturn(0L);

        gamificationService.processLogin(TEST_USER_ID);

        assertEquals(1, user.getStreak(), "Streak should reset to 1 after missing a day");
        verify(userRepo).save(user);
    }

    @Test
    void testStreakAchievementUnlocked() {
        User user = new User();
        user.setId(TEST_USER_ID);
        user.setStreak(2); // Will become 3
        user.setLastLoginAt(LocalDateTime.now().minusDays(1));
        user.setUnlockedAchievements(new HashSet<>());

        Achievement streakAch = new Achievement();
        streakAch.setId(10L);
        streakAch.setCriteriaType(AchievementType.STREAK_DAYS);
        streakAch.setConditionValue(3);

        when(userRepo.findByIdWithLock(TEST_USER_ID)).thenReturn(Optional.of(user));
        when(achievementRepo.findAll()).thenReturn(List.of(streakAch));
        when(userTaskRepo.countByUserIdAndStatus(anyLong(), any())).thenReturn(0L);

        gamificationService.processLogin(TEST_USER_ID);

        assertEquals(3, user.getStreak());
        assertTrue(user.getUnlockedAchievements().contains(streakAch), "Streak achievement should be unlocked");
        verify(userRepo).save(user);
    }
}
