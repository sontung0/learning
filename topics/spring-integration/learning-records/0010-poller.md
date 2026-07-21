# Learning Record 0010 — The Poller

## What Was Taught

Lesson 0010 covered the **Poller** — Spring Integration's scheduling mechanism for driving `PollingConsumer` endpoints and `MessageSource` producers.

1. **PollingConsumer vs EventDrivenConsumer** — `PollingConsumer` polls a `PollableChannel` on a schedule rather than being message-driven. The key distinction: `EventDrivenConsumer` subscribes to a `SubscribableChannel` and receives messages as they arrive; `PollingConsumer` uses a poller to periodically check a `PollableChannel`.

2. **Two polling styles:**
   - **Manual:** `PollableChannel.receive(timeout)` — blocking, returns `null` on timeout. Useful for ad-hoc consumption.
   - **Framework-driven:** `@Poller` annotation on `@ServiceActivator` (and other endpoint annotations) — the framework manages the scheduling and thread.

3. **`@Poller` configuration attributes:**
   - `fixedDelay` — delay after the previous poll *completes*. Next poll starts after handler finishes + delay.
   - `fixedRate` — interval between *start* of polls. Next poll fires on schedule even if previous hasn't finished.
   - `cron` — Unix cron expression for time-of-day scheduling.
   - `maxMessagesPerPoll` — limits messages per poll cycle. Default is unlimited.
   - `receiveTimeout` — how long `receive()` blocks before timing out.
   - `taskExecutor` — for concurrent polling.
   - `adviceChain` — cross-cutting advice (retry, circuit breaker, etc.).

4. **`MessageSource<T>`** — functional interface with a single `receive()` method. Produces messages on demand. Returns `null` when no message is available. Connected to the framework via `SourcePollingChannelAdapter` (which needs no input channel) or used directly in code.

5. **Conditional Pollers / Smart Polling:**
   - `PollSkipAdvice` — `MethodInterceptor` that conditionally skips a poll cycle via `shouldSkip()` (e.g., skip weekends).
   - `ReceiveMessageAdvice` — extension point for `receive()` calls. Built-in: `SimpleActiveIdleReceiveMessageAdvice` (idle/active transitions), `CompoundTriggerAdvice` (external triggers).
   - `MessageSourceMutator` — transforms the source before each poll.
   - Custom `Trigger` implementations for arbitrary scheduling logic.

6. **Deferred Acknowledgment** — messages removed from `QueueChannel` on `receive()` can be transactionally acknowledged: committed only after the handler transaction commits.

## Sandbox Demonstration

The sandbox demonstrated three approaches:

- **Section 1 — Manual polling:** Created a fresh `QueueChannel(5)`, sent one message, called `receive(1000)` → got `"poll-me"`. Then called `receive(500)` on empty queue → got `null (correct)`.
- **Section 2 — Background PollingConsumer:** Sent 3 messages (`bg-msg-1` to `bg-msg-3`) to `queueChannel` (a `QueueChannel(10)` bean). A `@ServiceActivator` with `@Poller(fixedDelay = "800", maxMessagesPerPoll = "1")` drained them one by one on thread `scheduling-1`, ~800ms apart.
- **Section 3 — MessageSource.receive():** Created an anonymous `MessageSource<String>` that counts from 1 to 3, then returns `null`. Polled it 5 times → got `src-1`, `src-2`, `src-3`, `null`, `null`.

All three sections ran successfully with zero warnings or errors.

## Key Nuances Learned

- **`receive(timeout)` on an empty queue blocks for the full timeout** before returning `null`. The second call in section 1 took 500ms, visible in the timestamps.
- **`maxMessagesPerPoll = "1"`** is critical for controlled rate-limiting. Without it, the poller could drain the entire queue in one cycle. With it, each poll takes exactly one message, and the scheduled delay (800ms) paces consumption.
- **The poller thread pool is `scheduling-1`** by default (a single-threaded `TaskScheduler`). This means only one poll runs at a time. For concurrent polling, set `taskExecutor` on the `@Poller`.
- **`MessageSource.receive()` returning `null`** is not an error — it's the normal contract. The framework records it as an idle cycle and polls again on schedule.
- **The `SourcePollingChannelAdapter`** is the framework endpoint that bridges `MessageSource` to flows. In our sandbox we used the source directly in code rather than wiring it to a channel adapter, demonstrating the interface without framework overhead.
- **`fixedDelay` is usually safer than `fixedRate`** for I/O-bound pollers: if a poll takes longer than expected, `fixedDelay` naturally paces subsequent polls, while `fixedRate` can cause overlapping polls and thread contention.

## ZPD

~25 min. The Poller is a conceptually simple component (scheduled task that calls `receive()`) but the configuration surface is broad. The key insight is the distinction between `EventDrivenConsumer` (push) and `PollingConsumer` (pull) — once that clicks, the rest is attribute memorization.

The `MessageSource` interface is elegantly minimal — just one method. It's worth noting that this is the same interface used by file adapters, JDBC polling, and custom sources, making it a universal "producer on demand" contract.

Ready for Channel Adapters next.
