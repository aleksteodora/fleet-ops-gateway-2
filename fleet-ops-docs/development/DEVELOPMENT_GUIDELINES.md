# Development Guidelines

This document defines the development guidelines and engineering practices used throughout the FleetOps Gateway project.

The goal is not only to implement working functionality, but to build software that is readable, maintainable, testable, and easy to extend.

These guidelines apply to all FleetOps Gateway components unless a component-specific guideline states otherwise.

## 1. General Principles

When implementing a feature, consider:

- Is the solution simple and easy to understand?
- Is each component responsible for one clear thing?
- Can the code be easily tested?
- Is the implementation easy to extend?
- Are business rules separated from technical/infrastructure concerns?
- Is the chosen approach justified for the current requirements?

Prefer simple solutions over unnecessary complexity.

Do not introduce a framework, library, design pattern, or abstraction without a clear reason.

## 2. Clean Code

Code should be:

- Readable
- Consistent
- Self-explanatory
- Focused
- Easy to maintain

### Naming

Use meaningful names that describe the purpose of a class, method, variable, or component.

Prefer:

```java
VehicleSearchService
findVehicleByVin()
premiumProviderClient
```

over:

```java
Service
process()
data
```

Avoid abbreviations unless they are well established in the project domain.

> **Code is read much more often than it is written.**

When writing code, do not think only about how quickly you can implement it. Think about the developer who will read, debug, modify, or extend it later — including yourself.

Readable code reduces the amount of time developers need to understand the system and makes future changes safer and easier.

Optimize for readability, not for writing as few lines of code as possible.

## 3. Single Responsibility

Classes, methods, and components should have a clear and focused responsibility.

Avoid classes that simultaneously:

- Handle HTTP requests
- Implement business logic
- Access the database
- Call external services
- Transform API responses

Separate these responsibilities where appropriate.

## 4. Separation of Concerns

Keep different responsibilities separated.

For example:

```text
API / Controller
       ↓
Business Logic
       ↓
Persistence / External Services
```

Controllers should primarily handle HTTP/API concerns rather than contain complex business logic.

External service communication should be isolated from the core business logic.

The exact project structure and architectural boundaries will be defined during the project.

## 5. SOLID Principles

Students should understand and apply SOLID principles where they provide value.

Particular attention should be paid to:

- Single Responsibility Principle
- Open/Closed Principle
- Liskov Substitution Principle
- Interface Segregation Principle
- Dependency Inversion Principle

SOLID should not be applied mechanically.

The goal is to improve maintainability and flexibility, not to create unnecessary abstractions.

## 6. Design Patterns

Use design patterns when they solve a real problem.

Examples that may be relevant to FleetOps Gateway include:

- Strategy
- Adapter
- Factory
- Template Method
- Repository
- Dependency Injection

Students should be able to explain why a pattern is being used, what problem it solves, what alternatives were considered, and whether the added complexity is justified.

## 7. API Design

REST APIs should be:

- Consistent
- Predictable
- Resource-oriented
- Properly validated
- Properly documented

Pay attention to:

- HTTP methods
- HTTP status codes
- Request and response DTOs
- Validation
- Error responses
- Pagination
- Filtering
- Naming conventions

Avoid exposing persistence entities directly as API responses unless there is a clear reason to do so.

## 8. Validation and Error Handling

Validate input at appropriate boundaries.

Examples include:

- Required fields
- VIN format
- Pagination parameters
- Invalid identifiers
- Unsupported values

Errors should be handled consistently.

API clients should receive meaningful HTTP status codes and structured error responses.

Avoid exposing internal implementation details or stack traces to API clients.

## 9. External Service Communication

FleetOps Gateway communicates with external/provider systems.

External calls should be treated as unreliable.

Consider:

- Timeouts
- Connection failures
- Invalid responses
- Service unavailability
- Slow responses
- Retries where appropriate
- Fallback behavior
- Circuit breakers where appropriate

Provider-specific implementation details should remain isolated from the core business logic.

## 10. Database and Persistence

Database access should be separated from business logic.

Consider:

- Appropriate entity design
- Database constraints
- Indexes
- Transactions
- Query performance
- Pagination
- Migration management

Database changes should be versioned using the project's migration mechanism.

Avoid loading more data than necessary.

## 11. Testing

Automated tests are an integral part of development.

Tests should provide confidence that the application behaves correctly.

Consider different levels of testing:

```text
Unit Tests
    ↓
Integration Tests
    ↓
API / Component Tests
```

Tests should cover:

- Business rules
- Happy paths
- Validation
- Error scenarios
- Edge cases
- External service failures
- Fallback behavior

Tests should be deterministic and independent from each other.

> **Tests are documentation.**

A good test does more than verify that the code works. It also describes how the system is expected to behave.

Tests should make it easy for another developer to understand:

- What the component is supposed to do.
- What inputs are valid.
- What happens in edge cases.
- How errors are handled.
- What behavior must not be changed accidentally.

Prefer clear test names and test scenarios that describe the expected behavior.

For example:

```java
shouldFallbackToPremiumProviderWhenFreeProviderTimesOut()
```

## 12. Testability

Code should be designed so that important behavior can be tested easily.

Avoid unnecessary static dependencies, hidden global state, and tightly coupled components.

Dependencies should be injected where appropriate.

A useful question when designing a component is:

> How would I test this component in isolation?

## 13. Logging

Logs should help developers understand what happened in the system.

Log important events such as:

- External service failures
- Unexpected application errors
- Important business events
- Relevant state transitions

Avoid logging:

- Passwords
- Tokens
- API keys
- Sensitive personal information
- Full request/response payloads when they may contain sensitive data

Use appropriate log levels.

## 14. Configuration

Environment-specific configuration should not be hardcoded.

Use configuration files and environment variables where appropriate.

Never commit:

- Passwords
- API keys
- JWT secrets
- Credentials
- Other sensitive information

## 15. Performance

Performance should be considered, but premature optimization should be avoided.

Pay particular attention to:

- Database queries
- N+1 queries
- External service calls
- Unnecessary network requests
- Large result sets
- Repeated expensive operations

Measure and understand the bottleneck before introducing complex optimizations.

## 16. Scalability

The FleetOps Gateway project includes scenarios where the number of requests and external service calls can increase significantly.

Topics that may be explored include:

- Horizontal scaling
- Caching
- Rate limiting
- Connection pooling
- Asynchronous processing
- Virtual Threads
- Reactive Programming
- Circuit Breakers
- Bulkheads
- Load balancing

The goal is to understand the trade-offs rather than automatically apply every available technique.

## 17. Code Duplication

Avoid unnecessary duplication.

If the same logic appears in multiple places, consider whether it should be extracted.

However, do not create abstractions prematurely.

Prefer duplication over an abstraction that is difficult to understand or likely to change.

## 18. Comments and Documentation

Code should primarily explain itself through good naming and structure.

Comments should explain:

- Why something is done
- Important business decisions
- Non-obvious technical constraints
- Workarounds

Avoid comments that simply restate what the code does.

## 19. Security

> **Note:** Security is not a primary focus of the internship. Due to the limited time, we may not have the opportunity to cover security topics in depth. Nevertheless, students should be aware of basic security considerations when designing and implementing features, and selected topics may be discussed if time allows.

Basic security considerations include:

- Authentication
- Authorization
- Input validation
- Access control
- Sensitive data
- Secrets
- API exposure
- Error messages

Users should only be able to access resources they are authorized to access.

## 20. Code Review

Code review is an important part of the development process.

When reviewing code, consider:

- Correctness
- Readability
- Maintainability
- Architecture
- Testing
- Security
- Performance
- Error handling

Review comments should focus on improving the solution rather than personal preferences.

## 21. Definition of Done

A task should generally be considered complete when:

- The requirements are implemented.
- Appropriate tests are added or updated.
- All tests pass.
- Validation and error handling are implemented.
- The code follows project conventions.
- No unnecessary debugging code remains.
- Documentation is updated where necessary.
- The Merge Request has been reviewed and approved.
- CI pipeline passes successfully.

## 22. Engineering Mindset

The goal of the FleetOps Gateway project is not simply to make the application work.

Students should learn to ask:

> Does this solution work?

and then:

> Is this the simplest appropriate solution?

and finally:

> How will this solution behave when the system grows or requirements change?

Technical decisions should be discussed, justified, and documented when appropriate.
