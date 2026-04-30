package com.levelup.backend.service;

import com.levelup.backend.config.GamificationProperties;
import com.levelup.backend.dto.DashboardDTO;
import com.levelup.backend.entity.Task;
import com.levelup.backend.entity.User;
import com.levelup.backend.entity.UserTask;
import com.levelup.backend.enums.TaskStatus;
import com.levelup.backend.exception.ResourceNotFoundException;
import com.levelup.backend.exception.TaskAlreadyCompletedException;
import com.levelup.backend.exception.UnauthorizedActionException;
import com.levelup.backend.repository.TaskRepository;
import com.levelup.backend.repository.UserRepository;
import com.levelup.backend.repository.UserTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final GamificationProperties props;
    private final TaskRepository taskRepo;
    private final UserTaskRepository userTaskRepo;
    private final UserRepository userRepo;
    private final VerificationService verificationService;

    @Transactional(readOnly = true)
    public DashboardDTO getDashboard(String username) {
        User user = userRepo.findByUsernameWithStudyProgram(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        List<UserTask> tasks = userTaskRepo.findByUserIdWithTask(user.getId());
        return DashboardDTO.fromUserAndTasks(user, tasks, props.xpPerLevel());
    }

    @Transactional
    public void assignDailyTasks(User user) {
        Long studyProgramId = (user.getStudyProgram() != null) ? user.getStudyProgram().getId() : null;
        List<Task> selectedTasks = new ArrayList<>();

        if (studyProgramId != null) {
            List<Task> programTasks = taskRepo.findRandomTasksByProgram(studyProgramId, props.minProgramTasks());
            selectedTasks.addAll(programTasks);
        }

        int remainingSlots = props.dailyTaskLimit() - selectedTasks.size();

        if (remainingSlots > 0) {
            List<Task> globalTasks = taskRepo.findRandomGlobalTasks(remainingSlots);
            selectedTasks.addAll(globalTasks);
        }

        Collections.shuffle(selectedTasks);

        for (Task task : selectedTasks) {
            UserTask assignment = new UserTask();
            assignment.setUser(user);
            assignment.setTask(task);
            assignment.setStatus(TaskStatus.PENDING);
            assignment.setAssignedDate(LocalDate.now());
            userTaskRepo.save(assignment);
        }
    }

    @Transactional
    public void completeTask(Long userTaskId, String username) {
        UserTask ut = userTaskRepo.findById(userTaskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + userTaskId));

        if (!ut.getUser().getUsername().equals(username)) {
            throw new UnauthorizedActionException("This task does not belong to you");
        }

        int updatedRows = userTaskRepo.updateStatusIfPending(userTaskId, TaskStatus.VERIFYING, TaskStatus.PENDING, LocalDateTime.now());

        if (updatedRows == 0) {
            throw new TaskAlreadyCompletedException("Task is already completed or under verification");
        }

        verificationService.verifyTaskInBackground(ut.getId());
    }

    @Transactional
    public void cleanupPendingTasks(UUID userId) {
        userTaskRepo.deleteTasksByUserIdAndStatus(userId, TaskStatus.PENDING);
    }
}
