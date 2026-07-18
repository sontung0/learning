# 0025 — Consuming Batches

Demonstrated batch-mode consumption for the Kafka binder: how `consumer.batch-mode=true` delivers a whole `Consumer.poll()` to the function as a `List`, the two permitted signatures (`Consumer<List<T>>` with no headers vs. `Consumer<Message<List<T>>>` where each header value is itself a Collection aligned by index), the forbidden `List<Message<T>>` form, KafkaNull/tombstones appearing as `null` list elements, binder retry being disabled with two alternatives (`DefaultErrorHandler` via `ListenerContainerCustomizer`, or manual `AckMode` + `Acknowledgment.nack(index, sleep)`), DLQ granularity in batch mode (all records from the failed poll, not individual records, since 4.0.2), and the observability caveat that tracing propagation is not directly supported in batch listeners.

**Evidence:** `lessons/0025-consuming-batches.html`
