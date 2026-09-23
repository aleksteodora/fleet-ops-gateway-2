# ADR_0002 Timeouts and Virtual Threads for Request Handling

## Status
**Accepted** - September 22, 2026

---

## Context

As outlined in `ADR_0001`, FleetOps Gateway is expected to scale toward thousands of users and millions of monthly queries, with occasional traffic surges. The current `VehicleSearchService` flow - calling FREE, falling back to PREMIUM, writing search history - is fully synchronous and blocking.

Two immediate risks were identified as part of Improve search performance:

1. The `RestClient` beans used to call FREE and PREMIUM had no connect or read timeout configured, so a slow or unresponsive provider could block a request indefinitely.
2. Under Java's traditional (platform) thread model, each blocked thread ties up an OS thread for the full duration of an external call. Since the Tomcat thread pool is finite, a burst of concurrent requests - especially while FREE is degraded - can exhaust the pool and cause new requests to queue or be rejected, even though the CPU itself is idle.

---

## Decision

### 1. Explicit connect/read timeouts

Both provider `RestClient` beans (`RestClientConfig`) now build their request factory with explicit timeouts, sourced from `application.yml`:

| Provider | Connect timeout | Read timeout |
|---|---|---|
| FREE | 2000ms | 5000ms |
| PREMIUM | 2000ms | 2000ms |

FREE is given a longer read timeout since its role is either a fast success or a fast, deliberate failure before falling back; PREMIUM, as the last line of the fallback chain, is kept tighter so a stuck request doesn't compound the total wait. Both values are configuration-only - no code change is needed to retune them.

### 2. Virtual threads

`spring.threads.virtual.enabled=true` was enabled in `application-dev.yml`. No application code changes were required - `RestClient`, JDBC, and the rest of the blocking call chain are compatible without modification, as documented by Spring Boot. This lets the JVM schedule many concurrently-blocked requests onto a small pool of OS threads instead of exhausting Tomcat's platform thread pool.

---

## Verification

### Timeouts

* `RestClientConfigTest` covers both failure modes with a real, local `HttpServer`/`ServerSocket`, not mocks:
    * **Read timeout:** a local `HttpServer` accepts the connection but deliberately delays its response past the configured read timeout.
    * **Connect timeout:** a local `ServerSocket` with a backlog of 1 is pre-filled with a blocking connection, so a second connection attempt has to wait past the configured connect timeout with no listener ever accepting it.
* Both tests assert the client throws `ResourceAccessException` - the same exception type `AbstractVehicleProviderClient` already catches and maps to `Outcome.UNAVAILABLE`, so no downstream code needed to change.
* Manually verified end-to-end with deliberately tiny timeouts (2ms/5ms): both providers correctly timed out and the endpoint returned `THIRD_PARTY_DOWN`, confirming the mechanism is wired through the whole fallback chain, not just the client in isolation.

### Virtual threads

* Confirmed via direct inspection (`Thread.currentThread().isVirtual()` logged from the controller) that request-handling threads are backed by `VirtualThread` instances once enabled, not just that the configuration flag is set.
* Existing endpoints were re-tested manually (FOUND / NOT_FOUND / THIRD_PARTY_DOWN) to confirm no behavioral change from enabling virtual threads.

### Load test methodology

**Baseline (no load):** 20 sequential manual requests via Postman against a single VIN established a solo response-time baseline: 96ms-796ms (avg ~211ms), reflecting normal, uncontended FREE/PREMIUM fallback behavior.

**Load scenario:** Using [k6](https://k6.io), we defined a fixed scenario of **300 concurrent virtual users (VUs)** issuing a combined **10,000 requests** against `GET /api/v1/vehicle-search`, all for the same VIN (isolating thread-handling/concurrency behavior from variance in provider response content). 300 VUs was chosen as a deliberately aggressive multiple of the solo baseline's implicit concurrency of 1, intended to expose thread-pool exhaustion under a realistic traffic-surge scenario (per `ADR_0001`'s anticipated "tens of thousands of requests" bursts); 10,000 iterations gives a large enough sample for a statistically meaningful p95, while keeping each run short enough to repeat multiple times during comparison testing.

**Threshold:** p95 request duration < 2500ms, error rate < 5%. The duration threshold was not chosen arbitrarily: it was derived from the solo baseline's observed maximum (796ms) multiplied by an approximate 3x tolerance factor, accounting for the small baseline sample size (20 requests) and the contention expected under 300 concurrent VUs, while still being tight enough to distinguish genuine saturation from the fallback chain's own inherent worst-case latency.

**Comparison:** The identical scenario was run twice against an unmodified codebase, with `spring.threads.virtual.enabled` (true vs. false) as the only variable. The gateway and the provider simulator were both restarted between runs to eliminate any carried-over state from a prior run.

### Results

| Metric | With Virtual Threads | Without Virtual Threads | Improvement |
|---|---|---|---|
| Total test duration | 3m 50.9s | 6m 08.2s | ~37% faster |
| Throughput | 43.30 req/s | 27.15 req/s | ~60% higher |
| Avg response time | 6.74s | 10.79s | ~37.5% faster |
| Median | 7.77s | 11.47s | ~32.2% faster |
| p95 | 9.53s | 15.96s | ~40% faster |
| Max | 12.22s | 29.09s | >2.3x lower peak latency |
| Error rate | 0.02% (2/10000) | 0.65% (65/10000) | ~32.5x fewer failures |

Both runs still exceeded the p95<2500ms threshold. This is expected, not a defect: under 300 concurrent VUs, the FREE→PREMIUM fallback chain's own worst-case latency (FREE's 5s read timeout, potentially followed by a PREMIUM call) compounds with queuing delay under contention. Virtual threads measurably reduce that queuing delay and nearly eliminate failures, but they do not - and are not intended to - eliminate the fallback chain's own latency. Closing that remaining gap is the explicit goal of the next step, the FREE→PREMIUM circuit breaker, which will be evaluated and documented in a separate ADR.

---

## Consequences

* **Positive:** A request stuck on a slow provider can no longer block a thread indefinitely; timeout values are configuration-only, requiring no code change to retune.
* **Positive:** Measured, meaningful improvement in throughput, latency, and reliability under concurrent load from virtual threads, at effectively zero implementation cost (one configuration line, no code changes).
* **Positive:** No behavioral or API change from either mechanism; fully backward-compatible with existing blocking code.
* **Neutral:** Virtual threads address thread-pool exhaustion, not the underlying latency of a slow/unavailable provider - the results above directly motivate the next planned mechanism (circuit breaker), to be covered in its own ADR.