# 0006 — Channel Interceptors

**Date:** 2026-07-19
**Lesson:** 0006 — Channel Interceptors
**Phase:** 2 — Core Messaging (fifth lesson; follows 0005 Channel Implementations)

## What Was Taught

- The **ChannelInterceptor** interface with six hook methods organised into two lifecycles:
  - **Send lifecycle:** `preSend` (modify or veto), `postSend` (observe result), `afterSendCompletion` (cleanup — always fires).
  - **Receive lifecycle:** `preReceive` (veto via `false`), `postReceive` (modify), `afterReceiveCompletion` (cleanup).
- **Veto mechanism:** `preSend` returns `null` to cancel a send; `preReceive` returns `false` to cancel a receive. `send()` returns `false` on veto; `afterSendCompletion` still fires.
- **Attaching interceptors:** per-channel via `channel.addInterceptor()`, or globally via `@GlobalChannelInterceptor` with `patterns` and `order` attributes.
- **Global interceptor execution order:** global interceptors run first (sorted by `order` ascending), then channel-specific interceptors, then dispatch to subscribers.
- **WireTap pattern** — the `WireTap` class implements `ChannelInterceptor` as a read-only interceptor that copies messages to a secondary channel without affecting the primary flow. DSL support via `.wireTap("flow.input")`.
- **Custom interceptor vs WireTap:** use custom interceptors for modification/vetoing; use WireTap for read-only observability (logging, metrics, audit).

## Sandbox Code

- **Package:** `com.learning.si.lesson0006`, profile `lesson0006`.
- Three sections in separate `CommandLineRunner` beans (safe because they send to different channels — no ordering risk):
  1. **Custom interceptor with veto:** a `DirectChannel` with a `ChannelInterceptor` that logs every lifecycle hook. Payload `"veto"` causes `preSend` to return `null`. Verifies: the vetoed message doesn't reach the handler, `postSend` fires with `sent=false`, `afterSendCompletion` fires regardless.
  2. **WireTap:** a `DirectChannel` with a `WireTap` interceptor pointing to a `PublishSubscribeChannel`. Both the primary handler and the wire tap subscriber receive each message. Verifies: wire tap delivers a copy without disrupting the primary flow.
  3. **Global interceptor:** two `DirectChannel` beans (`globalChannelA`, `globalChannelB`) with a `@GlobalChannelInterceptor(pattern = "global*")` that adds an `"intercepted"` header. Verifies: both channels receive the injected header automatically.

## Key Nuances Learned

- `ChannelInterceptor` is in `org.springframework.messaging.support` (spring-messaging, not spring-integration). The `WireTap` class is in `org.springframework.integration.channel.support.interceptor` (spring-integration-core).
- `preSend` returns `Message<?>` — to modify, return a new message (e.g., via `MessageBuilder.fromMessage()`); to veto, return `null`. The same applies to `postReceive`.
- `afterSendCompletion` is the only send-hook guaranteed to fire — even on veto or exception. Use it for resource cleanup.
- Global interceptors match channel bean names, not channel type or class. Use `patterns = "input*"` to match by name prefix.
- Multiple `CommandLineRunner` beans are safe in this lesson because each runner sends to distinct channels. No ordering dependency between sections.

## Zone of Proximal Development (Current)

Next lesson in strict `RESOURCES.md` order is **0007 — MessagingTemplate** (`https://docs.spring.io/spring-integration/reference/channel/messaging-template.html`): programmatic send/receive with `MessagingTemplate` and `IntegrationMessageHeaderAccessor`.

## Status

Lesson created and ready for sandbox verification. Awaiting user followup or request to continue.
