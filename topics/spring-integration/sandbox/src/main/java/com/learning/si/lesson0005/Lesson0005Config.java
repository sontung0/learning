package com.learning.si.lesson0005;

import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.channel.PriorityChannel;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.PollableChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageBuilder;

/**
 * Lesson 0005 — Message Channel Implementations.
 *
 * <p>Demonstrates five of the seven channel implementations:
 * <ul>
 *   <li>{@link DirectChannel} — default, synchronous, point-to-point</li>
 *   <li>{@link QueueChannel} — buffered, polled, point-to-point</li>
 *   <li>{@link PublishSubscribeChannel} — broadcast to all subscribers</li>
 *   <li>{@link ExecutorChannel} — async dispatch via thread pool</li>
 *   <li>{@link PriorityChannel} — ordered by priority header</li>
 * </ul>
 *
 * <p>Run it:
 * <pre>{@code
 *   cd topics/spring-integration/sandbox
 *   ./mvnw spring-boot:run -Dspring-boot.run.profiles=lesson0005
 * }</pre>
 *
 * @see <a href="https://docs.spring.io/spring-integration/reference/channel/implementations.html">Message Channel Implementations reference</a>
 */
@Configuration
@Profile("lesson0005")
public class Lesson0005Config {

    private static final Logger log = LoggerFactory.getLogger(Lesson0005Config.class);

    // ── Channels ──────────────────────────────────────────────────────────

    @Bean
    public SubscribableChannel directChannel() {
        return new DirectChannel();
    }

    @Bean
    public PollableChannel queueChannel() {
        return new QueueChannel(5);
    }

    @Bean
    public SubscribableChannel pubSubChannel() {
        return new PublishSubscribeChannel();
    }

    @Bean
    public SubscribableChannel executorChannel() {
        return new ExecutorChannel(Executors.newFixedThreadPool(3));
    }

    @Bean
    public PollableChannel priorityChannel() {
        return new PriorityChannel(10);
    }

    // ── Single runner ─────────────────────────────────────────────────────

    @Bean
    public CommandLineRunner demo(
            SubscribableChannel directChannel,
            PollableChannel queueChannel,
            SubscribableChannel pubSubChannel,
            SubscribableChannel executorChannel,
            PollableChannel priorityChannel) {

        return args -> {

            // ── 1. DirectChannel — round-robin ─────────────────────────────
            log.info("=== 1. DirectChannel (round-robin) ===");

            directChannel.subscribe(msg ->
                    log.info("DirectHandler A got: {}", msg.getPayload()));
            directChannel.subscribe(msg ->
                    log.info("DirectHandler B got: {}", msg.getPayload()));

            directChannel.send(new GenericMessage<>("Order-1"));
            directChannel.send(new GenericMessage<>("Order-2"));
            directChannel.send(new GenericMessage<>("Order-3"));
            directChannel.send(new GenericMessage<>("Order-4"));

            // ── 2. QueueChannel — buffered, polled ─────────────────────────
            log.info("=== 2. QueueChannel (buffered, polled) ===");

            queueChannel.send(new GenericMessage<>("Task-A"));
            queueChannel.send(new GenericMessage<>("Task-B"));
            queueChannel.send(new GenericMessage<>("Task-C"));
            log.info("Sent 3 tasks. Pulling them with receive(1000)...");

            Message<?> q1 = queueChannel.receive(1000);
            Message<?> q2 = queueChannel.receive(1000);
            Message<?> q3 = queueChannel.receive(1000);
            if (q1 != null) log.info("Pulled: {}", q1.getPayload());
            if (q2 != null) log.info("Pulled: {}", q2.getPayload());
            if (q3 != null) log.info("Pulled: {}", q3.getPayload());

            // ── 3. PublishSubscribeChannel — broadcast ─────────────────────
            log.info("=== 3. PublishSubscribeChannel (broadcast) ===");

            pubSubChannel.subscribe(msg ->
                    log.info("PubSubHandler X got: {}", msg.getPayload()));
            pubSubChannel.subscribe(msg ->
                    log.info("PubSubHandler Y got: {}", msg.getPayload()));

            pubSubChannel.send(new GenericMessage<>("Alert: update available"));

            // ── 4. ExecutorChannel — async ─────────────────────────────────
            log.info("=== 4. ExecutorChannel (async) ===");

            executorChannel.subscribe(msg -> {
                log.info("ExecutorHandler on thread {} got: {}",
                        Thread.currentThread().getName(), msg.getPayload());
            });

            executorChannel.send(new GenericMessage<>("Fire and forget"));
            executorChannel.send(new GenericMessage<>("Another async task"));
            Thread.sleep(500);

            // ── 5. PriorityChannel — ordered ───────────────────────────────
            log.info("=== 5. PriorityChannel (ordered by priority header) ===");

            priorityChannel.send(MessageBuilder.withPayload("Low priority")
                    .setHeader("priority", 1).build());
            priorityChannel.send(MessageBuilder.withPayload("Medium priority")
                    .setHeader("priority", 5).build());
            priorityChannel.send(MessageBuilder.withPayload("High priority")
                    .setHeader("priority", 10).build());
            log.info("Sent with priorities 1, 5, 10. receive(0) should return highest first...");

            Message<?> p1 = priorityChannel.receive(0);
            Message<?> p2 = priorityChannel.receive(0);
            Message<?> p3 = priorityChannel.receive(0);
            if (p1 != null) log.info("1st: {} (priority={})", p1.getPayload(),
                    p1.getHeaders().get("priority"));
            if (p2 != null) log.info("2nd: {} (priority={})", p2.getPayload(),
                    p2.getHeaders().get("priority"));
            if (p3 != null) log.info("3rd: {} (priority={})", p3.getPayload(),
                    p3.getHeaders().get("priority"));
        };
    }
}
