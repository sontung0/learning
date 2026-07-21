# Learning Record 0012 — Messaging Bridge

## What Was Taught

Lesson 0012 covered the **Messaging Bridge** — the simplest Spring Integration endpoint, which forwards messages from one channel to another with zero transformation.

1. **Role:** A `BridgeHandler` receives a message and calls `outputChannel.send(message)`. It's a `MessageHandler` implementation — nothing more, nothing less.

2. **Primary use case:** Connect a `PollableChannel` to a `SubscribableChannel`. The bridge manages the polling, so downstream subscribers use a `DirectChannel` without any polling concerns. Also used for throttling (poller controls throughput) and connecting channel adapters (e.g., stdin → stdout).

3. **Three configuration styles:**
   - **`@BridgeFrom`** on the target channel bean — declares the source channel name and poller configuration. Cleanest for annotation-only setups.
   - **`@BridgeTo`** on the source channel bean — alternative to `@BridgeFrom`, goes on the source instead of the target.
   - **`BridgeHandler` + `@ServiceActivator`** — explicit `BridgeHandler` bean creation with `setOutputChannel()`. Full programmatic control.
   - **Java DSL** — `.bridge(e -> ...)` step in `IntegrationFlow`. Syntactic sugar for `BridgeHandler`.

4. **Output channel fallback:** If no output channel is configured, the bridge uses the reply channel from the incoming message header. If neither exists, an exception is thrown.

## Sandbox Demonstration

The sandbox ran all three configuration styles:

- **Section 1 — @BridgeFrom:** `sourceChannel` (QueueChannel) bridged to `bridgedChannel` (DirectChannel) with `@BridgeFrom(value = "sourceChannel", poller = @Poller(fixedDelay = "500", maxMessagesPerPoll = "1"))`. Messages `bf-msg-1` and `bf-msg-2` were sent to `sourceChannel`, polled by the bridge, and delivered to the `@ServiceActivator` handler on `bridgedChannel`. Output: `[bridgeFrom] received: bf-msg-1`, `bf-msg-2` (on `scheduling-1` thread).
- **Section 2 — BridgeHandler:** Explicit `BridgeHandler` bean with `@ServiceActivator(inputChannel = "sourceB", poller = @Poller(...))` forwarding to `targetB`. Output: `[bridgeHandler] received: bh-msg-1`, `bh-msg-2`.
- **Section 3 — Java DSL:** `IntegrationFlow.from("sourceDsl").bridge(e -> e.poller(Pollers.fixedDelay(500).maxMessagesPerPoll(1))).channel("targetDsl")`. Output: `[dsl] received: dsl-msg-1`, `dsl-msg-2`.

All messages were delivered on `scheduling-1` thread (the shared polling thread). Zero errors.

## Key Nuances Learned

- **A bridge is just `BridgeHandler` wrapping `outputChannel.send()`.** There is zero message transformation. The only "work" is the polling schedule.
- **`@BridgeFrom` vs `@BridgeTo` — same result, different placement.** `@BridgeFrom` goes on the target channel and names the source. `@BridgeTo` goes on the source channel and names the target. Use whichever feels more natural.
- **The bridge's poller is the only moving part.** Without the poller, a bridge on a `QueueChannel` would never consume messages — the bridge is useless without scheduling.
- **All three styles produce identical thread behaviour.** In our sandbox, all bridges used `scheduling-1` thread (the single shared `TaskScheduler`). With `maxMessagesPerPoll="1"`, one message per 500ms is forwarded.
- **The bridge is a "throttler by proxy."** The poller's trigger determines rate, `maxMessagesPerPoll` limits burst. This is a clean pattern: you don't need a separate throttling component.
- **No `@Poller` means no polling happens on a `QueueChannel`.** If you create a `@BridgeFrom` without a `poller` attribute, the bridge won't consume from a `QueueChannel` — you'd need a default poller bean.

## ZPD

~15 min. The messaging bridge is conceptually trivial — it's the simplest possible endpoint. The main learning was the three configuration styles and the "bridge = BridgeHandler = outputChannel.send()" mental model. The poller knowledge from Lesson 0010 was a prerequisite.

Ready for Transformer next.
