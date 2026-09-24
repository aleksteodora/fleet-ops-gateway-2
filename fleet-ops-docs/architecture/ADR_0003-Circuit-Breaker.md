# ADR_0003 Circuit Breaker for FREE and PREMIUM Providers

## Status
**Accepted** - September 23, 2026

---

## Context

`ADR_0002` established that virtual threads meaningfully improve throughput and reliability under concurrent load, but do not eliminate the FREE→PREMIUM fallback chain's own worst-case latency: when FREE is slow or unavailable, every request still waits out FREE's full read timeout (5s) before falling back to PREMIUM, even when recent calls have already shown FREE to be failing. Under sustained load, this repeated, uninformed waiting compounds queuing delay and was identified as the primary remaining bottleneck.

That proposed wrapping the FREE and PREMIUM provider calls with a Resilience4j circuit breaker: once a provider's recent failure rate crosses a threshold, the circuit "opens" and subsequent calls are skipped immediately (falling straight through to the next provider in the chain) until a cooldown period elapses and a small number of trial calls determine whether the provider has recovered.

## Decision

### Architecture change: extracting `FreeProviderApi` / `PremiumProviderApi`

The circuit breaker is implemented via Resilience4j's `@CircuitBreaker` annotation, which relies on a Spring AOP proxy to intercept method calls. Initially, `@CircuitBreaker` was placed directly on `callProvider()` in `FreeProviderClient`/`PremiumProviderClient`, called internally from `search()` (inherited from `AbstractVehicleProviderClient`) via `this`. This is a well-known Spring AOP limitation (the "self-invocation problem"): a call from within the same class bypasses the proxy entirely, so the circuit breaker never observed any calls, regardless of failure rate.

To resolve this without restructuring `AbstractVehicleProviderClient`'s existing exception-handling logic, the actual provider call was extracted into two new, dedicated beans:

* `FreeProviderApi.callProvider(vin)` - holds the `RestClient` call and the `@CircuitBreaker(name = "freeProvider")` annotation.
* `PremiumProviderApi.callProvider(vin)` - same pattern, `@CircuitBreaker(name = "premiumProvider")`.

`FreeProviderClient`/`PremiumProviderClient` now simply delegate to their respective `*Api` bean - an external, cross-bean call, which correctly passes through the Spring proxy. `AbstractVehicleProviderClient.search()` gained one additional catch clause for `CallNotPermittedException` (thrown when a circuit is OPEN), mapped to the existing `Outcome.UNAVAILABLE`, so no change was needed anywhere downstream (`VehicleSearchService`, `SearchHistoryService`).

### Configuration

```yaml
resilience4j:
  circuitbreaker:
    instances:
      freeProvider:
        sliding-window-size: 10
        failure-rate-threshold: 50
        wait-duration-in-open-state: 10s
        permitted-number-of-calls-in-half-open-state: 3
        minimum-number-of-calls: 5
      premiumProvider:
        sliding-window-size: 10
        failure-rate-threshold: 50
        wait-duration-in-open-state: 15s
        permitted-number-of-calls-in-half-open-state: 3
        minimum-number-of-calls: 5
```

PREMIUM is given a longer `wait-duration-in-open-state` (15s vs. 10s) than FREE: since PREMIUM is billed per call and sits at the end of the fallback chain, we deliberately wait longer before probing it again once it has shown signs of trouble.

### Metrics

`resilience4j-spring-boot3` automatically reports circuit breaker state to Micrometer, already present via `spring-boot-starter-actuator`. No code was needed - only exposing the relevant Actuator endpoints:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health, metrics, prometheus, circuitbreakers, circuitbreakerevents
  endpoint:
    health:
      show-details: always
```

`GET /actuator/circuitbreakers` exposes live state (CLOSED/OPEN/HALF-OPEN), buffered call count, and failure rate per provider; `GET /actuator/metrics/resilience4j.circuitbreaker.state` and the Prometheus endpoint make the same data consumable by external monitoring tooling.

---

## Verification

### Full-cycle manual verification

With the provider simulator stopped, 15 sequential requests were sent via Postman:

* **Calls 1-4:** FREE and PREMIUM both fail normally (connection error), each counted toward the sliding window.
* **Call 5:** first "FREE provider circuit breaker is OPEN, skipping call" - the circuit opened exactly after `minimum-number-of-calls: 5`, at a 100% observed failure rate (well above the 50% threshold).
* **Calls 5-12:** both circuits OPEN, calls skipped immediately rather than waiting out the full timeout.
* **Call 13:** FREE circuit enters HALF-OPEN (its 10s wait duration elapsed) and attempts a real trial call - it still fails (provider was still down), circuit returns to OPEN.

After restarting the provider simulator:

* **Call 24:** a new HALF-OPEN trial call for FREE, still fails (provider up but returning its own simulated 503).
* **Call 25:** trial call succeeds - circuit closes (CLOSED).
* **Call 33:** PREMIUM's longer 15s wait duration elapses; its own HALF-OPEN trial is observed.

This confirms the full CLOSED → OPEN → HALF-OPEN → CLOSED cycle for both providers, independently, with the configured thresholds and wait durations behaving exactly as configured.

### Automated tests

* `FreeProviderClientTest` / `PremiumProviderClientTest`: verify `search()` correctly maps a successful `*Api.callProvider()` result to `FOUND`, and maps `HttpClientErrorException`, `HttpServerErrorException`, and `CallNotPermittedException` (thrown by the `*Api` beans) to `NOT_FOUND` / `UNAVAILABLE` / `UNAVAILABLE` respectively - confirming `AbstractVehicleProviderClient`'s exception handling correctly absorbs the new circuit-breaker-specific exception without any downstream change.
* `FreeProviderApiTest` / `PremiumProviderApiTest`: verify `callProvider()`'s direct `RestClient` interaction (FOUND / 404 / 503), plus a dedicated test that builds an isolated `CircuitBreakerRegistry` and a manual Spring AOP proxy around the `*Api` bean (mirroring what `@CircuitBreaker` does automatically inside a full Spring context, since a Mockito-only unit test has no AOP proxying) to confirm the circuit transitions to OPEN after 5 consecutive failures and that a subsequent call throws `CallNotPermittedException`.

### Load test comparison

Using the same k6 scenario as `ADR_0002` (300 VUs, 10,000 requests, single VIN, identical thresholds), run against an unmodified codebase with the *only* difference being the circuit breaker's presence:

| Metric | VT + Circuit Breaker | VT only (ADR_0002) | Improvement |
|---|---|---|---|
| Total test duration | 36.7s | 3m 50.9s (230.9s) | ~6.3x faster |
| Throughput | 272.5 req/s | 43.3 req/s | ~6.3x higher |
| Avg response time | 1.05s | 6.74s | ~6.4x faster |
| Median (p50) | 438.58ms | 7.77s | ~17.7x faster |
| p90 | 3.19s | 9.19s | ~3x faster |
| p95 | 3.89s | 9.53s | ~2.5x faster |
| Success rate (200 OK) | 99.14% | 99.98% | see below |
| Error rate | 0.86% (86/10000) | 0.02% (2/10000) | see below |

Both runs still exceeded the p95<2500ms threshold, though the circuit breaker run came much closer (3.89s vs. 9.53s).

**On the small increase in error rate:** the circuit breaker run shows *more* failed requests (86 vs. 2), not fewer. This is expected and is the circuit breaker's fail-fast behavior working as intended, not a regression: while a circuit is OPEN, a small number of requests are deliberately rejected quickly (`CallNotPermittedException` → surfaced as `THIRD_PARTY_DOWN` / a non-200 response to the caller) rather than being queued to wait out a provider that recent calls have already shown to be degraded. Trading a small number of fast, explicit failures for a large reduction in latency and a 6x increase in throughput for the remaining 99%+ of requests is the intended tradeoff of this pattern, and is reflected in the dramatically improved median (438ms vs. 7.77s) - the typical request is far better off, at the cost of a small minority being told "unavailable" faster instead of waiting.

---

## Consequences

* **Positive:** Under sustained provider degradation, the circuit breaker stops repeated, uninformed waiting on a provider recent calls have shown to be failing - directly closing the latency gap identified in `ADR_0002`. Measured median latency improved ~17.7x and throughput ~6.3x under the same 300-VU load scenario.
* **Positive:** No change was required to `VehicleSearchService`, `SearchHistoryService`, or any code downstream of `search()` - the new `CallNotPermittedException` path is absorbed at the same layer as existing provider-unavailable handling.
* **Positive:** Circuit breaker state is exposed as metrics via the existing Actuator/Micrometer setup, with no additional code - only configuration.
* **Neutral:** Extracting `FreeProviderApi`/`PremiumProviderApi` was necessary to work around Spring AOP's self-invocation limitation; this is a well-documented Spring pattern for exactly this situation, not project-specific complexity.
* **Trade-off:** A small percentage of requests (0.86% in this test, vs. 0.02% without) now fail fast rather than waiting - a deliberate and, in aggregate, favorable trade for the large majority of requests.
* **Follow-up:** FREE's `failure-rate` in the provider simulator is randomized per call (~50%), rather than representing sustained outages. The manual, full-outage verification (provider stopped) demonstrates the complete CLOSED→OPEN→HALF-OPEN→CLOSED cycle unambiguously; the k6 comparison reflects a more realistic, noisier failure pattern and should be read as a lower-bound improvement estimate, not the ceiling.