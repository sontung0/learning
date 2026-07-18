# Getting Started with the Kafka Binder

The user entered Phase 5 (Apache Kafka Binder) and studied how to add the binder via two dependency options (`spring-cloud-stream-binder-kafka` vs. `spring-cloud-starter-stream-kafka` starter). The user reviewed the direct concept mapping — destination → Kafka topic, consumer group → Kafka consumer group, partition → Kafka partition — meaning all prior learning (functional model, groups, partitioning, content-type negotiation) transfers cleanly. The user also noted broker compatibility limitations: brokers before 0.11.x.x lack native headers, and 0.11.x.x lacks `autoAddPartitions` support.

**Evidence:** The user completed Lesson 0022 and elected to skip the assessment, moving directly to the next topic (Kafka Binder Configuration Options).