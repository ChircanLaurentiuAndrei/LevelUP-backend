package com.levelup.backend.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private Integer currentLevel;
    private Integer currentXp;
    private Integer streak;
    private String studyProgram;
    private LocalDate lastLoginDate;
}
