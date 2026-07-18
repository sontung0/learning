# Spring Integration Sandbox

A single runnable Spring Boot (Maven) project for the Spring Integration
learning track. You code along here as you progress through the lessons.

## Isolation model

- **One module.** Everything is in this one Maven project.
- **One package per lesson.** Lesson NNNN's code lives in
  `src/main/java/com/learning/si/lessonNNNN/`.
- **One Spring profile per lesson.** Each lesson's `@Configuration` is annotated
  `@Profile("lessonNNNN")`. Only the active profile's beans are wired up, so
  lessons never collide.
- **No tests.** You run a lesson to see it work; you don't assert on it.

## Requirements

- JDK 25+
- Maven (or the wrapper `./mvnw`, added on first `mvn -N wrapper:wrapper` run)

## Run a lesson

```bash
cd topics/spring-integration/sandbox
./mvnw spring-boot:run -Dspring-boot.run.profiles=lesson0001
```

Replace `lesson0001` with the lesson you're working on. Running with no profile
starts an empty context.

## Layout

```
sandbox/
  pom.xml
  src/main/java/com/learning/si/
    SandboxApplication.java      # @SpringBootApplication, defines no flows
    lesson0001/                  # added as you reach each lesson
      ...
  src/main/resources/
    application.yml
```
