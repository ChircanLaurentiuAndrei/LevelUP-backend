package com.levelup.backend.service;

import com.levelup.backend.entity.StudyProgram;
import com.levelup.backend.repository.StudyProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyProgramService {

    private final StudyProgramRepository studyProgramRepo;

    @Transactional(readOnly = true)
    public List<StudyProgram> getAllStudyPrograms() {
        return studyProgramRepo.findAll();
    }
}
