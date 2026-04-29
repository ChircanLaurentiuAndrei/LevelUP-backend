package com.levelup.backend.dto;

import com.levelup.backend.entity.UserTask;
import com.levelup.backend.enums.TaskStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record UserTaskDTO(
    Long userTaskId,
    TaskStatus status,
    LocalDate assignedDate,
    LocalDateTime completedAt,
    TaskDetailDTO task
) {
    public static UserTaskDTO fromEntity(UserTask userTask) {
        return new UserTaskDTO(
            userTask.getId(),
            userTask.getStatus(),
            userTask.getAssignedDate(),
            userTask.getCompletedAt(),
            TaskDetailDTO.fromEntity(userTask.getTask())
        );
    }
}
