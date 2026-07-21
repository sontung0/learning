package com.learning.si.lesson0013;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Transformers;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.transformer.ObjectToStringTransformer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.PollableChannel;
import org.springframework.messaging.support.GenericMessage;

/**
 * Lesson 0013 — Transformer.
 *
 * <p>Demonstrates message transformation via:
 * <ul>
 *   <li><strong>{@code @Transformer}</strong> annotation on a POJO method.</li>
 *   <li><strong>Built-in </strong>{@link ObjectToStringTransformer}.</li>
 *   <li><strong>SpEL</strong> expression-based transformer.</li>
 *   <li><strong>JSON</strong> round-trip (object → JSON → object).</li>
 *   <li><strong>Header filter</strong> to strip unwanted headers.</li>
 * </ul>
 *
 * <p>Run it:
 * <pre>{@code
 *   cd topics/spring-integration/sandbox
 *   ./mvnw spring-boot:run -Dspring-boot.run.profiles=lesson0013
 * }</pre>
 *
 * @see <a href="https://docs.spring.io/spring-integration/reference/transformer.html">
 *      Transformer reference</a>
 */
@Configuration
@Profile("lesson0013")
public class Lesson0013Config {

    private static final Logger log = LoggerFactory.getLogger(Lesson0013Config.class);

    // ── Helper record ───────────────────────────────────────────────────

    public record Person(String name, int age) {}

    // ── 1. @Transformer annotation on a POJO method ─────────────────────

    @Transformer(inputChannel = "annotationIn", outputChannel = "annotationOut")
    public String upper(String payload) {
        return payload.toUpperCase();
    }

    @Bean
    public MessageChannel annotationIn()  { return new DirectChannel(); }
    @Bean
    public QueueChannel annotationOut()   { return new QueueChannel(); }

    // ── 2. Built-in ObjectToStringTransformer ──────────────────────────

    @Bean
    @Transformer(inputChannel = "stringIn", outputChannel = "stringOut")
    public ObjectToStringTransformer toStringTransformer() {
        return new ObjectToStringTransformer();
    }

    @Bean
    public MessageChannel stringIn()       { return new DirectChannel(); }
    @Bean
    public QueueChannel stringOut()        { return new QueueChannel(); }

    // ── 3. SpEL expression transformer ─────────────────────────────────

    @Bean
    public IntegrationFlow spelFlow() {
        return IntegrationFlow.from("spelIn")
                .transform("payload.toUpperCase() + ' via SpEL'")
                .channel("spelOut")
                .get();
    }

    @Bean
    public MessageChannel spelIn()         { return new DirectChannel(); }
    @Bean
    public QueueChannel spelOut()          { return new QueueChannel(); }

    // ── 4. JSON round-trip via DSL ─────────────────────────────────────

    @Bean
    public IntegrationFlow jsonFlow() {
        return IntegrationFlow.from("jsonIn")
                .transform(Transformers.toJson())
                .wireTap(f -> f.handle(m ->
                        log.info("[json-raw] {}", m.getPayload())))
                .transform(Transformers.fromJson(Person.class))
                .channel("jsonOut")
                .get();
    }

    @Bean
    public MessageChannel jsonIn()         { return new DirectChannel(); }
    @Bean
    public QueueChannel jsonOut()          { return new QueueChannel(); }

    // ── 5. Header filter ──────────────────────────────────────────────

    @Bean
    public IntegrationFlow filterFlow() {
        return IntegrationFlow.from("filterIn")
                .headerFilter("sensitive")
                .channel("filterOut")
                .get();
    }

    @Bean
    public MessageChannel filterIn()       { return new DirectChannel(); }
    @Bean
    public QueueChannel filterOut()        { return new QueueChannel(); }

    // ── Runner ──────────────────────────────────────────────────────────

    @Bean
    CommandLineRunner runner(PollableChannel annotationOut,
                             PollableChannel stringOut,
                             PollableChannel spelOut,
                             PollableChannel jsonOut,
                             PollableChannel filterOut,
                             MessageChannel annotationIn,
                             MessageChannel stringIn,
                             MessageChannel spelIn,
                             MessageChannel jsonIn,
                             MessageChannel filterIn) {

        return args -> {

            // 1. @Transformer
            log.info("=== 1. @Transformer annotation ===");
            annotationIn.send(new GenericMessage<>("hello"));
            Message<?> a = annotationOut.receive(2000);
            log.info("[@Transformer] output: {}", a != null ? a.getPayload() : "null");

            // 2. ObjectToStringTransformer
            log.info("=== 2. ObjectToStringTransformer ===");
            stringIn.send(new GenericMessage<>(12345));
            Message<?> s = stringOut.receive(2000);
            log.info("[toString] output: {} (type: {})",
                    s != null ? s.getPayload() : "null",
                    s != null ? s.getPayload().getClass().getSimpleName() : "n/a");

            // 3. SpEL expression
            log.info("=== 3. SpEL expression transformer ===");
            spelIn.send(new GenericMessage<>("transform me"));
            Message<?> sp = spelOut.receive(2000);
            log.info("[spel] output: {}", sp != null ? sp.getPayload() : "null");

            // 4. JSON round-trip
            log.info("=== 4. JSON transformer round-trip ===");
            jsonIn.send(new GenericMessage<>(new Person("Alice", 30)));
            Message<?> j = jsonOut.receive(2000);
            if (j != null && j.getPayload() instanceof Person p) {
                log.info("[json-out] Person(name={}, age={})", p.name(), p.age());
            } else {
                log.info("[json-out] {}", j != null ? j.getPayload() : "null");
            }

            // 5. Header filter
            log.info("=== 5. Header filter ===");
            filterIn.send(MessageBuilder.withPayload("secret")
                    .setHeader("sensitive", "do-not-leak")
                    .setHeader("public", "safe")
                    .build());
            Message<?> f = filterOut.receive(2000);
            if (f != null) {
                log.info("[header-filter] payload: {}, headers: {}",
                        f.getPayload(), f.getHeaders());
            }

            log.info("=== Lesson 0013 complete ===");
        };
    }
}
