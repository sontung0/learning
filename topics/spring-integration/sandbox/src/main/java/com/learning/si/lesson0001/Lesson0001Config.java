package com.learning.si.lesson0001;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;

/**
 * Lesson 0001 — Overview: Messages, Channels &amp; Endpoints.
 *
 * <p>The whole framework in miniature:
 * <ul>
 *   <li>a {@link QueueChannel} — a <em>pollable</em> message channel (the "pipe"),</li>
 *   <li>a {@link ServiceActivator} POJO method — a message <em>endpoint</em> (the "filter"),</li>
 *   <li>a {@link CommandLineRunner} that sends one {@code Message} into the pipe.</li>
 * </ul>
 *
 * <p>Because the channel is pollable, the framework needs a poller to pull messages from it. A
 * default poller bean is provided below, scoped to this lesson's profile so it never affects
 * other lessons.
 *
 * <p>Run it:
 * <pre>{@code
 *   cd topics/spring-integration/sandbox
 *   ./mvnw spring-boot:run -Dspring-boot.run.profiles=lesson0001
 * }</pre>
 *
 * @see <a href="https://docs.spring.io/spring-integration/reference/overview.html">Overview reference</a>
 */
@Configuration
@Profile("lesson0001")
public class Lesson0001Config {

    private static final Logger log = LoggerFactory.getLogger(Lesson0001Config.class);

    /** The "pipe": a pollable, buffering point-to-point channel. */
    @Bean
    public MessageChannel greetings() {
        return new QueueChannel();
    }

    /**
     * Default poller for this context. A pollable channel is only read when a poller drives it;
     * this one polls every second so our single message is picked up promptly.
     */
    @Bean(name = PollerMetadata.DEFAULT_POLLER)
    public PollerMetadata defaultPoller() {
        return Pollers.fixedDelay(1000L)
                .maxMessagesPerPoll(1)
                .getObject();
    }

    /**
     * The "filter": a POJO endpoint subscribed to {@code greetings}. The framework extracts the
     * String payload, invokes this method, and (here) we simply log the produced greeting.
     */
    @ServiceActivator(inputChannel = "greetings")
    public void greet(String name) {
        log.info("Service activator handled a message → Hello, {}!", name);
    }

    /** Sends exactly one message into the channel at startup so you can watch it flow. */
    @Bean
    public CommandLineRunner sendOne(MessageChannel greetings) {
        return args -> {
            log.info("Sending one message with payload 'Spring Integration' into the 'greetings' channel...");
            greetings.send(new GenericMessage<>("Spring Integration"));
        };
    }
}
