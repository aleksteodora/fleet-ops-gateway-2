# A Well-Designed API

A well-designed API is not only about making endpoints work. Good API design makes the system predictable, easy to use, reliable, secure, and easier to maintain.

The following principles should be considered when designing FleetOps APIs.

## 1. Idempotency

An operation is **idempotent** when making the same request multiple times has the same intended effect as making it once.

HTTP semantics define `GET`, `HEAD`, `PUT`, and `DELETE` as idempotent methods.

For example:

```http
PUT /api/vehicles/123
```

Sending the same request multiple times should leave the resource in the same state.

```http
DELETE /api/vehicles/123
```

Repeating the request should not cause additional side effects.

### What about POST and PATCH?

`POST` is generally **not idempotent**. Repeating the same request may create multiple resources or trigger the same operation multiple times.

`PATCH` is **not inherently idempotent**. Whether a PATCH operation is idempotent depends on how the operation is defined.

For operations where duplicate processing would be a problem, consider using an **idempotency key**.

### Idempotency Keys

The client generates a unique key and sends it with the request:

```http
POST /api/searches
Idempotency-Key: 7f4c8b2a-...
```

The server stores the key together with the result of the operation, for example in Redis or a database.

If the client retries the same request with the same key, the server can recognize that the operation has already been processed and return the original result instead of processing it again.

This is particularly useful for operations such as:

- Payments
- Orders
- Resource creation
- Other operations where duplicate processing must be avoided

## 2. Versioning

APIs evolve over time.

Changes that are not backward compatible should not unexpectedly break existing clients.

One common approach is URL-based versioning:

```http
/api/v1/vehicles
/api/v2/vehicles
```

Another approach is header-based versioning.

The important point is to have a clear versioning strategy and use it consistently.

Versioning should be introduced when there is a real need for incompatible API changes rather than simply creating a new version for every small change.

## 3. Noun-Based Resource Names

REST resource names should represent **resources**, not actions.

Prefer:

```http
GET /api/products
GET /api/products/123
```

instead of:

```http
GET /api/getProducts
GET /api/getProductById/123
```

Use HTTP methods to express the operation:

```text
GET     /api/products       → retrieve products
POST    /api/products       → create a product
GET     /api/products/123   → retrieve a specific product
PUT     /api/products/123   → replace a product
PATCH   /api/products/123   → partially update a product
DELETE  /api/products/123   → delete a product
```

Resource naming should be consistent throughout the API.

## 4. Security

API endpoints should be protected according to their requirements.

Consider:

- Authentication
- Authorization
- Input validation
- Access control
- Sensitive data
- Secrets
- HTTPS
- Token validation

When bearer tokens such as JWTs are used, the server must validate the token before allowing access to protected resources.

JWTs commonly contain a header, payload, and signature. The signature allows the server to verify that the token was issued by a trusted party and has not been modified.

Always use **HTTPS** for production API communication.

Authentication answers:

> Who are you?

Authorization answers:

> Are you allowed to perform this operation on this resource?

Both should be considered when designing protected endpoints.

## 5. Pagination

Avoid returning unnecessarily large collections in a single response.

For example:

```http
GET /api/vehicles?limit=20&offset=40
```

This allows the client to request a specific portion of the dataset.

Pagination helps with:

- Response size
- Network traffic
- Database load
- Response time
- Client-side processing

Pagination should be predictable and consistently implemented.

For large or frequently changing datasets, cursor-based pagination may be more appropriate than offset-based pagination:

```http
GET /api/vehicles?limit=20&cursor=eyJpZCI6...
```

The appropriate approach depends on the use case and data access patterns.

## 6. HTTP Status Codes

Use HTTP status codes consistently to communicate the result of an operation.

Examples:

```text
200 OK
201 Created
204 No Content
400 Bad Request
401 Unauthorized
403 Forbidden
404 Not Found
409 Conflict
422 Unprocessable Content
500 Internal Server Error
```

Do not use `200 OK` for every outcome simply because the HTTP request itself was successfully processed.

The status code should communicate the outcome to the client.

## 7. Consistent Error Responses

Errors should have a predictable structure.

For example:

```json
{
  "status": 400,
  "code": "INVALID_VIN",
  "message": "The provided VIN is invalid."
}
```

Clients should not have to understand different error formats for different endpoints.

Avoid exposing internal implementation details, stack traces, database errors, or sensitive information.

## 8. Request and Response Models

Avoid exposing internal persistence entities directly through the API.

Use request and response models (DTOs) where appropriate.

This provides a clear API contract and separates:

```text
API Model
    ↓
Business Model
    ↓
Persistence Model
```

This also allows internal implementation details to change without unnecessarily breaking the API contract.

## 9. Validation

Validate incoming requests at the API boundary.

Examples:

- Required fields
- VIN format
- String length
- Numeric ranges
- Pagination parameters
- Supported enum values

Return clear validation errors that allow clients to understand what needs to be corrected.

## 10. Consistency

An API should behave consistently across endpoints.

Keep consistent conventions for:

- Resource naming
- HTTP methods
- Status codes
- Error responses
- Pagination
- Filtering
- Sorting
- Date/time formats
- Field naming

Once a convention is established, follow it unless there is a good reason not to.

## 11. Design for Clients

An API is a contract between systems.

When designing an endpoint, consider the client consuming it:

- Is the endpoint intuitive?
- Is the request easy to construct?
- Is the response predictable?
- Are errors understandable?
- Can the API evolve without unnecessarily breaking clients?
- Does the API expose enough information without exposing unnecessary internal details?

A good API should be easy to use correctly and difficult to use incorrectly.

## 12. Keep It Simple

Do not introduce complexity without a reason.

A good API should provide the simplest interface that satisfies the requirements.

Before adding a new endpoint, parameter, abstraction, or version, ask:

> Do we actually need this?

Good API design is often about making the right trade-offs rather than following rules mechanically.
