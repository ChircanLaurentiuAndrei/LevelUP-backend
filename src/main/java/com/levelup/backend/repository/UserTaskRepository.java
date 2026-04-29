package com.levelup.backend.repository;

import com.levelup.backend.entity.UserTask;
import com.levelup.backend.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface UserTaskRepository extends JpaRepository<UserTask, Long> {

    @Query("SELECT ut FROM UserTask ut JOIN FETCH ut.task WHERE ut.user.id = :userId")
    List<UserTask> findByUserIdWithTask(@Param("userId") UUID userId);

    List<UserTask> findByUserIdAndStatus(UUID userId, TaskStatus status);

    @Modifying
    @Query("UPDATE UserTask u SET u.status = :newStatus, u.completedAt = :timestamp WHERE u.id = :id AND u.status = com.levelup.backend.enums.TaskStatus.PENDING")
    int updateStatusIfPending(@Param("id") Long id,
                              @Param("newStatus") TaskStatus newStatus,
                              @Param("timestamp") LocalDateTime timestamp);

    @Modifying
    @Query("DELETE FROM UserTask u WHERE u.user.id = :userId AND u.status = com.levelup.backend.enums.TaskStatus.PENDING")
    void deletePendingTasksByUserId(@Param("userId") UUID userId);

    @Modifying
    @Transactional
    @Query("UPDATE UserTask u SET u.status = com.levelup.backend.enums.TaskStatus.PENDING WHERE u.status = com.levelup.backend.enums.TaskStatus.VERIFYING")
    int resetStuckTasks();
}
