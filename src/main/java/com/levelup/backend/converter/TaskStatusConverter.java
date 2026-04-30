package com.levelup.backend.converter;

import com.levelup.backend.enums.TaskStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TaskStatusConverter implements AttributeConverter<TaskStatus, String> {
    @Override
    public String convertToDatabaseColumn(TaskStatus attribute) {
        if (attribute == null) return null;
        return switch (attribute) {
            case TaskStatus.Pending p -> "PENDING";
            case TaskStatus.Verifying v -> "VERIFYING";
            case TaskStatus.Completed c -> "COMPLETED";
            case TaskStatus.Failed f -> "FAILED";
        };
    }

    @Override
    public TaskStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return switch (dbData) {
            case "PENDING" -> TaskStatus.PENDING;
            case "VERIFYING" -> TaskStatus.VERIFYING;
            case "COMPLETED" -> TaskStatus.COMPLETED;
            case "FAILED" -> TaskStatus.FAILED;
            default -> throw new IllegalArgumentException("Unknown DB TaskStatus: " + dbData);
        };
    }
}
