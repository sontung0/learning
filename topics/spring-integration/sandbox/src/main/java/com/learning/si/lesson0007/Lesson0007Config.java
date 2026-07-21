package com.learning.si.lesson0007;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.PollableChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.GenericMessage;

/**
 * Lesson 0007 — MessagingTemplate.
 *
 * <p>Demonstrates programmatic send/receive against channels using
 * {@link MessagingTemplate}:
 * <ul>
 *   <li><strong>sendAndReceive:</strong> request-reply with a temporary
 *       anonymous reply channel.</li>
 *   <li><strong>Channel.receive():</strong> pull from a PollableChannel.</li>
 *   <li><strong>Channel.send():</strong> fire-and-forget to a SubscribableChannel.</li>
 * </ul>
 *
 * <p>Run it:
 * <pre>{@code
 *   cd topics/spring-integration/sandbox
 *   ./mvnw spring-boot:run -Dspring-boot.run.profiles=lesson0007
 * }</pre>
 *
 * @see <a href="https://docs.spring.io/spring-integration/reference/channel/template.html">
 *      MessagingTemplate reference</a>
 */
@Configuration
@Profile("lesson0007")
public class Lesson0007Config {

    private static final Logger log = LoggerFactory.getLogger(Lesson0007Config.class);

    // ── Channel beans ────────────────────────────────────────────────────

    @Bean
    public SubscribableChannel sendChannel() {
        return new DirectChannel();
    }

    @Bean
    public PollableChannel receiveChannel() {
        return new QueueChannel();
    }

    /**
     * A request channel for sendAndReceive — the handler sends back an uppercased reply.
     */
    @Bean
    public SubscribableChannel requestChannel() {
        DirectChannel channel = new DirectChannel();
        channel.subscribe(msg -> {
            String payload = (String) msg.getPayload();
            String reply = payload.toUpperCase();
            log.info("[requestChannel handler] got '{}', replying with '{}'", payload, reply);
            ((MessageChannel) msg.getHeaders().getReplyChannel()).send(new GenericMessage<>(reply));
        });
        return channel;
    }

    // ── Runner ───────────────────────────────────────────────────────────

    @Bean
    public CommandLineRunner demo(
            SubscribableChannel sendChannel,
            PollableChannel receiveChannel,
            SubscribableChannel requestChannel) {

        return args -> {
            MessagingTemplate template = new MessagingTemplate();
            template.setSendTimeout(2000L);
            template.setReceiveTimeout(2000L);

            // ── 1. Fire-and-forget via Channel.send() ────────────────────
            log.info("=== 1. Channel.send() — fire-and-forget ===");

            sendChannel.subscribe(msg ->
                    log.info("[sendChannel handler] received: {}", msg.getPayload()));

            boolean sent = sendChannel.send(new GenericMessage<>("hello-send"));
            log.info("sendChannel.send() returned: {}", sent);

            // ── 2. Receive via PollableChannel.receive() ─────────────────
            log.info("=== 2. PollableChannel.receive() ===");

            receiveChannel.send(new GenericMessage<>("msg-1"));
            receiveChannel.send(new GenericMessage<>("msg-2"));
            receiveChannel.send(new GenericMessage<>("msg-3"));

            for (int i = 0; i < 4; i++) {
                Message<?> received = receiveChannel.receive(2000);
                if (received != null) {
                    log.info("receive() got: {}", received.getPayload());
                } else {
                    log.warn("receive() returned null (queue empty or timeout)");
                }
            }

            // ── 3. Request-reply via MessagingTemplate.sendAndReceive() ──
            log.info("=== 3. MessagingTemplate.sendAndReceive() ===");

            Message<?> reply = template.sendAndReceive(
                    requestChannel, new GenericMessage<>("hello request"));
            log.info("sendAndReceive() reply: {}", reply.getPayload());

            log.info("=== Lesson 0007 complete ===");
        };
    }
}
