package com.learning.si.lesson0014;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.core.GenericHandler;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.transformer.HeaderEnricher;
import org.springframework.integration.transformer.support.HeaderValueMessageProcessor;
import org.springframework.integration.transformer.support.ExpressionEvaluatingHeaderValueMessageProcessor;
import org.springframework.integration.transformer.support.StaticHeaderValueMessageProcessor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.PollableChannel;
import org.springframework.messaging.support.GenericMessage;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Lesson 0014 — Content Enricher.
 *
 * <p>Demonstrates message enrichment via:
 * <ul>
 *   <li><strong>Header Enricher (Java config)</strong> — add literal and SpEL headers.</li>
 *   <li><strong>Header Enricher (DSL)</strong> — {@code .enrichHeaders()} step.</li>
 *   <li><strong>Payload Enricher with request channel</strong> — enrich a {@code Ticket} by
 *       querying a "service" via a request channel.</li>
 *   <li><strong>Static payload enrichment (no request channel)</strong> — direct property
 *       injection via {@code .enrich()}.property().</li>
 * </ul>
 *
 * <p>Run it:
 * <pre>{@code
 *   cd topics/spring-integration/sandbox
 *   ./mvnw spring-boot:run -Dspring-boot.run.profiles=lesson0014
 * }</pre>
 *
 * @see <a href="https://docs.spring.io/spring-integration/reference/content-enrichment.html">
 *      Content Enricher reference</a>
 */
@Configuration
@Profile("lesson0014")
public class Lesson0014Config {

    private static final Logger log = LoggerFactory.getLogger(Lesson0014Config.class);

    // ── Helper classes ─────────────────────────────────────────────────

    /** A ticket with only an ID; the enricher will fill in the rest. */
    public static class Ticket {
        private int id;
        private String assignee;
        private String status;
        private Instant createdAt;

        public Ticket() {}

        public Ticket(int id) { this.id = id; }

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getAssignee() { return assignee; }
        public void setAssignee(String assignee) { this.assignee = assignee; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public Instant getCreatedAt() { return createdAt; }
        public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

        @Override
        public String toString() {
            return "Ticket{id=%d, assignee='%s', status='%s', createdAt=%s}"
                    .formatted(id, assignee, status, createdAt);
        }
    }

    /** The enriched full view returned by the "lookup service". */
    public record FullDetails(String assignee, String status, Instant createdAt) {}

    // ── Channels ────────────────────────────────────────────────────────

    @Bean
    public MessageChannel headerJavaIn()     { return new DirectChannel(); }
    @Bean
    public PollableChannel headerJavaOut()   { return new QueueChannel(); }

    @Bean
    public MessageChannel headerDslIn()      { return new DirectChannel(); }
    @Bean
    public PollableChannel headerDslOut()    { return new QueueChannel(); }

    @Bean
    public MessageChannel payloadReqIn()     { return new DirectChannel(); }
    @Bean
    public PollableChannel payloadReqOut()   { return new QueueChannel(); }

    @Bean
    public MessageChannel staticIn()         { return new DirectChannel(); }
    @Bean
    public PollableChannel staticOut()       { return new QueueChannel(); }

    // ── 1. Header Enricher — Java configuration ─────────────────────────

    @Bean
    public HeaderEnricher headerEnricherJava() {
        Map<String, HeaderValueMessageProcessor<?>> headers = new HashMap<>();
        headers.put("source", new StaticHeaderValueMessageProcessor<>("enricher-java"));
        headers.put("length",
                new ExpressionEvaluatingHeaderValueMessageProcessor<>(
                        "payload.length()", Integer.class));

        HeaderEnricher enricher = new HeaderEnricher(headers);
        enricher.setDefaultOverwrite(false);
        return enricher;
    }

    @Bean
    public IntegrationFlow headerJavaFlow() {
        return IntegrationFlow.from("headerJavaIn")
                .transform(headerEnricherJava())
                .channel("headerJavaOut")
                .get();
    }

    // ── 2. Header Enricher — DSL ────────────────────────────────────────

    @Bean
    public IntegrationFlow headerDslFlow() {
        return IntegrationFlow.from("headerDslIn")
                .enrichHeaders(h -> h
                        .header("dslSource", "enricher-dsl")
                        .headerExpression("upperCase", "payload.toUpperCase()")
                )
                .channel("headerDslOut")
                .get();
    }

    // ── 3. Payload Enricher with request channel —────────────────────────

    /**
     * A mock "lookup service" that returns enriched details for a ticket ID.
     * In a real app this might call a database or REST endpoint.
     */
    @Bean
    public IntegrationFlow lookupServiceFlow() {
        return IntegrationFlow.from("lookupRequest")
                .handle(Integer.class, (ticketId, headers) -> {
                    log.info("[lookupService] looking up ticket #{}", ticketId);
                    // Simulate a partial response — the enricher maps properties
                    return new FullDetails("Alice", "open", Instant.now());
                })
                .get();
    }

    @Bean
    public MessageChannel lookupRequest() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow payloadEnricherFlow() {
        return IntegrationFlow.from("payloadReqIn")
                .enrich(e -> e
                        .requestChannel("lookupRequest")
                        .requestPayload(p -> ((Ticket) p.getPayload()).getId())
                        .propertyExpression("assignee", "payload.assignee")
                        .propertyExpression("status", "payload.status")
                        .propertyExpression("createdAt", "payload.createdAt")
                )
                .channel("payloadReqOut")
                .get();
    }

    // ── 4. Static payload enrichment (no request channel) ───────────────

    @Bean
    public IntegrationFlow staticEnrichFlow() {
        return IntegrationFlow.from("staticIn")
                .enrich(e -> e
                        .propertyExpression("assignee", "'system'")
                        .property("status", "pending")
                )
                .channel("staticOut")
                .get();
    }

    // ── Runner ──────────────────────────────────────────────────────────

    @Bean
    CommandLineRunner runner(PollableChannel headerJavaOut,
                             PollableChannel headerDslOut,
                             PollableChannel payloadReqOut,
                             PollableChannel staticOut,
                             MessageChannel headerJavaIn,
                             MessageChannel headerDslIn,
                             MessageChannel payloadReqIn,
                             MessageChannel staticIn) {

        return args -> {

            // 1. Header Enricher — Java config
            log.info("=== 1. Header Enricher (Java config) ===");
            headerJavaIn.send(MessageBuilder.withPayload("hello")
                    .setHeader("existing", "keep-me")
                    .build());
            Message<?> hj = headerJavaOut.receive(2000);
            if (hj != null) {
                log.info("[header-java] payload: {}, source={}, length={}, existing={}",
                        hj.getPayload(),
                        hj.getHeaders().get("source"),
                        hj.getHeaders().get("length"),
                        hj.getHeaders().get("existing"));
            }

            // 2. Header Enricher — DSL
            log.info("=== 2. Header Enricher (DSL) ===");
            headerDslIn.send(new GenericMessage<>("spring integration"));
            Message<?> hd = headerDslOut.receive(2000);
            if (hd != null) {
                log.info("[header-dsl] payload: {}, dslSource={}, upperCase={}",
                        hd.getPayload(),
                        hd.getHeaders().get("dslSource"),
                        hd.getHeaders().get("upperCase"));
            }

            // 3. Payload Enricher with request channel
            log.info("=== 3. Payload Enricher (request channel) ===");
            payloadReqIn.send(new GenericMessage<>(new Ticket(42)));
            Message<?> pr = payloadReqOut.receive(2000);
            if (pr != null && pr.getPayload() instanceof Ticket t) {
                log.info("[payload-req] Ticket(id={}, assignee={}, status={}, createdAt={})",
                        t.getId(), t.getAssignee(), t.getStatus(), t.getCreatedAt());
            }

            // 4. Static payload enrichment
            log.info("=== 4. Static payload enrichment (no request channel) ===");
            staticIn.send(new GenericMessage<>(new Ticket(99)));
            Message<?> st = staticOut.receive(2000);
            if (st != null && st.getPayload() instanceof Ticket t) {
                log.info("[static] Ticket(id={}, assignee={}, status={}, createdAt={})",
                        t.getId(), t.getAssignee(), t.getStatus(), t.getCreatedAt());
            }

            log.info("=== Lesson 0014 complete ===");
        };
    }
}
