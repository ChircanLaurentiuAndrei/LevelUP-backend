package com.levelup.backend.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class EntityCoverageTest {

    @Test
    void testTaskEntity() {
        Task task = new Task();
        task.setTitle("Study Math");
        task.setXpReward(50);
        task.setCategory("Education");

        assertNotNull(task);
        assertEquals("Study Math", task.getTitle());
        assertEquals(50, task.getXpReward());
    }

    @Test
    void testAchievementEntity() {
        Achievement ach = new Achievement();
        ach.setName("Novice");
        ach.setCriteriaType("XP_TOTAL");
        ach.setConditionValue(100);

        assertNotNull(ach);
        assertEquals("Novice", ach.getName());
        assertEquals(100, ach.getConditionValue());
    }

    @Test
    void testUserTaskEntity() {
        UserTask ut = new UserTask();
        ut.setStatus("PENDING");
        ut.setAssignedDate(LocalDate.now());

        assertNotNull(ut);
        assertEquals("PENDING", ut.getStatus());
        assertNotNull(ut.getAssignedDate());
    }

    @Test
    void testStudyProgramEntity() {
        StudyProgram sp = new StudyProgram();
        sp.setName("Computer Science");

        assertNotNull(sp);
        assertEquals("Computer Science", sp.getName());
    }
}