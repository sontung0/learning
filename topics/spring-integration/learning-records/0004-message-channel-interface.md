# 0004 — The MessageChannel Interface

**Date:** 2026-07-18
**Lesson:** 0004 — The MessageChannel Interface
**Phase:** 2 — Core Messaging (third lesson; follows 0003 Message Channels)

## What Was Taught

- The three-interface hierarchy: `MessageChannel` (root: `send(Message)`, `send(Message, long)`), `SubscribableChannel` (adds `subscribe(MessageHandler)`, `unsubscribe(MessageHandler)` — push model), `PollableChannel` (adds `receive()`, `receive(long)` — pull model).
- Every channel implementation lives on one of the two branches: `DirectChannel`, `PublishSubscribeChannel`, `ExecutorChannel`, `FluxMessageChannel` → SubscribableChannel; `QueueChannel`, `PriorityChannel`, `RendezvousChannel` → PollableChannel.
- Push vs pull trade-offs: threading (sender thread vs scheduler thread), buffering (none vs queue-backed), back-pressure (sender blocks vs queue grows), transaction scope (single-thread tx crosses channel vs poller tx ends before handler).
- The `send()` boolean return value: `false` on timeout (no exception thrown).
- `receive(0)` returns `null` immediately on an empty queue; `receive(-1)` blocks forever.
- The framework adapts `@ServiceActivator` to a `PollableChannel` by wrapping the endpoint in a `PollingConsumer` — but only if a `PollerMetadata` bean exists.
- `EventDrivenConsumer` calls `subscribe()` under the hood for `SubscribableChannel`s; `PollingConsumer` calls `receive()` for `PollableChannel`s.

## Sandbox Code

- **Package:** `com.learning.si.lesson0004`, profile `lesson0004`.
- Demonstrated `SubscribableChannel` with explicit `.subscribe()` calls on a `DirectChannel` — two handlers, point-to-point dispatch.
- Demonstrated `PollableChannel` with explicit `.receive(0)` and `.receive(1000)` on a `QueueChannel` — three sent, three pulled, fourth returns null.
- Demonstrated `send()` return value on the `SubscribableChannel` — `true` for successful delivery.
- **Verified running:** log output confirmed each section's expected behaviour (handler A/B alternating, three tasks pulled, null on empty queue).

## Glossary Additions (merged into `reference/0001-core-concepts.html`)

- `SubscribableChannel`, `PollableChannel`, `EventDrivenConsumer`, `PollingConsumer`, Push model, Pull model

## Zone of Proximal Development (Current)

Next lesson in strict `RESOURCES.md` order is **0005 — Message Channel Implementations** (`https://docs.spring.io/spring-integration/reference/channel/implementations.html`): DirectChannel, QueueChannel, PublishSubscribeChannel, ExecutorChannel, PriorityChannel, RendezvousChannel, FluxMessageChannel.

## Status

Lesson completed and verified. Awaiting user followup or request to continue.
