package com.levelup.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @Column(nullable = false, unique = true)
    private String username;

    @Email(message = "Invalid email format")
    @Column(unique = true)
    private String email;

    @JsonIgnore
    private String password;

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

    @NotBlank(message = "Role cannot be blank")
    @Column(nullable = false)
    private String role = "USER";
}
