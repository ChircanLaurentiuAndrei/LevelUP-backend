package com.levelup.backend.dto;

import com.levelup.backend.entity.Task;

public record TaskDetailDTO(
    Long id,
    String title,
    String description,
    String category,
    Integer xpReward
) {
    public static TaskDetailDTO fromEntity(Task task) {
        return new TaskDetailDTO(
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.getCategory(),
            task.getXpReward()
        );
    }
}
