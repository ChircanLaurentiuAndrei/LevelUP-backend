package com.levelup.backend.controller;

import com.levelup.backend.entity.StudyProgram;
import com.levelup.backend.repository.StudyProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final StudyProgramRepository studyProgramRepo;

    @GetMapping("/study-programs")
    public ResponseEntity<List<StudyProgram>> getStudyPrograms() {
        return ResponseEntity.ok(studyProgramRepo.findAll());
    }
}
