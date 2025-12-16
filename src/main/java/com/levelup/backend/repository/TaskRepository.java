package com.levelup.backend.repository;

import com.levelup.backend.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // OLD METHOD (You can keep or remove)
    List<Task> findByStudyProgramIdOrStudyProgramIsNull(Long studyProgramId);

    // NEW METHOD: Get X random tasks for a specific program + generic ones
    @Query(value = "SELECT * FROM tasks t " +
            "WHERE t.study_program_id = :programId OR t.study_program_id IS NULL " +
            "ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<Task> findRandomTasks(@Param("programId") Long programId, @Param("limit") int limit);

    List<Task> findByCategoryIgnoreCase(String category);
}