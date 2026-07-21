package com.learning.si.lesson0012;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.annotation.BridgeFrom;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.handler.BridgeHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.PollableChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.GenericMessage;

/**
 * Lesson 0012 — Messaging Bridge.
 *
 * <p>Demonstrates bridging a {@link PollableChannel} to a
 * {@link SubscribableChannel} using three styles:
 * <ul>
 *   <li><strong>{@code @BridgeFrom}</strong> — creates a bridge from a pollable
 *       channel to another channel.</li>
 *   <li><strong>{@code BridgeHandler}</strong> — programmatic bridge via
 *       {@link ServiceActivator}.</li>
 *   <li><strong>Java DSL</strong> — {@code .bridge(e -> ...)} in an
 *       {@link IntegrationFlow}.</li>
 * </ul>
 *
 * <p>Run it:
 * <pre>{@code
 *   cd topics/spring-integration/sandbox
 *   ./mvnw spring-boot:run -Dspring-boot.run.profiles=lesson0012
 * }</pre>
 *
 * @see <a href="https://docs.spring.io/spring-integration/reference/bridge.html">
 *      Messaging Bridge reference</a>
 */
@Configuration
@Profile("lesson0012")
@EnableIntegration
public class Lesson0012Config {

    private static final Logger log = LoggerFactory.getLogger(Lesson0012Config.class);

    // ── Section 1: @BridgeFrom ──────────────────────────────────────────
    // @BridgeFrom on a channel bean creates a BridgeHandler that polls the
    // named source channel and forwards to the annotated channel.

    @Bean
    public PollableChannel sourceChannel() {
        return new QueueChannel();
    }

    @Bean
    @BridgeFrom(value = "sourceChannel",
                poller = @Poller(fixedDelay = "500", maxMessagesPerPoll = "1"))
    public SubscribableChannel bridgedChannel() {
        return new DirectChannel();
    }

    @ServiceActivator(inputChannel = "bridgedChannel")
    public void handleBridged(String payload) {
        log.info("[bridgeFrom] received: {}", payload);
    }

    // ── Section 2: BridgeHandler + @ServiceActivator ────────────────────
    // Equivalent bridge using the BridgeHandler class directly.

    @Bean
    public PollableChannel sourceB() {
        return new QueueChannel();
    }

    @Bean
    public SubscribableChannel targetB() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "sourceB",
                      poller = @Poller(fixedDelay = "500", maxMessagesPerPoll = "1"))
    public BridgeHandler bridgeHandler() {
        BridgeHandler b = new BridgeHandler();
        b.setOutputChannel(targetB());
        return b;
    }

    @ServiceActivator(inputChannel = "targetB")
    public void handleTargetB(String payload) {
        log.info("[bridgeHandler] received: {}", payload);
    }

    // ── Section 3: Java DSL bridge ──────────────────────────────────────

    @Bean
    public PollableChannel sourceDsl() {
        return new QueueChannel();
    }

    @Bean
    public SubscribableChannel targetDsl() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow bridgeFlow() {
        return IntegrationFlow.from("sourceDsl")
                .bridge(e -> e.poller(Pollers.fixedDelay(500)
                        .maxMessagesPerPoll(1)))
                .channel("targetDsl")
                .get();
    }

    @ServiceActivator(inputChannel = "targetDsl")
    public void handleTargetDsl(String payload) {
        log.info("[dsl] received: {}", payload);
    }

    // ── Runner ──────────────────────────────────────────────────────────

    @Bean
    CommandLineRunner demo(MessageChannel sourceChannel,
                           MessageChannel sourceB,
                           MessageChannel sourceDsl) {

        return args -> {

            log.info("=== 1. @BridgeFrom ===");
            sourceChannel.send(new GenericMessage<>("bf-msg-1"));
            sourceChannel.send(new GenericMessage<>("bf-msg-2"));
            Thread.sleep(1500);

            log.info("=== 2. BridgeHandler @ServiceActivator ===");
            sourceB.send(new GenericMessage<>("bh-msg-1"));
            sourceB.send(new GenericMessage<>("bh-msg-2"));
            Thread.sleep(1500);

            log.info("=== 3. Java DSL ===");
            sourceDsl.send(new GenericMessage<>("dsl-msg-1"));
            sourceDsl.send(new GenericMessage<>("dsl-msg-2"));
            Thread.sleep(1500);

            log.info("=== Lesson 0012 complete ===");
        };
    }
}
