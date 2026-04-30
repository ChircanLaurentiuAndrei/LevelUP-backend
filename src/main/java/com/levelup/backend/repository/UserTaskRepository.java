package com.levelup.backend.repository;

import com.levelup.backend.entity.UserTask;
import com.levelup.backend.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserTaskRepository extends JpaRepository<UserTask, Long> {

    @Query("SELECT ut FROM UserTask ut JOIN FETCH ut.task WHERE ut.user.id = :userId AND ut.assignedDate = :date")
    List<UserTask> findByUserIdAndAssignedDateWithTask(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query("SELECT ut FROM UserTask ut JOIN FETCH ut.task WHERE ut.user.id = :userId")
    List<UserTask> findByUserIdWithTask(@Param("userId") Long userId);

    List<UserTask> findByUserIdAndStatus(Long userId, TaskStatus status);

    @Modifying
    @Query("UPDATE UserTask u SET u.status = :newStatus, u.completedAt = :timestamp WHERE u.id = :id AND u.status = :oldStatus")
    int updateStatusIfPending(@Param("id") Long id,
                              @Param("newStatus") TaskStatus newStatus,
                              @Param("oldStatus") TaskStatus oldStatus,
                              @Param("timestamp") LocalDateTime timestamp);

    @Modifying
    @Query("DELETE FROM UserTask u WHERE u.user.id = :userId AND u.status = :status")
    void deleteTasksByUserIdAndStatus(@Param("userId") Long userId, @Param("status") TaskStatus status);

    @Modifying
    @Transactional
    @Query("UPDATE UserTask u SET u.status = :pendingStatus WHERE u.status = :verifyingStatus")
    int resetStuckTasks(@Param("pendingStatus") TaskStatus pendingStatus, @Param("verifyingStatus") TaskStatus verifyingStatus);
}
