package com.levelup.backend.entity;

import com.levelup.backend.enums.AchievementType;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "achievements")
@Data
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private AchievementType criteriaType;

    private Integer conditionValue;
}
