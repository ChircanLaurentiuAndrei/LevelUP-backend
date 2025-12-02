package com.levelup.backend.entity;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tasks")
@Data
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer xpReward;

    private String category;

    @ManyToOne
    @JoinColumn(name = "study_program_id")
    private StudyProgram studyProgram;
}
