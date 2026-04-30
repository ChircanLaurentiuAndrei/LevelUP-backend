package com.levelup.backend.service;

import com.levelup.backend.config.GamificationProperties;
import com.levelup.backend.dto.DashboardDTO;
import com.levelup.backend.entity.Task;
import com.levelup.backend.entity.User;
import com.levelup.backend.entity.UserTask;
import com.levelup.backend.exception.TaskAlreadyCompletedException;
import com.levelup.backend.exception.UnauthorizedActionException;
import com.levelup.backend.repository.TaskRepository;
import com.levelup.backend.repository.UserRepository;
import com.levelup.backend.repository.UserTaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock private GamificationProperties props;
    @Mock private TaskRepository taskRepo;
    @Mock private UserTaskRepository userTaskRepo;
    @Mock private UserRepository userRepo;
    @Mock private VerificationService verificationService;

    @InjectMocks private TaskService taskService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setUnlockedAchievements(new java.util.HashSet<>());
    }

    @Test
    void getDashboard_ShouldReturnOnlyTodaysTasks() {
        when(userRepo.findByUsernameWithStudyProgram("testuser")).thenReturn(Optional.of(user));
        when(props.xpPerLevel()).thenReturn(100);
        
        UserTask todayTask = new UserTask();
        todayTask.setAssignedDate(LocalDate.now());
        todayTask.setTask(new Task());
        
        when(userTaskRepo.findByUserIdAndAssignedDateWithTask(eq(1L), eq(LocalDate.now())))
                .thenReturn(List.of(todayTask));

        DashboardDTO dashboard = taskService.getDashboard("testuser");

        assertNotNull(dashboard);
        assertEquals(1, dashboard.tasks().size());
        verify(userTaskRepo).findByUserIdAndAssignedDateWithTask(eq(1L), eq(LocalDate.now()));
    }

    @Test
    void completeTask_ShouldThrowUnauthorizedActionException_WhenWrongUser() {
        User otherUser = new User();
        otherUser.setUsername("otheruser");

        UserTask ut = new UserTask();
        ut.setUser(otherUser);

        when(userTaskRepo.findById(1L)).thenReturn(Optional.of(ut));

        assertThrows(UnauthorizedActionException.class, () -> 
            taskService.completeTask(1L, "testuser")
        );
    }

    @Test
    void completeTask_ShouldThrowTaskAlreadyCompletedException_WhenTaskNotPending() {
        UserTask ut = new UserTask();
        ut.setUser(user);

        when(userTaskRepo.findById(1L)).thenReturn(Optional.of(ut));
        when(userTaskRepo.updateStatusIfPending(anyLong(), any(), any(), any())).thenReturn(0);

        assertThrows(TaskAlreadyCompletedException.class, () -> 
            taskService.completeTask(1L, "testuser")
        );
    }
}
