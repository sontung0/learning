# Mission: Master Spring Integration

## Why

Spring Integration is the Spring portfolio's implementation of the Enterprise
Integration Patterns (EIP). It lets you build message-driven flows —
channels, endpoints, routers, transformers, adapters — directly inside a Spring
Boot application, with no external ESB. Mastering it means being able to design
and implement **production-grade integrations independently**: wiring systems
together, choosing the right EIP for each problem, and operating the result with
confidence.

The learner is already comfortable building Spring Boot applications, so this
track goes deep rather than broad — it covers the full reference documentation in
order, from core messaging concepts through the Java DSL, system management,
error handling, security, configuration, and testing.

## Success looks like

- Designing a message flow from scratch using the Java DSL and choosing the
  correct channel, endpoint, router, transformer, and error-handling strategy.
- Explaining and applying every core EIP the framework provides (routing,
  splitting, aggregating, enriching, claim check, scatter-gather, and more).
- Adding cross-cutting behavior via advice, transactions, idempotency, and
  observability without coupling business logic to the framework.
- Operating a flow in production: metrics, message history/store, distributed
  locks, control bus, graceful shutdown, native images.
- Writing deterministic tests for integration flows using
  `spring-integration-test`.
- Running every concept as working code in an isolated sandbox project.

## Constraints

- Follow the reference-documentation order strictly (see `RESOURCES.md`); one
  reference page becomes one lesson.
- Every lesson is runnable: code along in the topic-local sandbox
  (`topics/spring-integration/sandbox/`) and run it by activating that lesson's
  Spring profile.
- Emphasize the Java DSL and annotation-based configuration (Spring Boot style).

## Out of scope

Per the learner's request, the following reference sections are excluded from
this track:

- Home, Preface, What's New?
- Groovy DSL, Kotlin DSL (dedicated chapters)
- AMQP Support (entire group)
- FTP/FTPS Support (entire group)
- TCP and UDP Support (entire group)
- SFTP Support (entire group)
- XML Support — Dealing with XML Payloads (entire group)
- Integration Endpoints overview and all protocol adapter groups
  (Camel, Cassandra, CloudEvents, Debezium, ApplicationEvent, Feed, File,
  GraphQL, gRPC, Hazelcast, HTTP, JDBC, JPA, JMS, JMX, Kafka, Mail, MongoDb,
  MQTT, R2DBC, Redis, Resource, RSocket, SMB, STOMP, Stream, Syslog, WebFlux,
  WebSockets, Web Services, XMPP, ZeroMQ, Zip, Zookeeper)
