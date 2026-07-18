# 0005 — Message Channel Implementations

**Date:** 2026-07-18
**Lesson:** 0005 — Message Channel Implementations
**Phase:** 2 — Core Messaging (fourth lesson; follows 0004 MessageChannel Interface)

## What Was Taught

- The seven concrete channel implementations in Spring Integration, organized by interface branch, dispatch behaviour, and buffering strategy:
  - **DirectChannel** (Subscribable, point-to-point, caller's thread, no buffer) — the default. Round-robin load balancing, failover support, single-thread transaction.
  - **QueueChannel** (Pollable, point-to-point, scheduler thread, `BlockingQueue`) — always set a capacity; unbounded by default.
  - **PublishSubscribeChannel** (Subscribable, broadcast, caller's thread or executor) — `minSubscribers`, async via `TaskExecutor`.
  - **ExecutorChannel** (Subscribable, point-to-point, executor thread pool) — breaks tx scope, fire-and-forget.
  - **PriorityChannel** (Pollable, point-to-point, ordered by `priority` header) — backed by `PriorityBlockingQueue`.
  - **RendezvousChannel** (Pollable, zero-capacity `SynchronousQueue`) — sender and receiver both block until matched.
  - **FluxMessageChannel** (Reactive `Publisher`, back-pressure aware) — bridge to Project Reactor.
- Decision guide mapping integration requirements to the correct channel type.
- Dispatcher configuration: failover and load-balancing on DirectChannel/ExecutorChannel.

## Sandbox Code

- **Package:** `com.learning.si.lesson0005`, profile `lesson0005`.
- Demonstrated five implementations in a single runner: DirectChannel (round-robin A/B alternating), QueueChannel (bounded at 5, three tasks pulled), PublishSubscribeChannel (broadcast to X and Y), ExecutorChannel (handler runs on pool thread, not main thread), PriorityChannel (high→medium→low order by `priority` header).
- **Verified running:** all five sections produced expected output, including correct thread name and priority ordering.

## Key Nuances Learned

- `MessageChannel` does not have `subscribe()` — you must type beans as `SubscribableChannel` or `PollableChannel` (the sub-interfaces) to access those methods.
- The priority header key is the plain string `"priority"` (or the constant in `IntegrationMessageHeaderAccessor.PRIORITY`, but the class lives in `spring-integration-core`, not `spring-messaging`).
- Three separate `CommandLineRunner` beans run in non-deterministic order (caught in Lesson 0004, fixed by consolidating to one runner). Same pattern applied here.

## Zone of Proximal Development (Current)

Next lesson in strict `RESOURCES.md` order is **0006 — Channel Interceptors** (`https://docs.spring.io/spring-integration/reference/channel/interceptors.html`): `ChannelInterceptor`, global interceptors, wire tap patterns.

## Status

Lesson completed and verified. Awaiting user followup or request to continue.
