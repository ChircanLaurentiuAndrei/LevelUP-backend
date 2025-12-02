package com.levelup.backend.repository;

import com.levelup.backend.entity.StudyProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyProgramRepository extends JpaRepository<StudyProgram, Long> {
    // StudyProgram findByName(String name);
}