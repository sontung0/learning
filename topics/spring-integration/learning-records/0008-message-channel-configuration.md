# Learning Record 0008 — Configuring Message Channels

## What Was Taught

Lesson 0008 covered the five key channel configuration options developers use to tune flow behaviour:

1. **Datatype channels** — `DirectChannel.setDatatypes(Class...)` restricts accepted payload types; `@IntegrationConverter` registers auto-conversion via `integrationConversionService`.
2. **Dispatcher failover** — `DirectChannel.setFailover(false)` makes the dispatcher propagate exceptions to the sender immediately instead of trying the next handler.
3. **ExecutorChannel** — wraps a `TaskExecutor` for thread-pool dispatch; `send()` returns before the handler runs, breaking transaction propagation.
4. **PublishSubscribeChannel applySequence** — injects `sequenceNumber`/`sequenceSize` headers per subscriber copy, essential for downstream aggregators/resequencers.
5. **Bounded QueueChannel** — `new QueueChannel(capacity)` prevents unbounded memory growth; `send()` returns `false` when full after timeout.

The sandbox demonstrated all five in a single `CommandLineRunner` with `@Profile("lesson0008")`.

## Key Nuances Learned

- `DirectChannel` default failover is **on** — the dispatcher iterates through subscribed handlers when one throws. This is the opposite of "fail fast" which most engineers expect.
- Passing `null` as the `LoadBalancingStrategy` constructor arg disables both load-balancing and failover, creating fixed-order dispatch.
- `ExecutorChannel` breaks single-thread execution context — the sender returns before the handler runs, so no exception can be thrown back to the sender (errors go to the error channel instead).
- Transaction context is **thread-bound**, so it does NOT propagate across an `ExecutorChannel`.
- `applySequence` allocates new `Message` instances per subscriber (since the sequence headers differ); without it, all subscribers share the same `Message` reference (headers are immutable, so this is safe).
- `QueueChannel` without capacity is **unbounded** — a common source of OOM in production.
- The `send()` to a full bounded queue returns `false` (not an exception) when the send timeout expires.

## ZPD

Spent ~20 min. The material was straightforward application of concepts already introduced (channel types, interceptors). The `failover` default being "on" was the only genuinely surprising detail — it contradicts the typical engineering intuition of "fail fast". The transaction propagation limitation with `ExecutorChannel` is a critical real-world constraint worth reinforcing in later lessons.

The reference glossary package correction for `MessagingTemplate` (`org.springframework.integration.core`, not `support`) was applied retroactively to the 0001 glossary.
