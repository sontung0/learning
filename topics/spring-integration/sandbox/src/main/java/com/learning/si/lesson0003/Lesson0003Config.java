package com.learning.si.lesson0003;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;

/**
 * Lesson 0003 — Message Channels: point-to-point vs publish-subscribe.
 *
 * <p>Demonstrates the two <em>delivery modes</em> of Spring Integration channels:
 * <ul>
 *   <li><strong>Point-to-point</strong> ({@link DirectChannel}): each message is delivered to
 *       exactly one subscriber. With two competing subscribers, only one handles each message.</li>
 *   <li><strong>Publish-subscribe</strong> ({@link PublishSubscribeChannel}): each message is
 *       delivered to <em>all</em> subscribers. Both handlers see every message.</li>
 * </ul>
 *
 * <p>Run it:
 * <pre>{@code
 *   cd topics/spring-integration/sandbox
 *   ./mvnw spring-boot:run -Dspring-boot.run.profiles=lesson0003
 * }</pre>
 *
 * @see <a href="https://docs.spring.io/spring-integration/reference/channel.html">Message Channels reference</a>
 */
@Configuration
@Profile("lesson0003")
public class Lesson0003Config {

    private static final Logger log = LoggerFactory.getLogger(Lesson0003Config.class);

    // ── Point-to-point: one subscriber per message ──────────────────────────

    /** A DirectChannel is the default point-to-point channel: subscribable, no buffering. */
    @Bean
    public MessageChannel orders() {
        return new DirectChannel();
    }

    @ServiceActivator(inputChannel = "orders")
    public void handleOrderA(String payload) {
        log.info("Subscriber A received: {}", payload);
    }

    @ServiceActivator(inputChannel = "orders")
    public void handleOrderB(String payload) {
        log.info("Subscriber B received: {}", payload);
    }

    // ── Publish-subscribe: all subscribers see every message ────────────────

    @Bean
    public MessageChannel notifications() {
        return new PublishSubscribeChannel();
    }

    @ServiceActivator(inputChannel = "notifications")
    public void notifyEmail(String payload) {
        log.info("Email notifier got: {}", payload);
    }

    @ServiceActivator(inputChannel = "notifications")
    public void notifySlack(String payload) {
        log.info("Slack notifier got: {}", payload);
    }

    // ── Sender ──────────────────────────────────────────────────────────────

    @Bean
    public CommandLineRunner sendMessages(MessageChannel orders, MessageChannel notifications) {
        return args -> {
            log.info("=== Sending to point-to-point channel (orders) ===");
            orders.send(new GenericMessage<>("Order-001"));
            orders.send(new GenericMessage<>("Order-002"));

            log.info("=== Sending to publish-subscribe channel (notifications) ===");
            notifications.send(new GenericMessage<>("Alert: disk full"));
        };
    }
}
