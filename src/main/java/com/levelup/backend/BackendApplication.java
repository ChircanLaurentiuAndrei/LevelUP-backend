package com.levelup.backend;

import com.levelup.backend.repository.UserTaskRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableAsync
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Bean
    public CommandLineRunner resetStuckTasks(UserTaskRepository userTaskRepo) {
        return args -> {
            int count = userTaskRepo.resetStuckTasks();
            if (count > 0) {
                System.out.println("🔄 Recovered " + count + " stuck tasks (reset to PENDING).");
            }
        };
    }
}