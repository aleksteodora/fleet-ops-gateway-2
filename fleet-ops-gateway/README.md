# FleetOps Gateway

FleetOps Gateway is a backend service responsible for communication between the FleetOps Gateway application and vehicle data provider systems.

The FleetOps Gateway hides provider-specific communication details and provides a consistent integration layer for accessing vehicle information.

It provides the REST API for vehicle searches and manages users, companies, search history and statistics.

## Responsibilities

The FleetOps Gateway is expected to handle:

- Communication with the FleetOps Provider service
- Free Provider integration
- Premium Provider integration
- Provider response mapping
- External service error handling
- Timeout handling
- Provider-specific configuration
- Resilience mechasnisms where appropriate

The exact responsibilities and architecture will be defined during the project together with the students.

## Provider Flow

```text
FleetOps Application
       │
       ▼
FleetOps Gateway
       │
       ▼
FleetOps Provider
   ┌───────┴───────┐
   ▼               ▼
Free API       Premium API
```

The exact communication contracts and fallback responsibilities are **TBD**.

## Tech Stack

- Java 21
- Spring Boot 4.1
- Spring MVC
- Spring Security
- PostgreSQL
- Spring Data JPA / Hibernate
- OpenAPI / Swagger
- Maven
- JUnit 5
- Mockito

## Prerequisites

- Java 21
- Maven (or Maven Wrapper)
- PostgreSQL
- Git

## Configuration

Set the following environment variables for local development:

```text
SPRING_PROFILES_ACTIVE=dev
DB_URL=jdbc:postgresql://localhost:5432/fleetops
DB_USERNAME=fleetops
DB_PASSWORD=fleetops
```

## Running the Application

Windows:

```bash
./mvnw.cmd spring-boot:run
```

Linux / macOS:

```bash
./mvnw spring-boot:run
```

The application runs on:

```text
http://localhost:8080
```

## Swagger

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI specification:

```text
http://localhost:8080/v3/api-docs
```

## Database

PostgreSQL is used as the primary database.

Hibernate is configured to validate the schema rather than modify it.

## Testing

Run tests with:

```bash
./mvnw test
```

## Security

The planned security model uses JWT authentication with two roles:

- `ADMIN`
- `COMPANY_USER`

During initial development, API endpoints are currently accessible without authentication. Security rules will be introduced as the authentication and authorization implementation is developed.

## Documentation

Project-wide documentation is maintained in the `fleetops-docs` repository, including:

- API documentation
- Architecture
- Database design
- Git workflow
- Development guidelines
- Team agreement
- ADRs
- Project specifications
