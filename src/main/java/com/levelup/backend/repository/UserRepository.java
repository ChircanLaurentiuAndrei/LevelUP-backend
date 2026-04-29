package com.levelup.backend.repository;

import com.levelup.backend.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.studyProgram WHERE u.username = :username")
    Optional<User> findByUsernameWithStudyProgram(@Param("username") String username);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.studyProgram LEFT JOIN FETCH u.unlockedAchievements WHERE u.username = :username")
    Optional<User> findByUsernameWithAchievements(@Param("username") String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.unlockedAchievements WHERE u.id = :id")
    Optional<User> findByIdWithLock(@Param("id") UUID id);

    @Query("SELECT u FROM User u WHERE u.role != :role ORDER BY u.currentXp DESC")
    List<User> findLeaderboard(@Param("role") String role);
}
