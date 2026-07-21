package com.learning.si.lesson0008;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.PollableChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.GenericMessage;

/**
 * Lesson 0008 — Configuring Message Channels.
 *
 * <p>Demonstrates channel configuration options beyond the simple defaults:
 * <ul>
 *   <li><strong>Datatype channel:</strong> restricts accepted payload types.</li>
 *   <li><strong>Dispatcher failover:</strong> disabling failover so a channel
 *       rejects a message if the first handler throws.</li>
 *   <li><strong>ExecutorChannel:</strong> dispatches on a thread pool.</li>
 *   <li><strong>PublishSubscribeChannel with applySequence:</strong>
 *       broadcast with sequence headers.</li>
 *   <li><strong>Bounded QueueChannel:</strong> capacity-limited pollable channel.</li>
 * </ul>
 *
 * <p>Run it:
 * <pre>{@code
 *   cd topics/spring-integration/sandbox
 *   ./mvnw spring-boot:run -Dspring-boot.run.profiles=lesson0008
 * }</pre>
 *
 * @see <a href="https://docs.spring.io/spring-integration/reference/channel/configuration.html">
 *      Configuring Message Channels reference</a>
 */
@Configuration
@Profile("lesson0008")
public class Lesson0008Config {

    private static final Logger log = LoggerFactory.getLogger(Lesson0008Config.class);

    // ── Channel beans ────────────────────────────────────────────────────

    /**
     * A Datatype Channel that only accepts Number payloads.
     */
    @Bean
    public SubscribableChannel numberChannel() {
        DirectChannel channel = new DirectChannel();
        channel.setDatatypes(Number.class);
        return channel;
    }

    /**
     * A DirectChannel with failover disabled — if the first subscribed handler
     * throws, the message is rejected rather than falling through to the next.
     */
    @Bean
    public SubscribableChannel failFastChannel() {
        DirectChannel channel = new DirectChannel();
        channel.setFailover(false);
        return channel;
    }

    /**
     * An ExecutorChannel that dispatches on a pool thread.
     */
    @Bean
    public SubscribableChannel executorChannel() {
        return new ExecutorChannel(new SimpleAsyncTaskExecutor("si-pool-"));
    }

    /**
     * A PublishSubscribeChannel with apply-sequence enabled.
     */
    @Bean
    public SubscribableChannel pubsubSeqChannel() {
        PublishSubscribeChannel channel = new PublishSubscribeChannel();
        channel.setApplySequence(true);
        return channel;
    }

    /**
     * A bounded QueueChannel with capacity 2.
     */
    @Bean
    public PollableChannel boundedQueueChannel() {
        return new QueueChannel(2);
    }

    // ── Runner ───────────────────────────────────────────────────────────

    @Bean
    public CommandLineRunner demo(
            SubscribableChannel numberChannel,
            SubscribableChannel failFastChannel,
            SubscribableChannel executorChannel,
            SubscribableChannel pubsubSeqChannel,
            PollableChannel boundedQueueChannel) {

        return args -> {

            // ── 1. Datatype channel ───────────────────────────────────────
            log.info("=== 1. Datatype channel (Number only) ===");

            numberChannel.subscribe(msg ->
                    log.info("[numberChannel handler] got: {} ({})",
                            msg.getPayload(), msg.getPayload().getClass().getSimpleName()));

            // This works: Integer IS-A Number
            numberChannel.send(new GenericMessage<>(42));
            // This works: Double IS-A Number
            numberChannel.send(new GenericMessage<>(3.14));

            try {
                // This fails: String is NOT a Number
                numberChannel.send(new GenericMessage<>("not-a-number"));
            } catch (Exception e) {
                log.warn("[numberChannel] correctly rejected String: {}",
                        e.getClass().getSimpleName());
            }

            // ── 2. Dispatcher failover disabled ───────────────────────────
            log.info("=== 2. DirectChannel with failover=false ===");

            failFastChannel.subscribe(msg -> {
                log.info("[failFast handler A] got: {}", msg.getPayload());
                throw new RuntimeException("Handler A failed");
            });
            failFastChannel.subscribe(msg ->
                    log.info("[failFast handler B] got: {}", msg.getPayload()));

            try {
                failFastChannel.send(new GenericMessage<>("will-fail-fast"));
            } catch (Exception e) {
                log.warn("[failFastChannel] send threw: {}", e.getMessage());
            }

            // ── 3. ExecutorChannel — thread-pool dispatch ─────────────────
            log.info("=== 3. ExecutorChannel ===");

            executorChannel.subscribe(msg -> {
                log.info("[executorChannel handler] payload={}, thread={}",
                        msg.getPayload(), Thread.currentThread().getName());
            });

            executorChannel.send(new GenericMessage<>("executed-async"));
            // Give the async thread a moment to complete
            Thread.sleep(300);
            log.info("[main] after executorChannel send (main thread={})",
                    Thread.currentThread().getName());

            // ── 4. PublishSubscribeChannel with applySequence ─────────────
            log.info("=== 4. PublishSubscribeChannel with applySequence ===");

            pubsubSeqChannel.subscribe(msg -> {
                log.info("[pubsub handler X] payload={}, seq={}/{}",
                        msg.getPayload(),
                        msg.getHeaders().get("sequenceNumber"),
                        msg.getHeaders().get("sequenceSize"));
            });
            pubsubSeqChannel.subscribe(msg -> {
                log.info("[pubsub handler Y] payload={}, seq={}/{}",
                        msg.getPayload(),
                        msg.getHeaders().get("sequenceNumber"),
                        msg.getHeaders().get("sequenceSize"));
            });

            pubsubSeqChannel.send(new GenericMessage<>("broadcast"));

            // ── 5. Bounded QueueChannel ──────────────────────────────────
            log.info("=== 5. Bounded QueueChannel (capacity=2) ===");

            boolean sent1 = boundedQueueChannel.send(new GenericMessage<>("msg-1"));
            boolean sent2 = boundedQueueChannel.send(new GenericMessage<>("msg-2"));
            // Queue is full — this should block. With no poller, use a short timeout.
            // We'll use send() with a timeout parameter via the channel directly.
            boolean sent3 = ((QueueChannel) boundedQueueChannel).send(
                    new GenericMessage<>("msg-3"), 500);
            log.info("boundedQueue sent1={}, sent2={}, sent3={} (third should be false)",
                    sent1, sent2, sent3);

            // Drain the queue
            Message<?> drained = boundedQueueChannel.receive(0);
            log.info("Drained: {}", drained != null ? drained.getPayload() : "null");

            log.info("=== Lesson 0008 complete ===");
        };
    }
}
