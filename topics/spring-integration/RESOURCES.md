# Spring Integration Resources

Base reference URL: `https://docs.spring.io/spring-integration/reference/`

## Knowledge

Primary source of truth for every lesson. Each page below becomes exactly one
lesson, followed in strict reference-documentation order. Excluded sections
(see `MISSION.md` → Out of scope) are omitted.

### Learning Path (Strict Reference Order)

Follow top to bottom. One page = one lesson.

#### Phase 1 — Overview
- [Overview](https://docs.spring.io/spring-integration/reference/overview.html)
  Use for: core concepts, EIP vocabulary, application-centric vs ESB, main components (Message, Channel, Endpoint), `@EnableIntegration`, POJO method invocation.

#### Phase 2 — Core Messaging
- [Core Messaging](https://docs.spring.io/spring-integration/reference/core.html)
  Use for: orientation to the channel/endpoint chapter.
- [Message Channels](https://docs.spring.io/spring-integration/reference/channel.html)
  Use for: what a channel is and the channel sub-topics that follow.
- [The MessageChannel Interface](https://docs.spring.io/spring-integration/reference/channel/interfaces.html)
  Use for: `MessageChannel`, `PollableChannel`, `SubscribableChannel` contracts.
- [Message Channel Implementations](https://docs.spring.io/spring-integration/reference/channel/implementations.html)
  Use for: `DirectChannel`, `QueueChannel`, `PublishSubscribeChannel`, `ExecutorChannel`, `PriorityChannel`, `RendezvousChannel`, `FluxMessageChannel`.
- [Channel Interceptors](https://docs.spring.io/spring-integration/reference/channel/interceptors.html)
  Use for: `ChannelInterceptor`, global interceptors, wire tap patterns.
- [MessagingTemplate](https://docs.spring.io/spring-integration/reference/channel/template.html)
  Use for: programmatic send/receive against channels.
- [Configuring Message Channels](https://docs.spring.io/spring-integration/reference/channel/configuration.html)
  Use for: declaring channels in Java/XML, datatype channels, dispatcher config.
- [Special Channels](https://docs.spring.io/spring-integration/reference/channel/special-channels.html)
  Use for: `errorChannel`, `nullChannel`, default channels.
- [Poller](https://docs.spring.io/spring-integration/reference/polling-consumer.html)
  Use for: polling consumers, `PollerMetadata`, pollable message sources.
- [Channel Adapter](https://docs.spring.io/spring-integration/reference/channel-adapter.html)
  Use for: inbound/outbound channel adapters as flow entry/exit points.
- [Messaging Bridge](https://docs.spring.io/spring-integration/reference/bridge.html)
  Use for: connecting two channels, throttling pollable → subscribable.

#### Phase 3 — Message
- [Message](https://docs.spring.io/spring-integration/reference/message.html)
  Use for: `Message`, `MessageHeaders`, `MessageBuilder`, message ID/timestamp.

#### Phase 4 — Message Routing
- [Message Routing](https://docs.spring.io/spring-integration/reference/message-routing.html)
  Use for: orientation to the routing chapter.
- [Routers](https://docs.spring.io/spring-integration/reference/router.html)
  Use for: what routers do and the router sub-topics.
- [Router Overview](https://docs.spring.io/spring-integration/reference/router/overview.html)
  Use for: router responsibilities and channel resolution.
- [Common Router Parameters](https://docs.spring.io/spring-integration/reference/router/common-parameters.html)
  Use for: `default-output-channel`, `resolution-required`, `apply-sequence`.
- [Router Implementations](https://docs.spring.io/spring-integration/reference/router/implementations.html)
  Use for: payload-type, header-value, recipient-list, XPath, exception-type routers.
- [Configuring a Generic Router](https://docs.spring.io/spring-integration/reference/router/namespace.html)
  Use for: declaring a custom router bean/endpoint.
- [Routers and SpEL](https://docs.spring.io/spring-integration/reference/router/spel.html)
  Use for: expression-driven routing.
- [Configuring a Router with Annotations](https://docs.spring.io/spring-integration/reference/router/annotation.html)
  Use for: `@Router` on POJO methods.
- [Dynamic Routers](https://docs.spring.io/spring-integration/reference/router/dynamic-routers.html)
  Use for: runtime channel mapping changes, `RouterMapping`.
- [Routing Slip](https://docs.spring.io/spring-integration/reference/router/routing-slip.html)
  Use for: predetermined multi-hop routing paths.
- [Process Manager](https://docs.spring.io/spring-integration/reference/router/process-manager.html)
  Use for: the process manager EIP with routing slips.
- [Filter](https://docs.spring.io/spring-integration/reference/filter.html)
  Use for: `MessageFilter`, `@Filter`, discard channels, throwing on rejection.
- [Splitter](https://docs.spring.io/spring-integration/reference/splitter.html)
  Use for: `@Splitter`, sequence headers, splitting collections/iterators.
- [Aggregator](https://docs.spring.io/spring-integration/reference/aggregator.html)
  Use for: `CorrelationStrategy`, `ReleaseStrategy`, message stores, expiry.
- [Resequencer](https://docs.spring.io/spring-integration/reference/resequencer.html)
  Use for: reordering messages by sequence number.
- [Message Handler Chain](https://docs.spring.io/spring-integration/reference/chain.html)
  Use for: composing handlers into a single endpoint.
- [Scatter-Gather](https://docs.spring.io/spring-integration/reference/scatter-gather.html)
  Use for: the scatter-gather EIP (broadcast + aggregate).
- [Thread Barrier](https://docs.spring.io/spring-integration/reference/barrier.html)
  Use for: suspending a thread until an aggregated event releases it.

#### Phase 5 — Message Transformation
- [Message Transformation](https://docs.spring.io/spring-integration/reference/transformer.html)
  Use for: `Transformer`, `@Transformer`, header enrichers, object/JSON transformers.
- [Content Enricher](https://docs.spring.io/spring-integration/reference/content-enricher.html)
  Use for: payload/header enrichment via gateways.
- [Claim Check](https://docs.spring.io/spring-integration/reference/claim-check.html)
  Use for: storing large payloads and passing a claim ticket.
- [Codec](https://docs.spring.io/spring-integration/reference/codec.html)
  Use for: encoding/decoding payloads (Kryo), `EncodingPayloadTransformer`.

#### Phase 6 — Messaging Endpoints
- [Messaging Endpoints](https://docs.spring.io/spring-integration/reference/endpoint.html)
  Use for: endpoint fundamentals, payload type conversion, poller config.
- [Endpoint Roles](https://docs.spring.io/spring-integration/reference/endpoint-roles.html)
  Use for: grouping endpoints for coordinated start/stop.
- [Leadership Event Handling](https://docs.spring.io/spring-integration/reference/leadership-event-handling.html)
  Use for: leader election driving endpoint lifecycle.
- [Messaging Gateways](https://docs.spring.io/spring-integration/reference/gateway.html)
  Use for: `@MessagingGateway`, request/reply, `GatewayProxyFactoryBean`.
- [Service Activator](https://docs.spring.io/spring-integration/reference/service-activator.html)
  Use for: `@ServiceActivator`, connecting POJO services to channels.
- [Delayer](https://docs.spring.io/spring-integration/reference/delayer.html)
  Use for: time-delayed message delivery, `DelayHandler`.
- [Scripting Support](https://docs.spring.io/spring-integration/reference/scripting.html)
  Use for: JSR-223 scripted endpoints.
- [Groovy Support](https://docs.spring.io/spring-integration/reference/groovy.html)
  Use for: Groovy scripts as handlers (endpoint-level, not the Groovy DSL).
- [Adding Behavior to Endpoints](https://docs.spring.io/spring-integration/reference/handler-advice.html)
  Use for: request-handler advice chain overview.
- [Provided Advice Classes](https://docs.spring.io/spring-integration/reference/handler-advice/classes.html)
  Use for: retry, circuit breaker, expression-evaluating advice.
- [Reactive Advice](https://docs.spring.io/spring-integration/reference/handler-advice/reactive.html)
  Use for: applying reactive operators around a handler.
- [Context Holder Advice](https://docs.spring.io/spring-integration/reference/handler-advice/context-holder.html)
  Use for: populating/clearing a context value around handling.
- [Custom Advice Classes](https://docs.spring.io/spring-integration/reference/handler-advice/custom.html)
  Use for: writing your own `AbstractRequestHandlerAdvice`.
- [Other Advice Chain Elements](https://docs.spring.io/spring-integration/reference/handler-advice/other.html)
  Use for: mixing generic Spring AOP advice into the chain.
- [Handling Message Advice](https://docs.spring.io/spring-integration/reference/handler-advice/handle-message.html)
  Use for: advising the whole `handleMessage`, not just the reply-producing part.
- [Transaction Support](https://docs.spring.io/spring-integration/reference/handler-advice/tx-handle-message-advice.html)
  Use for: transactional advice around message handling.
- [Advising Filters](https://docs.spring.io/spring-integration/reference/handler-advice/advising-filters.html)
  Use for: applying advice to filter endpoints correctly.
- [Advising Endpoints Using Annotations](https://docs.spring.io/spring-integration/reference/handler-advice/advising-with-annotations.html)
  Use for: attaching advice via messaging annotations.
- [Ordering Advices within an Advice Chain](https://docs.spring.io/spring-integration/reference/handler-advice/order.html)
  Use for: controlling advice execution order.
- [Advised Handler Properties](https://docs.spring.io/spring-integration/reference/handler-advice/handler-properties.html)
  Use for: how advice interacts with handler configuration.
- [Idempotent Receiver](https://docs.spring.io/spring-integration/reference/handler-advice/idempotent-receiver.html)
  Use for: the idempotent receiver EIP with metadata store.
- [Logging Channel Adapter](https://docs.spring.io/spring-integration/reference/logging-adapter.html)
  Use for: `<logging-channel-adapter>` / `.log()` for tracing flows.
- [java.util.function Support](https://docs.spring.io/spring-integration/reference/functions-support.html)
  Use for: using `Function`/`Consumer`/`Supplier` beans as endpoints.
- [Kotlin Support](https://docs.spring.io/spring-integration/reference/kotlin-functions.html)
  Use for: Kotlin lambdas as endpoints (endpoint-level, not the Kotlin DSL).

#### Phase 7 — Java DSL
- [Java DSL](https://docs.spring.io/spring-integration/reference/dsl.html)
  Use for: DSL rationale and `IntegrationFlow` basics.
- [DSL Basics](https://docs.spring.io/spring-integration/reference/dsl/java-basics.html)
  Use for: `IntegrationFlow`, `IntegrationFlowBuilder`, `@Bean` flows.
- [Message Channels (DSL)](https://docs.spring.io/spring-integration/reference/dsl/java-channels.html)
  Use for: `.channel()`, channel types in the DSL.
- [Pollers (DSL)](https://docs.spring.io/spring-integration/reference/dsl/java-pollers.html)
  Use for: `Pollers` factory in the DSL.
- [reactive() Endpoint](https://docs.spring.io/spring-integration/reference/dsl/java-reactive.html)
  Use for: reactive endpoint configuration in the DSL.
- [DSL and Endpoint Configuration](https://docs.spring.io/spring-integration/reference/dsl/java-endpoints.html)
  Use for: `.handle(...)` endpoint options, `GenericEndpointSpec`.
- [Transformers (DSL)](https://docs.spring.io/spring-integration/reference/dsl/java-transformers.html)
  Use for: `.transform()` in flows.
- [Inbound Channel Adapters (DSL)](https://docs.spring.io/spring-integration/reference/dsl/java-inboundadapters.html)
  Use for: `IntegrationFlow.from(...)` sources.
- [Message Routers (DSL)](https://docs.spring.io/spring-integration/reference/dsl/java-routers.html)
  Use for: `.route()` in flows.
- [Splitters and Aggregators (DSL)](https://docs.spring.io/spring-integration/reference/dsl/java-splitters.html)
  Use for: `.split()`, `.aggregate()`, `.resequence()` in flows.
- [Service Activators and .handle()](https://docs.spring.io/spring-integration/reference/dsl/java-handle.html)
  Use for: `.handle()` service activators.
- [Operator gateway()](https://docs.spring.io/spring-integration/reference/dsl/java-gateway.html)
  Use for: `.gateway()` operator in flows.
- [Operators log(), intercept(), wireTap()](https://docs.spring.io/spring-integration/reference/dsl/java-log.html)
  Use for: `.log()`, `.intercept()`, `.wireTap()` operators.
- [Working With Message Flows](https://docs.spring.io/spring-integration/reference/dsl/java-flows.html)
  Use for: structuring, naming, and reusing flows.
- [FunctionExpression](https://docs.spring.io/spring-integration/reference/dsl/java-function-expression.html)
  Use for: lambda-based expressions in the DSL.
- [Sub-flows](https://docs.spring.io/spring-integration/reference/dsl/java-subflows.html)
  Use for: inline sub-flow definitions within operators.
- [Using Protocol Adapters](https://docs.spring.io/spring-integration/reference/dsl/java-protocol-adapters.html)
  Use for: wiring protocol adapters through the DSL.
- [IntegrationFlowAdapter](https://docs.spring.io/spring-integration/reference/dsl/java-flow-adapter.html)
  Use for: class-based flow definitions.
- [Dynamic and Runtime Integration Flows](https://docs.spring.io/spring-integration/reference/dsl/java-runtime-flows.html)
  Use for: `IntegrationFlowContext`, registering/removing flows at runtime.
- [IntegrationFlow as a Gateway](https://docs.spring.io/spring-integration/reference/dsl/java-flow-as-gateway.html)
  Use for: exposing a flow via a gateway interface.
- [DSL Extensions](https://docs.spring.io/spring-integration/reference/dsl/java-extensions.html)
  Use for: creating custom DSL components.
- [Integration Flows Composition](https://docs.spring.io/spring-integration/reference/dsl/integration-flow-as-gateway.html)
  Use for: composing flows together (`to(IntegrationFlow)`).

#### Phase 8 — System Management
- [System Management](https://docs.spring.io/spring-integration/reference/system-management.html)
  Use for: orientation to operations/management chapter.
- [Metrics and Management](https://docs.spring.io/spring-integration/reference/metrics.html)
  Use for: Micrometer metrics, timers, gauges for integration components.
- [Message History](https://docs.spring.io/spring-integration/reference/message-history.html)
  Use for: tracking a message's path through components.
- [Message Store](https://docs.spring.io/spring-integration/reference/message-store.html)
  Use for: persistent stores for aggregators/claim check/backing state.
- [Metadata Store](https://docs.spring.io/spring-integration/reference/meta-data-store.html)
  Use for: `MetadataStore` for idempotency and adapter state.
- [Distributed Locks](https://docs.spring.io/spring-integration/reference/distributed-locks.html)
  Use for: `LockRegistry` for coordination across instances.
- [Control Bus](https://docs.spring.io/spring-integration/reference/control-bus.html)
  Use for: sending operational commands into a running context.
- [Orderly Shutdown](https://docs.spring.io/spring-integration/reference/shutdown.html)
  Use for: graceful shutdown sequencing.
- [Integration Graph](https://docs.spring.io/spring-integration/reference/graph.html)
  Use for: runtime graph model of the flow topology.
- [Integration Graph Controller](https://docs.spring.io/spring-integration/reference/graph-controller.html)
  Use for: exposing the graph over HTTP.

#### Phase 9 — Cross-cutting Runtime
- [Reactive Streams Support](https://docs.spring.io/spring-integration/reference/reactive-streams.html)
  Use for: `FluxMessageChannel`, reactive sources/handlers, backpressure.
- [Native Images Support](https://docs.spring.io/spring-integration/reference/native-aot.html)
  Use for: GraalVM AOT considerations for integration apps.

#### Phase 10 — Enterprise Concerns
- [Error Handling](https://docs.spring.io/spring-integration/reference/error-handling.html)
  Use for: error channels, `ErrorMessage`, retry, recovery, dead-letter patterns.
- [Spring Expression Language (SpEL)](https://docs.spring.io/spring-integration/reference/spel.html)
  Use for: SpEL usage across routers, transformers, and expressions.
- [Message Publishing](https://docs.spring.io/spring-integration/reference/message-publishing.html)
  Use for: `@Publisher`, publishing interceptor, `@EnablePublisher`.
- [Transaction Support](https://docs.spring.io/spring-integration/reference/transactions.html)
  Use for: transactional pollers, `TransactionSynchronizationFactory`, pseudo-tx.
- [Security in Spring Integration](https://docs.spring.io/spring-integration/reference/security.html)
  Use for: securing channels, `SecurityContext` propagation.

#### Phase 11 — Configuration
- [Configuration](https://docs.spring.io/spring-integration/reference/configuration.html)
  Use for: configuration chapter orientation.
- [Namespace Support](https://docs.spring.io/spring-integration/reference/configuration/namespace.html)
  Use for: XML namespace fundamentals.
- [Configuring the Task Scheduler](https://docs.spring.io/spring-integration/reference/configuration/namespace-taskscheduler.html)
  Use for: the shared `taskScheduler` used by pollers.
- [Global Properties](https://docs.spring.io/spring-integration/reference/configuration/global-properties.html)
  Use for: framework-wide `spring.integration.*` properties.
- [Annotation Support](https://docs.spring.io/spring-integration/reference/configuration/annotations.html)
  Use for: messaging annotations, `@EnableIntegration`, meta-annotations.
- [Messaging Meta-Annotations](https://docs.spring.io/spring-integration/reference/configuration/meta-annotations.html)
  Use for: composing your own messaging annotations.
- [Message Mapping Rules and Conventions](https://docs.spring.io/spring-integration/reference/configuration/message-mapping-rules.html)
  Use for: how method arguments/returns map to messages and headers.

#### Phase 12 — Verification & Wrap-up
- [Testing support](https://docs.spring.io/spring-integration/reference/testing.html)
  Use for: `spring-integration-test`, `MockIntegration`, `@SpringIntegrationTest`.
- [Spring Integration Samples](https://docs.spring.io/spring-integration/reference/samples.html)
  Use for: pointers to the official samples repository.
- [Additional Resources](https://docs.spring.io/spring-integration/reference/resources.html)
  Use for: further reading and community links.
- **Capstone final assessment** (no reference page) — synthesize the whole track.

## Wisdom (Communities)

- [Stack Overflow — spring-integration tag](https://stackoverflow.com/questions/tagged/spring-integration)
  Use for: real-world Q&A, common pitfalls, and answers from framework
  maintainers (Gary Russell, Artem Bilan) who actively monitor the tag.
- [Spring Integration GitHub Discussions](https://github.com/spring-projects/spring-integration/discussions)
  Use for: design questions, version-specific behavior, and direct guidance
  from the project team.

## Gaps

- The official reference does not ship a single canonical "beginner tutorial";
  the runnable sandbox in this workspace fills that gap by turning each
  reference page into working, tested code.
