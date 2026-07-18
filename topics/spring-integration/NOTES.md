# User Preferences & Notes

- **Experience level:** Comfortable — routinely builds Spring Boot applications.
  Lessons can assume familiarity with `@Configuration`, beans, dependency
  injection, and Boot auto-configuration.

- **Goal:** Master Spring Integration well enough to design and implement
  production-grade integrations independently.

- **Curriculum shape:** Strict reference-documentation order, full coverage of
  every non-excluded page (mastery, not a curated subset). Do NOT regroup
  thematically or reorder. One reference page → one lesson.

- **Runnable sandbox:** Lessons are code-along with a runnable Spring Boot
  sandbox scoped to THIS topic at
  `topics/spring-integration/sandbox/`. Single-module Maven project.
  Isolation rules:
  - One self-contained Maven project per topic (own `pom.xml`, own
    coordinates) so future topics never clash.
  - One Java package per lesson: `com.learning.si.lessonNNNN`.
  - One Spring profile per lesson (`lessonNNNN`) so beans don't collide;
    only the active profile's `@Configuration` loads.
  - No test files. Run a lesson by activating its profile:
    `cd sandbox && ./mvnw spring-boot:run -Dspring-boot.run.profiles=lessonNNNN`.

- **Emphasis:** Java DSL + annotation configuration (Spring Boot style) over XML
  namespace configuration, though XML is mentioned where the reference does.

- **Lesson Assessments:** End-of-lesson "Action" questions must be
  comprehensive — test ALL key knowledge points covered in that specific
  lesson, not just one concept.

- **Strict Sequential Progression:** Before creating any new lesson, read
  `RESOURCES.md`, identify the last completed topic via the latest
  `learning-records`, and select the very next uncompleted topic in the list.
  Do not group or skip topics unless explicitly requested.
