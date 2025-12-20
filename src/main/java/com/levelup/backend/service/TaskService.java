package com.levelup.backend.service;

import com.levelup.backend.entity.Task;
import com.levelup.backend.entity.User;
import com.levelup.backend.entity.UserTask;
import com.levelup.backend.repository.TaskRepository;
import com.levelup.backend.repository.UserTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepo;

    @Autowired
    private UserTaskRepository userTaskRepo;

    private static final int DAILY_TASK_LIMIT = 8;

    @Transactional
    public void assignDailyTasks(User user) {
        Long studyProgramId = (user.getStudyProgram() != null) ? user.getStudyProgram().getId() : null;

        List<Task> randomTasks = taskRepo.findRandomTasks(studyProgramId, DAILY_TASK_LIMIT);

        for (Task task : randomTasks) {
            UserTask assignment = new UserTask();
            assignment.setUser(user);
            assignment.setTask(task);
            assignment.setStatus("PENDING");
            assignment.setAssignedDate(LocalDate.now());
            userTaskRepo.save(assignment);
        }
    }

    @Transactional
    public void cleanupPendingTasks(Long userId) {
        userTaskRepo.deletePendingTasksByUserId(userId);
    }
}