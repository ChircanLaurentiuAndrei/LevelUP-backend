package com.levelup.backend.dto;

import com.levelup.backend.entity.UserTask;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;       // Changed from LocalDateTime
import java.time.LocalDateTime;

@Value
@Builder
public class UserTaskDTO {
    Long userTaskId;
    String status;

    LocalDate assignedDate;

    LocalDateTime completedAt;
    TaskDetailDTO task;

    public static UserTaskDTO fromEntity(UserTask userTask) {
        return UserTaskDTO.builder()
                .userTaskId(userTask.getId())
                .status(userTask.getStatus())
                .assignedDate(userTask.getAssignedDate())
                .completedAt(userTask.getCompletedAt())
                .task(TaskDetailDTO.fromEntity(userTask.getTask()))
                .build();
    }
}