# Learning Record 0009 — Special Channels

## What Was Taught

Lesson 0009 covered three framework-defined infrastructure channels:

1. **nullChannel** — a `/dev/null` sink bean that logs at DEBUG and returns `true`. Used when a component requires an output/reply channel but the result is irrelevant. Also subscribes to reactive `Publisher` payloads to initiate stream processing.
2. **errorChannel** — a default `PublishSubscribeChannel` for async error handling. When a handler on a different thread (e.g., `ExecutorChannel`, `QueueChannel` with poller) throws, the framework wraps the exception in an `ErrorMessage` and publishes it to either the `errorChannel` header (per-message) or the global `errorChannel` bean. Has `requireSubscribers=true` since 5.4.3 (throws `MessageDispatchingException` if no subscriber). Supports `ErrorMessageExceptionTypeRouter` for type-based error routing.
3. **Wire tap** — the cross-cutting observation pattern via `ChannelInterceptor`. Implemented with `@GlobalChannelInterceptor(patterns = "...")` or the `WireTap` class. Must return the message unchanged (returning `null` vetoes the send).

The sandbox demonstrated all three:
- Section 1: Sent to `nullChannel` → `send returned true`
- Section 2: `ExecutorChannel` handler threw → `ErrorMessage` with `MessageDeliveryException` delivered to `errorChannel` subscriber (running on `async-err-1` thread, not `main`)
- Section 3: `@GlobalChannelInterceptor(patterns = "*Channel")` intercepted all sends, logging `[wire-tap]` before each handler

## Key Nuances Learned

- **Error channel only applies to async handlers.** This is the single most important nuance: with `DirectChannel` (same thread), exceptions propagate like normal Java — no `ErrorMessage` is created. Only `ExecutorChannel` / `QueueChannel` + poller triggers the error channel mechanism.
- **`ErrorMessage` resolution order:** per-message `errorChannel` header takes priority over the global bean. This enables different error routing per flow.
- **The default `errorChannel` has a built-in `LoggingHandler` subscriber at `ERROR` level** with order `Ordered.LOWEST_PRECEDENCE - 100`. If you subscribe your own handler with a higher order value (lower priority), yours runs first.
- **`nullChannel` and reactive streams:** if the payload is a `Publisher`, `nullChannel` subscribes immediately to start stream processing. This was surprising — it's not purely passive.
- **Wire tap is not a channel type**, it's an interceptor pattern. The `@GlobalChannelInterceptor(patterns)` is the most flexible approach. The dedicated `WireTap` class is a simpler option for per-channel use.
- **The `failed message payload: null` observation:** The `messagePayload` header on the `ErrorMessage` was `null` in our sandbox. The original message is accessible via `((MessagingException) error.getPayload()).getFailedMessage()` — it's stored as a property of the exception, not as a header.

## ZPD

~20 min. Clean, straightforward material. The core concept — "DirectChannel exceptions propagate normally; async handler exceptions go to errorChannel" — is a critical mental model. The `nullChannel` was trivial. The wire tap section was mostly reinforcement of `ChannelInterceptor` from Lesson 0006, now applied globally.

Feels ready for Phase 3 (the poller and then the endpoint types).
