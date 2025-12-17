package com.levelup.backend.repository;

import com.levelup.backend.entity.UserTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserTaskRepository extends JpaRepository<UserTask, Long> {

    List<UserTask> findByUserId(Long userId);
    List<UserTask> findByUserIdAndStatus(Long userId, String status);
    Optional<UserTask> findByUserIdAndTaskIdAndStatus(Long userId, Long taskId, String status);

    @Modifying
    @Query("UPDATE UserTask u SET u.status = :newStatus, u.completedAt = :timestamp WHERE u.id = :id AND u.status = 'PENDING'")
    int updateStatusIfPending(@Param("id") Long id,
                              @Param("newStatus") String newStatus,
                              @Param("timestamp") LocalDateTime timestamp);

    @Modifying
    @Query("DELETE FROM UserTask u WHERE u.user.id = :userId AND u.status = 'PENDING'")
    void deletePendingTasksByUserId(@Param("userId") Long userId);
}