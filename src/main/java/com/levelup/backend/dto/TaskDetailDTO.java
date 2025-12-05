package com.levelup.backend.dto;

import com.levelup.backend.entity.Task;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TaskDetailDTO {
    Long id;
    String title;
    String category;
    Integer xpReward;
    public static TaskDetailDTO fromEntity(Task task) {
        return TaskDetailDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .category(task.getCategory())
                .xpReward(task.getXpReward())
                .build();
    }
}
