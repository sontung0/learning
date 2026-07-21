# 0007 — MessagingTemplate

**Date:** 2026-07-21
**Lesson:** 0007 — MessagingTemplate
**Phase:** 2 — Core Messaging (sixth lesson; follows 0006 Channel Interceptors)

## What Was Taught

- **MessagingTemplate** (`org.springframework.integration.support`) is the imperative API for programmatic channel interaction — the counterpart to declarative `@ServiceActivator` / `@MessagingGateway` endpoints.
- Three core operations and their signatures:
  - **`send(MessageChannel, Message<?>)` → `boolean`** — fire-and-forget. Returns `true` on successful delivery, `false` on timeout. Accepts any `MessageChannel`.
  - **`receive(PollableChannel)` → `Message<?>` or `null`** — pull next message. Requires `PollableChannel`. Returns `null` when the queue is empty and the receive timeout expires.
  - **`sendAndReceive(MessageChannel, Message<?>)` → `Message<?>`** — request-reply. Creates a temporary anonymous `PollableChannel`, sets it as the `replyChannel` header, sends, and blocks on `receive()` for the reply.
- **Timeout configuration:** `setSendTimeout(long)` and `setReceiveTimeout(long)`. Both default to `-1` (block forever). Always set explicit timeouts in production.
- **Return Address EIP:** The temporary anonymous channel mechanism in `sendAndReceive` implements the Return Address pattern — the sender tells the receiver where to reply via a header (`replyChannel`).
- **No extra dependency:** `MessagingTemplate` ships in `spring-integration-core`.
- Contrast with `@MessagingGateway` (Phase 6): `MessagingTemplate` is imperative and blocks the caller; `@MessagingGateway` is declarative and supports `CompletableFuture` return types for async request-reply.

## Sandbox Code

- **Package:** `com.learning.si.lesson0007`, profile `lesson0007`.
- Three sections in a single `CommandLineRunner`:
  1. **send()** — fire-and-forget to a `DirectChannel`. Handler confirms receipt.
  2. **receive()** — three messages pre-loaded into a `QueueChannel`, then pulled one by one. Fourth call returns `null`.
  3. **sendAndReceive()** — request sent to a handler that uppercases the payload and replies via `replyChannel`. Template returns the reply.

## Key Nuances Learned

- `send()` accepts any `MessageChannel` (the root interface), but `receive()` is restricted to `PollableChannel` — compile-time safety because you can't pull from a subscribable channel.
- `sendAndReceive()` blocks the caller until a reply arrives or the receive timeout fires. This is synchronous request-reply. For async, use `@MessagingGateway` with `Future`/`CompletableFuture`.
- The `replyChannel` header is typed as `MessageChannel` (or `PollableChannel`), not a string. The handler calls `replyChannel.send(replyMessage)` to return the response.
- `MessagingTemplate` also has the static factory `new MessagingTemplate()` — no need for a bean unless you're sharing configured timeout values across call sites.

## Glossary Additions (merged into `reference/0001-core-concepts.html`)

- `MessagingTemplate`, `send(MessageChannel, Message)`, `receive(PollableChannel)`, `sendAndReceive(MessageChannel, Message)`

## Zone of Proximal Development (Current)

Next lesson in strict `RESOURCES.md` order is **0008 — Configuring Message Channels** (`https://docs.spring.io/spring-integration/reference/channel/configuration.html`): declaring channels in Java/XML, datatype channels, dispatcher configuration.

## Status

Lesson created and ready for sandbox verification. Awaiting user followup or request to continue.
