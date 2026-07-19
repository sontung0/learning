package com.learning.si.lesson0006;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.channel.interceptor.WireTap;
import org.springframework.integration.config.GlobalChannelInterceptor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageBuilder;

/**
 * Lesson 0006 — Channel Interceptors.
 *
 * <p>Demonstrates three interceptor patterns:
 * <ul>
 *   <li><strong>Custom interceptor (veto):</strong> preSend/postSend/afterSendCompletion
 *       logging + veto on payload "veto".</li>
 *   <li><strong>WireTap:</strong> copies every message to a secondary channel.</li>
 *   <li><strong>Global interceptor:</strong> adds an "intercepted" header to all
 *       channels matching "global*".</li>
 * </ul>
 *
 * <p>Run it:
 * <pre>{@code
 *   cd topics/spring-integration/sandbox
 *   ./mvnw spring-boot:run -Dspring-boot.run.profiles=lesson0006
 * }</pre>
 *
 * @see <a href="https://docs.spring.io/spring-integration/reference/channel/interceptors.html">Channel Interceptors reference</a>
 */
@Configuration
@Profile("lesson0006")
public class Lesson0006Config {

    private static final Logger log = LoggerFactory.getLogger(Lesson0006Config.class);

    // ── Channel beans ────────────────────────────────────────────────────

    @Bean
    public SubscribableChannel vetoChannel() {
        DirectChannel channel = new DirectChannel();
        channel.addInterceptor(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel ch) {
                log.info("[preSend] channel={}, payload={}", ch, message.getPayload());
                if ("veto".equals(message.getPayload())) {
                    log.warn("[preSend] VETOing message with payload 'veto'");
                    return null;
                }
                return message;
            }

            @Override
            public void postSend(Message<?> message, MessageChannel ch, boolean sent) {
                log.info("[postSend] channel={}, sent={}, payload={}", ch, sent,
                        message != null ? message.getPayload() : "<null>");
            }

            @Override
            public void afterSendCompletion(
                    Message<?> message, MessageChannel ch, boolean sent, Exception ex) {
                log.info("[afterSendCompletion] channel={}, sent={}, ex={}", ch, sent, ex);
            }
        });
        return channel;
    }

    @Bean
    public SubscribableChannel primaryChannel() {
        return new DirectChannel();
    }

    @Bean
    public SubscribableChannel wiretapChannel() {
        return new PublishSubscribeChannel();
    }

    @Bean
    public WireTap wireTap(SubscribableChannel wiretapChannel) {
        return new WireTap(wiretapChannel);
    }

    @Bean
    public SubscribableChannel globalChannelA() {
        return new DirectChannel();
    }

    @Bean
    public SubscribableChannel globalChannelB() {
        return new DirectChannel();
    }

    @Bean
    @GlobalChannelInterceptor(patterns = "global*", order = 1)
    public ChannelInterceptor globalInterceptor() {
        return new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                log.info("[globalInterceptor] intercepting on {} with payload '{}'",
                        channel, message.getPayload());
                return MessageBuilder.fromMessage(message)
                        .setHeader("intercepted", true)
                        .build();
            }
        };
    }

    // ── Single runner ────────────────────────────────────────────────────

    @Bean
    public CommandLineRunner demo(
            SubscribableChannel vetoChannel,
            SubscribableChannel primaryChannel,
            SubscribableChannel wiretapChannel,
            WireTap wireTap,
            SubscribableChannel globalChannelA,
            SubscribableChannel globalChannelB) {

        return args -> {

            // ── 1. Custom interceptor with veto ─────────────────────────────
            log.info("=== 1. Custom interceptor with veto ===");

            vetoChannel.subscribe(msg ->
                    log.info("Handler got: {}", msg.getPayload()));

            log.info("--- Sending 'hello' (should be delivered) ---");
            vetoChannel.send(new GenericMessage<>("hello"));

            log.info("--- Sending 'veto' (should be cancelled) ---");
            boolean vetoResult = vetoChannel.send(new GenericMessage<>("veto"));
            log.info("send() returned: {}", vetoResult);

            // ── 2. WireTap ─────────────────────────────────────────────────
            log.info("=== 2. WireTap ===");

            ((DirectChannel) primaryChannel).addInterceptor(wireTap);

            wiretapChannel.subscribe(msg ->
                    log.info("WireTap received: {}", msg.getPayload()));

            primaryChannel.subscribe(msg ->
                    log.info("Primary received: {}", msg.getPayload()));

            log.info("--- Sending 'audit-me' ---");
            primaryChannel.send(new GenericMessage<>("audit-me"));

            // ── 3. Global interceptor ──────────────────────────────────────
            log.info("=== 3. Global interceptor ===");

            globalChannelA.subscribe(msg ->
                    log.info("globalChannelA handler: payload={}, intercepted={}",
                            msg.getPayload(), msg.getHeaders().get("intercepted")));

            globalChannelB.subscribe(msg ->
                    log.info("globalChannelB handler: payload={}, intercepted={}",
                            msg.getPayload(), msg.getHeaders().get("intercepted")));

            log.info("--- Sending to globalChannelA ---");
            globalChannelA.send(new GenericMessage<>("alpha"));

            log.info("--- Sending to globalChannelB ---");
            globalChannelB.send(new GenericMessage<>("beta"));
        };
    }
}
