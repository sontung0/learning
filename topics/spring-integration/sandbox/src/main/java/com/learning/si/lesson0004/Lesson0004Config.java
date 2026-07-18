package com.learning.si.lesson0004;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.PollableChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.GenericMessage;

/**
 * Lesson 0004 — The MessageChannel Interface hierarchy.
 *
 * <p>Demonstrates all three core channel interfaces explicitly:
 * <ul>
 *   <li>{@link MessageChannel} — the root: {@code send(Message)} and {@code send(Message, long)}</li>
 *   <li>{@link SubscribableChannel} — push-based: {@code subscribe(MessageHandler)}</li>
 *   <li>{@link PollableChannel} — pull-based: {@code receive()} and {@code receive(long)}</li>
 * </ul>
 *
 * <p>Run it:
 * <pre>{@code
 *   cd topics/spring-integration/sandbox
 *   ./mvnw spring-boot:run -Dspring-boot.run.profiles=lesson0004
 * }</pre>
 *
 * @see <a href="https://docs.spring.io/spring-integration/reference/channel/interfaces.html">The MessageChannel Interface reference</a>
 */
@Configuration
@Profile("lesson0004")
public class Lesson0004Config {

    private static final Logger log = LoggerFactory.getLogger(Lesson0004Config.class);

    // ── Channels ────────────────────────────────────────────────────────────

    @Bean
    public SubscribableChannel alerts() {
        return new DirectChannel();
    }

    @Bean
    public PollableChannel tasks() {
        return new QueueChannel();
    }

    // ── Single runner with explicit ordering ───────────────────────────────
    //
    // All three demos in one CommandLineRunner so section ordering is
    // guaranteed.  Separate runners would be ordered arbitrarily.

    @Bean
    public CommandLineRunner demo(SubscribableChannel alerts, PollableChannel tasks) {
        return args -> {

            // ── 1. SubscribableChannel (push-based) ─────────────────────────
            log.info("=== 1. SubscribableChannel (push-based) ===");

            alerts.subscribe(new MessageHandler() {
                @Override
                public void handleMessage(Message<?> message) throws MessagingException {
                    log.info("Handler A received: {}", message.getPayload());
                }
            });

            alerts.subscribe(new MessageHandler() {
                @Override
                public void handleMessage(Message<?> message) throws MessagingException {
                    log.info("Handler B received: {}", message.getPayload());
                }
            });

            log.info("Sending first alert...");
            alerts.send(new GenericMessage<>("High CPU"));

            log.info("Sending second alert...");
            alerts.send(new GenericMessage<>("Disk low"));

            // ── 2. PollableChannel (pull-based) ─────────────────────────────
            log.info("=== 2. PollableChannel (pull-based) ===");

            tasks.send(new GenericMessage<>("Task-1"));
            tasks.send(new GenericMessage<>("Task-2"));
            tasks.send(new GenericMessage<>("Task-3"));

            log.info("Sent 3 tasks. Now pulling them one at a time...");

            Message<?> m1 = tasks.receive(0);
            log.info("Pulled: {}", m1 != null ? m1.getPayload() : "null");

            Message<?> m2 = tasks.receive(1000);
            log.info("Pulled: {}", m2 != null ? m2.getPayload() : "null");

            Message<?> m3 = tasks.receive(1000);
            log.info("Pulled: {}", m3 != null ? m3.getPayload() : "null");

            Message<?> m4 = tasks.receive(0);
            log.info("Fourth pull (empty queue): {}", m4 != null ? m4.getPayload() : "null — as expected");

            // ── 3. The send contract on MessageChannel ──────────────────────
            log.info("=== 3. send() return value (inherited from MessageChannel) ===");

            boolean sent = alerts.send(new GenericMessage<>("Test alert"));
            log.info("send() with default timeout returned: {}", sent);
        };
    }
}
