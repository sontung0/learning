# Learning Record 0013 — Transformer

## What Was Taught

Lesson 0013 covered the **Transformer** — a message endpoint that modifies a message's payload, headers, or both.

1. **Role:** A transformer converts a message in flight. Unlike a channel adapter (external bridge) or a bridge (zero-transformation forwarder), the transformer changes the content. It's one of the most commonly used endpoints in any integration flow.

2. **Non-negotiable rule:** A transformer **must not return `null`**. Because `requiresReply` is always `true` for transformers, returning `null` throws a `MessageTransformationException`. If you need a conditional path that may produce no output, use `@ServiceActivator` instead.

3. **Five demonstration sections in the sandbox:**

   - **Section 1 — `@Transformer` annotation:** A plain POJO method annotated with `@Transformer(inputChannel = "annotationIn", outputChannel = "annotationOut")` converting a String to uppercase. Key: the method is on a POJO (not annotated with `@Bean`). The framework's `TransformerAnnotationPostProcessor` wraps it.
   
   - **Section 2 — `ObjectToStringTransformer`:** A built-in transformer configured as `@Bean @Transformer(inputChannel = "stringIn", outputChannel = "stringOut")` returning `new ObjectToStringTransformer()`. This calls `toString()` on any payload — converts `Integer(12345)` to `"12345"`.
   
   - **Section 3 — SpEL expression transformer:** DSL `.transform("payload.toUpperCase() + ' via SpEL'")` — no Java class needed, just a SpEL expression string.
   
   - **Section 4 — JSON round-trip:** DSL `.transform(Transformers.toJson())` followed by `transform(Transformers.fromJson(Person.class))` — object → JSON (intercepted via wireTap) → back to object. Requires `jackson-databind` on the classpath.
   
   - **Section 5 — Header filter:** DSL `.headerFilter("sensitive")` strips the `sensitive` header while preserving `public`. The output shows only `public=safe` in headers.

4. **Built-in transformers overview:** ObjectToString, ObjectToMap, MapToObject, JsonToObject, ObjectToJson, Stream, FileToString, FileToByteArray, SyslogToMap, Avro, Protobuf.

5. **`Transformers` utility class** (in `org.springframework.integration.dsl`, *not* `transformer.support`) provides static factory methods: `Transformers.toJson()`, `Transformers.fromJson(Class)`, `Transformers.objectToString()`, etc.

## Sandbox Verification

All 5 sections ran successfully with zero errors:

```
=== 1. @Transformer annotation ===
[@Transformer] output: HELLO
=== 2. ObjectToStringTransformer ===
[toString] output: 12345 (type: String)
=== 3. SpEL expression transformer ===
[spel] output: TRANSFORM ME via SpEL
=== 4. JSON transformer round-trip ===
[json-raw] {"name":"Alice","age":30}
[json-out] Person(name=Alice, age=30)
=== 5. Header filter ===
[header-filter] payload: secret, headers: {public=safe, ...}
=== Lesson 0013 complete ===
```

The `sensitive` header was successfully stripped in section 5. The JSON wireTap in section 4 correctly showed the intermediate raw JSON before deserialization.

## Key Nuances Learned

- **`@Transformer` on a POJO method vs a `@Bean` method:** A plain method (no `@Bean`) is processed by `TransformerAnnotationPostProcessor` and the method itself is the transformer. A `@Bean` + `@Transformer` method returns a transformer instance (like `ObjectToStringTransformer`). Both work, but the semantics differ: the former creates a wrapping handler; the latter registers the returned bean as-is.
- **`Transformers` is in `dsl` package:** The correct import is `org.springframework.integration.dsl.Transformers`, NOT `transformer.support.Transformers` (which doesn't exist in 7.1.0).
- **JSON transformers need jackson-databind:** Without it, `ObjectToJsonTransformer.<init>` throws `IllegalStateException: "No jackson-databind.jar is present in the classpath."`. The `spring-boot-starter-integration` starter does NOT include it — must be added explicitly.
- **Header filter operates on headers only:** It doesn't filter messages; it filters header keys. The payload passes through unchanged.
- **SpEL transformers are convenient but opaque:** The expression is a string evaluated at runtime. No compile-time safety. Good for simple transformations; complex logic should use a POJO `@Transformer` method or a `GenericTransformer` lambda.
- **`GenericTransformer<T, R>`** is the functional interface behind the DSL `.transform()` — you can pass a lambda: `.transform(p -> ((String) p).toUpperCase())`.

## Issues Encountered

1. **Wrong `Transformers` import path:** Initially imported `org.springframework.integration.transformer.support.Transformers` which doesn't exist. Correct path: `org.springframework.integration.dsl.Transformers`.
2. **Missing `jackson-databind`:** JSON transformers failed at runtime with `IllegalStateException`. Fixed by adding `<dependency><groupId>com.fasterxml.jackson.core</groupId><artifactId>jackson-databind</artifactId></dependency>` to `pom.xml`.

## ZPD

~20 min. The transformer is conceptually simple (input → conversion → output), but the variety of configuration styles and built-in types required more exploration. The `Transformers` DSL utility and the null-return rule were the key learning points. JSON transformers needing an explicit dependency was a practical gotcha.

Ready for Content Enricher next.
