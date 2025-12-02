package com.levelup.backend.entity;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_tasks")
@Data
public class UserTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ColumnDefault("'PENDING'")
    private String status = "PENDING";

    private LocalDateTime assignedAt = LocalDateTime.now();
    private LocalDateTime completedAt;
}
