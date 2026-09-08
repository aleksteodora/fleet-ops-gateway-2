# ADR_0001 System Architecture: Modular Monolith and Domain Decomposition

## Status
**Accepted** - September 7, 2026

---

## Context
FleetOps Gateway is a B2B SaaS platform for vehicle data lookup via VIN.

The system serves two main user profiles (**Admin** and **Company User**), integrates with two external data sources (**Free Provider** and **Premium Provider**), and tracks query history.

### Current Scale & Anticipated Growth
* **Current MVP Scope:** Low volume; tens of clients, a few hundred users.
* **Potential Future Scale:** Hundreds of clients, thousands of users, millions of monthly queries, with occasional traffic surges reaching tens of thousands of requests.
* **Cost Factor:** The Premium Data Provider charges per API call. Controlling when and how we invoke this provider directly impacts business margins.

We need to decide on:
1. The macro-architectural style (Monolith vs. Microservices).
2. The internal code organization strategy within the selected architecture.

---

## Considered Options

### Option A: Standard Monolith (Package-by-Layer)
A single application structured globally by technical roles (`controller/`, `service/`, `repository/`).

* **Pros:** Quickest to set up initially; standard structure out of the box in Spring Boot.
* **Cons:**
    * As the application grows, domain boundaries become blurred. Nothing prevents cross-domain pollution (e.g., a search controller directly calling a billing repository).
    * **Coarse-Grained Scaling & Scattered Bottlenecks:** The entire application must be scaled as a single unit. If a performance bottleneck occurs in a single domain (e.g., `vehiclesearch`), we are forced to scale the whole application-wasting resources on lightweight domains that are scattered across technical layers and impossible to isolate.
    * Extracting features later requires untangling heavily coupled code, demanding high refactoring effort.

---

### Option B: Modular Monolith (Package-by-Feature)
A single deployable application and database, but strictly structured by business domain (`identity`, `vehiclesearch`, `search-history`, etc.) at the root level. Each module internally manages its own layers.

* **Pros:**
    * **Clear Domain Boundaries:** High cohesion; each business domain is isolated within its own package.
    * **No Network Overhead:** All communication happens in-process. No network calls between internal modules means zero network latency, no distributed transaction issues, and a significantly simpler development and deployment workflow.
    * **Future-Proofing & Easy Extraction:** Because modules are neatly compartmentalized, extracting a high-load domain into an independent microservice later is straightforward. The code logic is already isolated, avoiding the nightmare of untangling a monolithic "ball of mud".
    * **Adaptive Scaling Based on Real Metrics:** We do not have to guess our bottlenecks in advance. As real traffic patterns emerge, we can extract only the specific modules that actually require independent scaling (e.g., isolating `vehiclesearch` while keeping the rest of the application as a monolith).
* **Cons:**
    * The application scales as a single runtime process until specific high-load modules are extracted into separate services.
    * Requires team discipline to prevent modules from bypassing public service interfaces and directly querying another module's repository.

---

### Option C: Microservices Architecture from Day One
Splitting every domain into a separate deployable application with its own database, communicating via network protocols.

* **Pros:** Independent deployments and isolated scaling per service.
* **Cons:**
    * **Network Unreliability & Complexity:** Moving from in-process calls to network calls introduces inherent network instability, latency, and failure modes. Handling service discovery, circuit breakers, retries, distributed tracing, and distributed transactions adds massive operational burden.
    * **Premature Complexity:** For an MVP serving a small user base, introducing this infrastructure tax distracts from delivering core business features and clean code.

---

## Decision
We choose **Option B: Modular Monolith (Package-by-Feature)**.

,,Premature optimization is the root of all evil." Introducing microservices on day one would solve problems we do not yet have, while introducing significant network and operational complexity. At our current scale, a microservice architecture is unjustified.

However, we also recognize that our system will grow, and we cannot predict with certainty which parts of the system will experience the highest load or become bottlenecks over time.

The **Modular Monolith** gives us the best of both worlds:
1. **Immediate Simplicity:** Fast iteration, easy testing, zero network friction, and straightforward deployment.
2. **Architectural Flexibility:** By enforcing clear domain boundaries today, we retain full freedom to evolve the architecture tomorrow. If real-world monitoring shows that a specific domain (such as `vehiclesearch`) is under heavy load, we can cleanly extract *just that domain* into a standalone microservice without refactoring the rest of the application. We are not locked into an all-or-nothing microservice topology; we can scale incrementally as actual system demand dictates.