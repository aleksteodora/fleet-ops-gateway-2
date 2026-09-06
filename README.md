# FleetOps Gateway

FleetOps Gateway is a B2B SaaS platform for searching and managing vehicle information based on VIN (Vehicle Identification Number).

The project is developed as an internship project and brings together backend and frontend development. The goal is to build a realistic business application while practicing clean code, project organization, API design, testing, Git workflow, code review, and other good software engineering practices.

## Features

The initial scope of the FleetOps platform includes:

- Vehicle search by VIN
- Integration with Free and Premium vehicle data providers
- Fallback from the Free Provider to the Premium Provider
- Timeout and error handling for external services
- Search history
- Search filtering and pagination
- Dashboard with basic search statistics
- Company-level data isolation
- FleetOps administrator statistics

Additional frontend features and UI details will be defined during the project.

## Project Components

The FleetOps project consists of the following main components:

- **FleetOps Backend** – REST API and business logic
- **FleetOps Frontend** – Web application used by FleetOps users
- **FleetOps Provider** – Service that simulates external Free and Premium vehicle data providers

The exact responsibilities and architecture of these components will be defined together with the students during the project.

## Architecture

**TBD**

The system architecture, service boundaries, communication patterns, and other architectural decisions will be discussed and defined during the project.

## Tech Stack

### Backend

- Java 21
- Spring Boot 4.1.0
- Spring Data JPA / Hibernate
- PostgreSQL
- Maven
- OpenAPI / Swagger
- JUnit 5
- Mockito

### Frontend

**TBD**

Frontend technologies, libraries, project structure, and other implementation details will be documented by the frontend mentors.

### Provider Service

- Java 21
- Spring Boot 3
- REST API

The Provider service simulates external vehicle data providers and exposes separate Free and Premium provider APIs.

### Infrastructure

**TBD**

Infrastructure, containerization, CI/CD, and deployment details will be defined during the project.

## Getting Started

Setup and local development instructions will be added as the project evolves.

### Prerequisites

**TBD**

The required tools and versions will be documented for each project component.

### Running the Project

**TBD**

Instructions for running the backend, frontend, provider service, and supporting infrastructure will be added during the project.

## Environment Variables

Environment-specific configuration should be provided through environment variables or local configuration files.

No secrets should be committed to the repository.

The exact environment variables will be documented as the implementation evolves.

## API

The backend will expose a REST API for vehicle search, search history, dashboard functionality, and other FleetOps features.

The API contract will be defined during implementation.

Interactive API documentation will be provided through OpenAPI / Swagger.

## Provider Integration

FleetOps uses a separate Provider service to simulate external vehicle data providers.

The Provider service exposes two provider APIs:

```text
Free Provider
Premium Provider
```

The expected business flow is:

```text
VIN Search
    │
    ▼
Free Provider
    │
    ├── Active vehicle ──────► Return result
    │
    └── Unavailable / inactive
              │
              ▼
       Premium Provider
              │
              ├── Success ───► Return result
              │
              └── Failure ───► Provider unavailable
```

The Provider service can simulate successful responses, inactive vehicles, timeouts, and service failures.

## Testing

Automated testing is an important part of the project.

The project should include appropriate tests for:

- Business logic
- REST controllers
- Persistence
- Provider integrations
- Fallback scenarios
- Error handling
- Frontend components and services

The exact testing strategy will be defined as part of the project.

## Development Guidelines

The project places particular emphasis on good engineering practices:

- Clean Code principles
- Clear and consistent naming
- Single Responsibility Principle
- Separation of concerns
- Appropriate project organization
- DTOs and API contracts
- Input validation
- Consistent error handling
- Automated testing
- Avoiding unnecessary duplication
- Appropriate use of design patterns
- Maintainable and readable code
- Meaningful Git commits
- Code review
- Documentation of important technical decisions

The goal is not only to make the application work, but also to understand why a particular solution is appropriate and how it can be maintained and extended.

## Project Structure

The final project structure will be defined during implementation.

The structure should provide a clear separation of responsibilities and remain easy to understand and maintain.

**TBD**

## Notes

- VIN stands for Vehicle Identification Number and is a unique identifier used to identify a vehicle.
- Free and Premium providers are simulated services created for the internship project.
- Provider failures are intentionally simulated so that students can implement and test fallback and error-handling scenarios.
- The project is a learning environment, so architectural and implementation decisions should be discussed and justified rather than simply copied from a predefined solution.

## Documentation

Additional project documentation, requirements, API contracts, and frontend-specific information will be added as the project evolves.
