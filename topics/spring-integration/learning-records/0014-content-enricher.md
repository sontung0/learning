# Lesson 0014 — Content Enricher

## What

The Content Enricher is a Message Endpoint that augments an incoming message with additional data obtained from an external source. Spring Integration provides two variants: **Header Enricher** (adds/modifies headers) and **Payload Enricher** (fetches data via a request channel and merges it onto the original payload).

## Key concepts

- **Header Enricher**: Adds headers via `StaticHeaderValueMessageProcessor` (literal) or `ExpressionEvaluatingHeaderValueMessageProcessor` (SpEL). DSL: `.enrichHeaders()`.
- **Payload Enricher** (`ContentEnricher`): Three phases — (1) extract a request payload from the original message, (2) send it to a request channel, (3) merge reply values onto the original payload via property expressions. DSL: `.enrich()`.
- **setDefaultOverwrite(false)**: Preserves existing headers when using `HeaderEnricher` directly.
- **EnricherSpec API**: `requestChannel()`, `requestPayload()`, `requestPayloadExpression()`, `requestSubFlow()`, `propertyExpression()`, `property()`, `replyChannel()`, `errorChannel()`, `requestTimeout()`, `replyTimeout()`, `shouldClonePayload()`.
- The enriched payload object **must have JavaBean setters** — records (with their final fields) don't work.

## Sandbox output

```
=== 1. Header Enricher (Java config) ===
[header-java] payload: hello, source=enricher-java, length=5, existing=keep-me
=== 2. Header Enricher (DSL) ===
[header-dsl] payload: spring integration, dslSource=enricher-dsl, upperCase=SPRING INTEGRATION
=== 3. Payload Enricher (request channel) ===
[lookupService] looking up ticket #42
[payload-req] Ticket(id=42, assignee=Alice, status=open, createdAt=...)
=== 4. Static payload enrichment (no request channel) ===
[static] Ticket(id=99, assignee=system, status=pending, createdAt=null)
=== Lesson 0014 complete ===
```

## Files changed

- `sandbox/src/main/java/com/learning/si/lesson0014/Lesson0014Config.java` — new sandbox
- `lessons/0014-content-enricher.html` — lesson page with 7 quiz questions
- `reference/0001-core-concepts.html` — added glossary entries and updated endpoint list

## Reference

- https://docs.spring.io/spring-integration/reference/content-enrichment.html
