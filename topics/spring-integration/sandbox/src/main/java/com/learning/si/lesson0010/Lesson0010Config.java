package com.learning.si.lesson0010;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.PollableChannel;
import org.springframework.messaging.support.GenericMessage;

/**
 * Lesson 0010 — The Poller.
 *
 * <p>Demonstrates Spring Integration polling:
 * <ul>
 *   <li><strong>Manual polling:</strong> {@code PollableChannel.receive(timeout)}.</li>
 *   <li><strong>PollingConsumer:</strong> {@code @ServiceActivator + @Poller}
 *       drains a {@code QueueChannel} on a scheduler thread.</li>
 *   <li><strong>MessageSource.receive():</strong> ad-hoc polling of a source
 *       directly.</li>
 * </ul>
 *
 * <p>Run it:
 * <pre>{@code
 *   cd topics/spring-integration/sandbox
 *   ./mvnw spring-boot:run -Dspring-boot.run.profiles=lesson0010
 * }</pre>
 *
 * @see <a href="https://docs.spring.io/spring-integration/reference/polling-consumer.html">
 *      Poller reference</a>
 */
@Configuration
@Profile("lesson0010")
public class Lesson0010Config {

    private static final Logger log = LoggerFactory.getLogger(Lesson0010Config.class);

    // ── A QueueChannel for both manual & background polling ──────────────

    @Bean
    public PollableChannel queueChannel() {
        return new org.springframework.integration.channel.QueueChannel(10);
    }

    // ── Background PollingConsumer — drains queueChannel every 800ms ─────

    @org.springframework.integration.annotation.ServiceActivator(
            inputChannel = "queueChannel",
            poller = @org.springframework.integration.annotation.Poller(
                    fixedDelay = "800", maxMessagesPerPoll = "1"))
    public void handleQueued(Message<?> msg) {
        log.info("[pollingConsumer] consumed: {} on thread {}",
                msg.getPayload(), Thread.currentThread().getName());
    }

    // ── Runner ───────────────────────────────────────────────────────────

    @Bean
    public CommandLineRunner demo(PollableChannel queueChannel) {

        return args -> {

            // ── 1. Manual polling with receive(timeout) ─────────────────
            log.info("=== 1. Manual polling: receive(timeout) ===");

            var fresh = new org.springframework.integration.channel.QueueChannel(5);
            fresh.send(new GenericMessage<>("poll-me"));

            Message<?> msg = fresh.receive(1000);
            log.info("[manual] received: {}",
                    msg != null ? msg.getPayload() : "null");

            // Queue is now empty — receive should time out and return null
            Message<?> empty = fresh.receive(500);
            log.info("[manual] second receive on empty queue: {}",
                    empty != null ? empty.getPayload() : "null (correct)");

            // ── 2. Background PollingConsumer with @Poller ───────────────
            log.info("=== 2. Background PollingConsumer ===");

            for (int i = 1; i <= 3; i++) {
                queueChannel.send(new GenericMessage<>("bg-msg-" + i));
            }
            log.info("[pollingConsumer] sent 3 messages — they will be "
                    + "drained by the background poller (watch for [pollingConsumer] lines)");

            // Give the poller time to pick up the messages
            Thread.sleep(3000);

            // ── 3. MessageSource.receive() (ad-hoc) ──────────────────────
            log.info("=== 3. MessageSource.receive() (ad-hoc) ===");

            MessageSource<String> source = new MessageSource<>() {
                private int count;
                @Override
                public Message<String> receive() {
                    if (count++ < 3) {
                        return MessageBuilder.withPayload("src-" + count).build();
                    }
                    return null;
                }
            };

            for (int i = 0; i < 5; i++) {
                Message<String> pulled = source.receive();
                log.info("[source] receive #{}: {}",
                        i + 1, pulled != null ? pulled.getPayload() : "null");
            }

            log.info("=== Lesson 0010 complete ===");
        };
    }
}
