package com.levelup.backend.entity;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "study_program")
@Data
public class StudyProgram {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
}
