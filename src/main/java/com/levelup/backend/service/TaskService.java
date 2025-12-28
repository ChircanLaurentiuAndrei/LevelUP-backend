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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TaskService {

    private static final int DAILY_TASK_LIMIT = 8;
    private static final int MIN_PROGRAM_TASKS = 4;
    @Autowired
    private TaskRepository taskRepo;
    @Autowired
    private UserTaskRepository userTaskRepo;

    @Transactional
    public void assignDailyTasks(User user) {
        Long studyProgramId = (user.getStudyProgram() != null) ? user.getStudyProgram().getId() : null;
        List<Task> selectedTasks = new ArrayList<>();

        if (studyProgramId != null) {
            List<Task> programTasks = taskRepo.findRandomTasksByProgram(studyProgramId, MIN_PROGRAM_TASKS);
            selectedTasks.addAll(programTasks);
        }

        int remainingSlots = DAILY_TASK_LIMIT - selectedTasks.size();

        if (remainingSlots > 0) {
            List<Task> globalTasks = taskRepo.findRandomGlobalTasks(remainingSlots);
            selectedTasks.addAll(globalTasks);
        }

        Collections.shuffle(selectedTasks);

        for (Task task : selectedTasks) {
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