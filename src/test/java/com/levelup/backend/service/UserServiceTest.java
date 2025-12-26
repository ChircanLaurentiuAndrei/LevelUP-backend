package com.levelup.backend.service;

import com.levelup.backend.entity.User;
import com.levelup.backend.entity.StudyProgram;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    @Test
    void testUserConstructor() {
        User user = new User();
        assertNotNull(user, "User object should be instantiated");
        user.setUsername("TestStudent");
        user.setEmail("test@test.com");

        assertEquals("TestStudent", user.getUsername());
        assertEquals("test@test.com", user.getEmail());
    }

    @Test
    void testStudyProgramConstructor() {
        StudyProgram program = new StudyProgram();
        program.setName("Computer Science");

        assertNotNull(program, "StudyProgram object should be instantiated");
        assertEquals("Computer Science", program.getName());
    }

    @Test
    void testUserDefaultValues() {
        User newUser = new User();

        assertEquals(1, newUser.getCurrentLevel(), "New user should start at Level 1");
        assertEquals(0, newUser.getCurrentXp(), "New user should start with 0 XP");
    }

    @Test
    void testRoleDefaultValue() {
        User user = new User();

        assertEquals("USER", user.getRole(), "Default role should be USER");
    }

    @Test
    void testXpCalculationLogic() {
        User user = new User();
        user.setCurrentXp(100);

        int xpReward = 50;
        user.setCurrentXp(user.getCurrentXp() + xpReward);

        assertEquals(150, user.getCurrentXp(), "XP should sum correctly");
    }
}