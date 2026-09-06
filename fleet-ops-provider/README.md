# FleetOps Provider

FleetOps Provider is a backend service that simulates external vehicle data providers for the FleetOps Gateway project.

It exposes separate APIs representing Free and Premium vehicle data providers. This allows the team to control provider behavior and simulate real-world scenarios without relying on external third-party systems.

## Responsibilities

The Provider service is expected to:

- Expose a Free Provider API
- Expose a Premium Provider API
- Return vehicle information based on VIN
- Simulate active and inactive vehicles
- Simulate provider errors
- Simulate timeouts and unavailable services
- Provide predictable responses for testing

## Provider APIs

```text
Free Provider
Premium Provider
```

The exact API contracts are **TBD**.

High-level communication:

```text
FleetOps Gateway
       │
       ▼
FleetOps Provider
   ┌───────┴───────┐
   ▼               ▼
Free API       Premium API
```

The service should support scenarios such as:

- Successful Free Provider response
- Inactive vehicle returned by Free Provider
- Free Provider timeout
- Free Provider unavailable
- Successful Premium Provider response
- Inactive vehicle returned by Premium Provider
- Premium Provider timeout
- Premium Provider unavailable

## Tech Stack

- Java 21
- Spring Boot 4.1.0
- Maven
- REST
- OpenAPI / Swagger
- JUnit 5
- Mockito

## Getting Started

### Prerequisites

- JDK 21
- Maven

### Running the Application

**TBD**

### Configuration

**TBD**

No secrets should be committed to the repository.

## Testing

Automated tests should cover Free and Premium Provider behavior, active/inactive vehicle scenarios, errors, timeouts, and API validation.

## Project Structure

**TBD**

The final project structure will be defined together with the students.

## Documentation

Project-wide development and Git conventions are available in the `fleetops-docs` repository.
