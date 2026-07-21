# Learning Record 0011 — Channel Adapter

## What Was Taught

Lesson 0011 covered the **Channel Adapter** pattern — the simplest endpoint type in Spring Integration.

1. **Inbound Channel Adapter** — a `SourcePollingChannelAdapter` that polls a method, expression, or `MessageSource` and sends every non-null return value to an output channel. Configured via:
   - `@InboundChannelAdapter` annotation on a method (not on a `@Bean` — the annotation goes directly on the method in a `@Configuration` or `@Component` class).
   - Java DSL `IntegrationFlow.from(MessageSource, c -> c.poller(...))`.
   - XML `<int:inbound-channel-adapter>`.
   - SpEL expression or script via the `expression` attribute or `<script>` sub-element.

2. **Outbound Channel Adapter** — a sink endpoint. It receives messages from a channel and passes the payload to a void-returning method. Backed by `@ServiceActivator` (on `DirectChannel` it's an `EventDrivenConsumer`; on `QueueChannel` with `@Poller` it's a `PollingConsumer`).

3. **Key rules:**
   - Inbound: non-null → message sent; null → skip cycle.
   - Outbound: void method → no reply; the message ends here.
   - `@InboundChannelAdapter` at the `@Bean` method level only works for `MessageSource` or `Supplier` beans.
   - If no poller is specified, a default with `PeriodicTrigger` (1s fixed delay) is used. A default poller bean must be registered.

4. **Implicit channels** — if a channel is not specified, the framework creates an implicit `DirectChannel` named after the adapter's id.

## Sandbox Demonstration

The sandbox ran three sections:

- **Section 1 — `@InboundChannelAdapter`:** A method annotated with `@InboundChannelAdapter(channel = "adapterChannel", poller = @Poller(fixedRate = "1000"))` returned a timestamp string every second. The main thread polled `adapterChannel` (a `QueueChannel`) 3 times — each poll received the next timestamp. Timestamps: `adapter-2026-07-21T11:12:07.093405Z`, `adapter-2026-07-21T11:12:08.097614Z`, `adapter-2026-07-21T11:12:09.094932Z` — exactly 1s apart.
- **Section 2 — DSL IntegrationFlow:** Equivalent inbound adapter expressed with `IntegrationFlow.from(MessageSource, c -> c.poller(Pollers.fixedRate(1500)))`. Polled `dslChannel` for 2 messages received at 1.5s intervals.
- **Section 3 — Outbound Channel Adapter:** 3 messages sent to `outboundSink` (a `DirectChannel`) were immediately consumed by the `@ServiceActivator` handler on the main thread: `[outbound-consumer] handled: outbound-1/2/3`.

## Key Nuances Learned

- **`@InboundChannelAdapter` on `@Bean` method only works for `MessageSource` or `Supplier`:** My first attempt put the annotation on a `@Bean` method returning `String`. The framework rejected it with `IllegalArgumentException`. The fix: remove `@Bean` — placing `@InboundChannelAdapter` on a plain method inside `@Configuration` works without `@Bean` (the framework treats the method as a source, not a bean definition).
- **The DSL form and annotation form are not exactly equivalent for the same use case:** The DSL `IntegrationFlow.from(MessageSource, poller)` gives you a full flow pipeline in a single method. The annotation style is simpler but limited — you can't chain downstream handlers without additional channels.
- **Outbound adapter is just a `@ServiceActivator` — nothing special:** The concept of "outbound channel adapter" in the reference is a role, not a distinct framework class. It's the same `EventDrivenConsumer`/`PollingConsumer` infrastructure, just used as a sink.
- **Implicit channel creation:** If you omit `channel` from an outbound adapter, the framework creates a `DirectChannel` named after the `id`/bean name. This means you can define channels implicitly by using adapter ids.
- **Expression-based adapters are powerful for lightweight glue code:** SpEL can reference bean properties, system properties, or any expression. For dynamic integration without Java compilation, this is the escape hatch.
- **The reference doc emphasizes the `max-messages-per-poll` interaction:** The polling task is invoked up to `maxMessagesPerPoll` times per poll cycle (or until the source returns null). This is a critical performance tuning parameter for inbound adapters backed by external systems.

## ZPD

~20 min. Straightforward material — the channel adapter is conceptually simple, and the main learning was the annotation placement rule (`@InboundChannelAdapter` on `@Bean` only works for MessageSource/Supplier). The key insight is that these are the building blocks for all transport adapters (file, JMS, HTTP, etc.) — understanding this pattern makes the rest of the framework predictable.

The previous lesson's Poller knowledge was a prerequisite — the inbound adapter is fundamentally a `SourcePollingChannelAdapter` that uses a poller.

Ready for Messaging Bridge next.
