package com.levelup.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "profiles")
@Data
@JsonIgnoreProperties({"unlockedAchievements"})
public class User {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(unique = true)
    private String email;

    private String fullName;
    private String avatarUrl;

    @Column(name = "current_xp")
    private Integer currentXp = 0;

    @Column(name = "current_level")
    private Integer currentLevel = 1;

    private Integer streak = 0;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "study_program_id")
    private StudyProgram studyProgram;

    @ManyToMany
    @JoinTable(
            name = "user_achievements",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "achievement_id")
    )
    private Set<Achievement> unlockedAchievements;

    @Column(nullable = false)
    private String role = "USER";
}
