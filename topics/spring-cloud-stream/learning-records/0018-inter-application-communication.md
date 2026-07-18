# Inter-Application Communication

The user studied how multi-application pipelines are built by correlating input and output destinations across adjacent Spring Cloud Stream apps (no direct app-to-app reference — only a shared destination name). The user reviewed consumer groups for load-balanced scaling, the `spring.cloud.stream.instanceCount` and `spring.cloud.stream.instanceIndex` properties (defaults 1 and 0), and the fact that Spring Cloud Data Flow sets these automatically while independent deployments require manual configuration. The user also noted that these properties are critical for correct partition assignment in binders such as Kafka.

**Evidence:** The user completed Lesson 0018 and elected to skip the assessment, moving directly to the next topic (Partitioning).