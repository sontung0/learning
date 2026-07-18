# 0001 — Overview: Messages, Channels & Endpoints

**Date:** 2026-07-18
**Lesson:** 0001 — Overview: Messages, Channels & Endpoints
**Phase:** 1 — Overview (first item in the strict reference sequence)

## What Was Taught

- Spring Integration = Spring's implementation of the Enterprise Integration
  Patterns, bringing message-driven pipes-and-filters design into an ordinary
  `ApplicationContext`.
- The application-centric / embedded architecture versus the centralized ESB
  (hub-and-spoke) model, and why "everything is just Spring beans" makes flows
  testable.
- The three core nouns: **Message** (payload + headers), **Message Channel**
  (the pipe; point-to-point vs publish-subscribe, subscribable vs pollable),
  **Message Endpoint** (the filter connecting POJOs non-invasively).
- The seven endpoint types the Overview lists (transformer, filter, router,
  splitter, aggregator, service activator, channel adapter) and the
  terminology trap between the pipes-and-filters "filter" and the EIP Message
  Filter.
- `@EnableIntegration`'s role (registers infrastructure beans; Boot's
  integration starter applies it) and POJO method invocation with
  `@ServiceActivator`, `@Payload`, `@Header`.

## First Runnable Flow

- Sandbox `com.learning.si.lesson0001`, profile `lesson0001`: a `QueueChannel`
  (pollable) + a default `Pollers.fixedDelay` poller + a `@ServiceActivator`
  POJO + a `CommandLineRunner` sender.
- **Verified running:** message sent on `main`, pulled by the poller on
  `scheduling-1`, service activator logged "Hello, Spring Integration!". This
  confirms the pipe → endpoint flow concretely.
- Note learned: `PollerMetadataBuilder` does not exist; the correct factory is
  `org.springframework.integration.dsl.Pollers` (`.getObject()` to obtain the
  `PollerMetadata`).

## Assets Established

- New reusable **quiz widget** component: `assets/quiz.js` + quiz styles in
  `assets/styles.css` (self-checking, immediate feedback, retrieval-progress
  tracker). Future lessons should reuse `<div class="quiz" data-quiz>` markup
  rather than inline new quiz code.
- New **reference doc**: `reference/0001-core-concepts.html` (core concepts +
  seeded glossary). This glossary is now the canonical vocabulary — adhere to
  it in all later lessons.

## Zone of Proximal Development (Current)

First resource in the path is complete. Per strict `RESOURCES.md` order, the
next lesson is **Phase 2 — Core Messaging**
(`https://docs.spring.io/spring-integration/reference/core.html`), a short
orientation to the channel/endpoint chapter, before diving into Message
Channels.

## Status

Lesson completed and verified. Awaiting user questions or an explicit request
to continue with Lesson 0002.
