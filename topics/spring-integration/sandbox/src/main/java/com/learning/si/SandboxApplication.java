package com.learning.si;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Spring Integration learning sandbox.
 *
 * <p>This class defines no integration flows itself. Each lesson lives in its own
 * package ({@code com.learning.si.lessonNNNN}) and is guarded by a matching Spring
 * profile ({@code lessonNNNN}), so only the lesson you activate is wired up.
 *
 * <p>Run a lesson:
 * <pre>{@code
 *   ./mvnw spring-boot:run -Dspring-boot.run.profiles=lesson0001
 * }</pre>
 */
@SpringBootApplication
public class SandboxApplication {

    public static void main(String[] args) {
        SpringApplication.run(SandboxApplication.class, args);
    }
}
