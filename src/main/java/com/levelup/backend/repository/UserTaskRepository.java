package com.levelup.backend.repository;

import com.levelup.backend.entity.UserTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTaskRepository extends JpaRepository<UserTask, Long> {

    List<UserTask> findByUserId(Long userId);

    List<UserTask> findByUserIdAndStatus(Long userId, String status);

    Optional<UserTask> findByUserIdAndTaskIdAndStatus(Long userId, Long taskId, String status);
}