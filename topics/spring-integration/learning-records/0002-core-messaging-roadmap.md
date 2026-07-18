# 0002 — Core Messaging: Chapter Roadmap

**Date:** 2026-07-18
**Lesson:** 0002 — Core Messaging
**Phase:** 2 — Core Messaging (orientation page, first of eleven Phase 2 lessons)

## What Was Taught

- The Core Messaging reference page is a one-paragraph orientation with no new
  API — it names messages, channels, endpoints, the core EIP endpoint types
  (filter, router, transformer, service activator, splitter, aggregator), and
  system management (control bus, message history) as everything the
  *chapter* eventually covers, but the individual API pages that follow it in
  `RESOURCES.md` are where each is actually taught.
- Mapped out the strict Phase 2 lesson order (Message Channels →
  `MessageChannel` interface → implementations → interceptors →
  `MessagingTemplate` → configuration → special channels → poller → channel
  adapter → messaging bridge), and confirmed the EIP endpoints and system
  management topics are deliberately deferred to Phase 4+ and Phase 8
  respectively — not skipped.
- No new sandbox code this lesson (nothing new to run); Lesson 0001's
  `QueueChannel` + `@ServiceActivator` flow was pointed to as a live instance
  of the Message Channel / Message Endpoint concepts the upcoming chapter
  will dissect.

## Zone of Proximal Development (Current)

Next lesson is **0003 — Message Channels**
(`https://docs.spring.io/spring-integration/reference/channel.html`), the
first Phase 2 lesson with real new content: what a channel is, point-to-point
vs publish-subscribe. New sandbox code (`com.learning.si.lesson0003`) resumes
here, contrasting a `DirectChannel` against the `QueueChannel` already run in
Lesson 0001.

## Status

Lesson completed. Awaiting user questions or a request to continue with
Lesson 0003.
