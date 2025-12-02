package com.levelup.backend.repository;

import com.levelup.backend.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByStudyProgramIdOrStudyProgramIsNull(Long studyProgramId);

    List<Task> findByCategoryIgnoreCase(String category);
}