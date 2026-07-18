# Error handling recovery strategies

The user demonstrated understanding of Spring Cloud Stream's recovery model: transient handler failures should use binding retry, invalid events belong in a DLQ for durable investigation, binding-specific error channels publish `ErrorMessage` instances in-process, and `max-attempts: 5` yields four retries after the initial call. This establishes the foundation for the next Phase 4 topic, observability.

**Evidence:** The user answered the Lesson 0014 assessment, correctly distinguishing error channels from DLQs and calculating retry count.