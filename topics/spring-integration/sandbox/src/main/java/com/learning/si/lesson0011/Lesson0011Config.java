package com.learning.si.lesson0011;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.PollableChannel;
import org.springframework.messaging.support.GenericMessage;

import java.time.Instant;

/**
 * Lesson 0011 — Channel Adapter.
 *
 * <p>Demonstrates the two channel adapter patterns:
 * <ul>
 *   <li><strong>Inbound Channel Adapter</strong> — a producer endpoint that
 *       polls a method/expression and sends the return value to a channel.</li>
 *   <li><strong>Outbound Channel Adapter</strong> — a consumer endpoint that
 *       receives messages from a channel and invokes a void-returning method
 *       (a "sink" to external systems).</li>
 * </ul>
 *
 * <p>Run it:
 * <pre>{@code
 *   cd topics/spring-integration/sandbox
 *   ./mvnw spring-boot:run -Dspring-boot.run.profiles=lesson0011
 * }</pre>
 *
 * @see <a href="https://docs.spring.io/spring-integration/reference/channel-adapter.html">
 *      Channel Adapter reference</a>
 */
@Configuration
@Profile("lesson0011")
public class Lesson0011Config {

    private static final Logger log = LoggerFactory.getLogger(Lesson0011Config.class);

    // ── Section 1: @InboundChannelAdapter (method-based) ────────────────
    // A method annotated with @InboundChannelAdapter is polled at a fixed
    // rate.  Every non-null return value is wrapped in a Message and sent to
    // the named channel.

    @Bean
    public PollableChannel adapterChannel() {
        return new QueueChannel();
    }

    @InboundChannelAdapter(channel = "adapterChannel",
                           poller = @Poller(fixedRate = "1000"))
    public String timestampSource() {
        return "adapter-" + Instant.now();
    }

    // ── Section 2: Inbound via DSL IntegrationFlow ──────────────────────
    // Equivalent to section 1, but expressed with the Java DSL.

    @Bean
    public PollableChannel dslChannel() {
        return new QueueChannel();
    }

    @Bean
    public IntegrationFlow inboundFlow() {
        return IntegrationFlow.from(
                (MessageSource<String>) () ->
                        MessageBuilder.withPayload("dsl-" + Instant.now()).build(),
                c -> c.poller(Pollers.fixedRate(1500)))
                .channel("dslChannel")
                .get();
    }

    // ── Section 3: Outbound Channel Adapter ─────────────────────────────
    // A @ServiceActivator that receives messages from a channel and
    // forwards them to a method — no return value (the "sink").

    @Bean
    public MessageChannel outboundSink() {
        return new DirectChannel();
    }

    @ServiceActivator(inputChannel = "outboundSink")
    public void handleOutbound(String payload) {
        log.info("[outbound-consumer] handled: {}", payload);
    }

    // ── Runner ──────────────────────────────────────────────────────────

    @Bean
    CommandLineRunner demo(PollableChannel adapterChannel,
                           PollableChannel dslChannel,
                           MessageChannel outboundSink) {

        return args -> {

            log.info("=== 1. @InboundChannelAdapter ===");
            log.info("Polling adapterChannel for 3 messages (every 1s)...");

            for (int i = 1; i <= 3; i++) {
                Message<?> msg = adapterChannel.receive(3000);
                log.info("[adapter] received #{}: {}",
                        i, msg != null ? msg.getPayload() : "null");
            }

            log.info("=== 2. Inbound via DSL IntegrationFlow ===");
            log.info("Polling dslChannel for 2 messages (every 1.5s)...");

            for (int i = 1; i <= 2; i++) {
                Message<?> msg = dslChannel.receive(3000);
                log.info("[dsl] received #{}: {}",
                        i, msg != null ? msg.getPayload() : "null");
            }

            log.info("=== 3. Outbound Channel Adapter ===");
            log.info("Sending 3 messages to outboundSink...");

            outboundSink.send(new GenericMessage<>("outbound-1"));
            outboundSink.send(new GenericMessage<>("outbound-2"));
            outboundSink.send(new GenericMessage<>("outbound-3"));

            log.info("=== Lesson 0011 complete ===");
        };
    }
}
