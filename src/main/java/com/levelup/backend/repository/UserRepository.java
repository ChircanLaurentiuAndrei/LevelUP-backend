package com.levelup.backend.repository;

import com.levelup.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findAllByOrderByCurrentXpDesc();

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.studyProgram WHERE u.username = :username")
    Optional<User> findByUsernameWithStudyProgram(@Param("username") String username);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.studyProgram LEFT JOIN FETCH u.unlockedAchievements WHERE u.username = :username")
    Optional<User> findByUsernameWithAchievements(@Param("username") String username);
}