package com.learning.si.lesson0009;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.GlobalChannelInterceptor;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.GenericMessage;

/**
 * Lesson 0009 — Special Channels.
 *
 * <p>Demonstrates the framework-provided infrastructure channels:
 * <ul>
 *   <li><strong>nullChannel:</strong> a sink that discards messages
 *       (like {@code /dev/null}), logging them at DEBUG level.</li>
 *   <li><strong>errorChannel:</strong> the global channel for async error
 *       handling; publishes {@code ErrorMessage} with the exception as payload.
 *       Can be customised by defining your own bean named {@code "errorChannel"}.</li>
 *   <li><strong>Wire tap:</strong> intercepting messages without affecting
 *       the primary flow, using a {@link ChannelInterceptor}.</li>
 * </ul>
 *
 * <p>Run it:
 * <pre>{@code
 *   cd topics/spring-integration/sandbox
 *   ./mvnw spring-boot:run -Dspring-boot.run.profiles=lesson0009
 * }</pre>
 *
 * @see <a href="https://docs.spring.io/spring-integration/reference/channel/special-channels.html">
 *      Special Channels reference</a>
 * @see <a href="https://docs.spring.io/spring-integration/reference/error-handling.html">
 *      Error Handling reference</a>
 */
@Configuration
@Profile("lesson0009")
public class Lesson0009Config {

    private static final Logger log = LoggerFactory.getLogger(Lesson0009Config.class);

    // ── Channel beans ────────────────────────────────────────────────────

    /**
     * An async channel used to trigger an error that will be published
     * to the <strong>global error channel</strong> (the default
     * {@code PublishSubscribeChannel} named {@code "errorChannel"}).
     * Because the handler runs on a pool thread, exceptions can't propagate
     * to the sender — Spring Integration wraps them in an {@code ErrorMessage}
     * and publishes to the error channel instead.
     */
    @Bean
    public SubscribableChannel asyncErrorChannel() {
        return new ExecutorChannel(new SimpleAsyncTaskExecutor("async-err-"));
    }

    /**
     * A simple channel that we'll wire-tap.
     */
    @Bean
    public SubscribableChannel mainChannel() {
        return new org.springframework.integration.channel.DirectChannel();
    }

    // ── Wire tap interceptor ─────────────────────────────────────────────

    /**
     * A global channel interceptor that logs every message sent to any
     * channel whose name ends with "Channel" — effectively a wire tap.
     *
     * <p>This is applied globally but filtered by pattern.
     * The interceptor is read-only: it merely observes, never modifies
     * the message.
     */
    @Bean
    @GlobalChannelInterceptor(patterns = "*Channel")
    public ChannelInterceptor wireTapInterceptor() {
        return new ChannelInterceptor() {

            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                log.info("[wire-tap] channel={}, payload={} ({}), headers={}",
                        channel instanceof org.springframework.integration.channel.AbstractMessageChannel amc
                                ? amc.getFullChannelName()
                                : channel.toString(),
                        message.getPayload(),
                        message.getPayload().getClass().getSimpleName(),
                        message.getHeaders().getId());
                return message;
            }
        };
    }

    // ── Runner ───────────────────────────────────────────────────────────

    @Bean
    public CommandLineRunner demo(
            MessageChannel nullChannel,
            MessageChannel errorChannel,
            SubscribableChannel asyncErrorChannel,
            SubscribableChannel mainChannel) {

        return args -> {

            // ── 1. nullChannel — /dev/null for messages ──────────────────
            log.info("=== 1. nullChannel (sink) ===");

            // Send a message to nullChannel — it will be logged at DEBUG
            // and discarded. No subscriber is needed.
            boolean sent = nullChannel.send(new GenericMessage<>("discard-me"));
            log.info("[nullChannel] send returned {}", sent);

            // You can also use nullChannel as a reply channel for flows
            // where you don't care about the response:
            nullChannel.send(
                    MessageBuilder.withPayload("no-reply-needed")
                            .setReplyChannel(nullChannel)
                            .build());

            // ── 2. errorChannel — async error handling ────────────────────
            log.info("=== 2. errorChannel (async error handling) ===");

            // Subscribe to the global error channel to inspect errors.
            // (The default errorChannel is a PublishSubscribeChannel that
            // also has a LoggingHandler subscriber at ERROR level.)
            if (errorChannel instanceof SubscribableChannel sub) {
                sub.subscribe(errorMsg -> {
                    log.info("[error subscriber] received: {}",
                            errorMsg.getPayload().getClass().getSimpleName());
                    log.info("[error subscriber] cause: {}",
                            ((Throwable) errorMsg.getPayload()).getMessage());
                    log.info("[error subscriber] failed message payload: {}",
                            errorMsg.getHeaders().get("messagePayload"));
                });
            }

            // Subscribe a handler to the async channel that always throws.
            asyncErrorChannel.subscribe(msg -> {
                log.info("[async handler] processing: {}", msg.getPayload());
                throw new RuntimeException("Something went wrong processing: "
                        + msg.getPayload());
            });

            // Send a message — the handler runs on a pool thread, so the
            // exception goes to the error channel, not back to us.
            asyncErrorChannel.send(new GenericMessage<>("trigger-error"));

            // Give the async thread time to fail and the error to publish
            Thread.sleep(500);

            log.info("[main] after async error — exception was routed to errorChannel");

            // ── 3. Wire tap — intercepting messages ───────────────────────
            log.info("=== 3. Wire tap (via @GlobalChannelInterceptor) ===");

            // This send will be intercepted by our wire-tap interceptor
            mainChannel.subscribe(msg ->
                    log.info("[mainChannel handler] got: {}", msg.getPayload()));

            mainChannel.send(new GenericMessage<>("wire-tap-me"));

            log.info("=== Lesson 0009 complete ===");
        };
    }
}
