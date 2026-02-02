# REST API Best Practices - Complete Guide

## Overview
A practical, interview-ready guide for designing and building robust REST APIs. Covers resources, HTTP methods, status codes, pagination, versioning, error handling, validation, security, performance, documentation, idempotency, and common pitfalls—plus answers to the most frequent interview questions.

---

## 1) Resources & Endpoints

### Summary
- Use nouns, plural resources; avoid verbs.
- Hierarchical relationships via sub-resources.
- Keep URIs simple, consistent, and lowercase; use hyphens for readability.

### Examples
- /api/v1/users
- /api/v1/users/{id}
- /api/v1/users/{id}/orders
- /api/v1/products/{id}/reviews

### Guidelines
- Prefer meaningful names (orders, customers, invoices).
- Avoid exposing internal DB structure directly.
- Use query params for filtering, sorting, pagination.

---

## 2) HTTP Methods

### Summary
- GET: Retrieve resources; safe and idempotent.
- POST: Create new resources; not idempotent.
- PUT: Replace entire resource; idempotent.
- PATCH: Partial update; idempotent if designed carefully.
- DELETE: Remove resource; idempotent.

### Examples
- GET /users ? list
- GET /users/{id} ? single
- POST /users ? create
- PUT /users/{id} ? replace
- PATCH /users/{id} ? partial update
- DELETE /users/{id} ? delete

---

## 3) Status Codes

### Summary
- 200 OK: Successful GET/PUT/PATCH.
- 201 Created: Resource created; include Location header.
- 202 Accepted: Async processing started.
- 204 No Content: Successful DELETE or update without body.
- 304 Not Modified: ETag/If-None-Match caching.
- 400 Bad Request: Validation errors.
- 401 Unauthorized: Missing/invalid auth.
- 403 Forbidden: Authenticated but not allowed.
- 404 Not Found: Resource not found.
- 409 Conflict: Version conflict or state violation.
- 412 Precondition Failed: ETag mismatch.
- 422 Unprocessable Entity: Semantic validation failure.
- 429 Too Many Requests: Rate limiting.
- 500 Internal Server Error: Unexpected error.
- 503 Service Unavailable: Temporary outage.

### Example Response
```
HTTP/1.1 201 Created
Location: /api/v1/users/123
Content-Type: application/json

{"id":123,"email":"john@example.com"}
```

---

## 4) Request & Response Design

### Summary
- Use JSON (application/json) with snake_case or camelCase consistently.
- Include Content-Type and Accept headers.
- Support ETag/If-None-Match for caching.
- Prefer consistent envelope shape when needed.

### Examples
- Request body: {"email":"john@example.com","name":"John"}
- Response body: {"id":123,"email":"john@example.com","name":"John"}
- Error payload: {"code":"VALIDATION_ERROR","message":"Email invalid","details":{"email":"must be a valid email"}}

---

## 5) Pagination, Sorting, Filtering

### Summary
- Use query params for paging: page, size; for sorting: sort; for filtering: field-specific params.
- Return metadata: total elements, total pages, page size, page index, links.

### Example
- GET /users?page=2&size=20&sort=createdAt,desc&status=ACTIVE

### Response Structure
```json
{
  "content": [ /* items */ ],
  "page": 2,
  "size": 20,
  "totalElements": 132,
  "totalPages": 7,
  "links": {
    "self": "/users?page=2&size=20",
    "next": "/users?page=3&size=20",
    "prev": "/users?page=1&size=20"
  }
}
```

---

## 6) Versioning

### Summary
- Path versioning is most common: /api/v1.
- Alternative: header-based (Accept: application/vnd.company.resource+json;version=1).
- Version only when breaking changes; prefer additive changes.

### Examples
- /api/v1/users vs /api/v2/users
- Accept: application/vnd.acme.user+json;version=2

---

## 7) Error Handling & Validation

### Summary
- Use structured error responses with an error code, human-readable message, and details.
- Map validation errors to 400/422.
- Don’t leak internal stack traces.

### Example Error
```json
{
  "code": "VALIDATION_ERROR",
  "message": "Email is invalid",
  "details": { "email": "must be a valid email" }
}
```

### Validation
- Use schema/DTO validation (Bean Validation in Spring): @NotBlank, @Email, etc.
- Validate query params and headers.

---

## 8) Idempotency

### Summary
- GET, PUT, DELETE are idempotent by definition.
- Make POST idempotent for critical operations via idempotency keys.

### Example
- Header: Idempotency-Key: 7b9f-...
- Server stores/locks by key to avoid duplicate side effects.

---

## 9) Security

### Summary
- Use HTTPS everywhere; HSTS at edge.
- Authentication: OAuth2/JWT for stateless APIs.
- Authorization: role/permission checks at endpoints.
- Input validation & output encoding; avoid injection.
- CORS configured explicitly, least-privilege.
- Rate limiting & throttling.

### Examples
- Bearer token: Authorization: Bearer <jwt>
- Roles: hasRole('ADMIN') for admin endpoints.

---

## 10) Performance & Caching

### Summary
- Use ETags/If-None-Match, Cache-Control, Expires.
- Paginate large responses; compress (gzip/brotli).
- Avoid N+1 with efficient data access; use projections.
- Prefer asynchronous processing for long tasks (202 Accepted + polling or callbacks).

### Examples
- Response headers: Cache-Control: max-age=60, public
- ETag: "a1b2c3"; If-None-Match: "a1b2c3"

---

## 11) HATEOAS (Optional)

### Summary
- Include links in responses to guide clients through the API.
- Useful for discoverability and evolvability; not mandatory.

### Example
```json
{
  "id": 123,
  "email": "john@example.com",
  "links": {
    "self": "/api/v1/users/123",
    "orders": "/api/v1/users/123/orders"
  }
}
```

---

## 12) Documentation & Discoverability

### Summary
- Use OpenAPI/Swagger for interactive docs.
- Keep docs versioned and up-to-date.
- Provide examples for all endpoints.

### Examples
- OpenAPI annotations in Spring (springdoc-openapi).
- Host Swagger UI in /swagger-ui.html or /api/docs.

---

## 13) Consistency & Naming

### Summary
- Consistent casing, naming, and response shapes.
- Standardize error codes and pagination responses.
- Prefer UTC timestamps in ISO-8601 (e.g., 2026-01-10T12:34:56Z).

---

## 14) Testing & Monitoring

### Summary
- Contract tests (e.g., Spring Cloud Contract) for provider/consumer.
- Integration and e2e tests for critical flows.
- Monitor endpoints with metrics, tracing (OpenTelemetry), and logs.

### Examples
- Health check at /actuator/health.
- Correlation IDs for request tracing.

---

## 15) Common Pitfalls

- Overloading endpoints with multiple responsibilities.
- Leaking internal errors or stack traces.
- Inconsistent response shapes and status code misuse.
- Missing pagination causing large payloads.
- Not validating inputs; weak security.
- Breaking backward compatibility without versioning.

---

## Common Interview Questions & Answers

### Q1: What makes a good REST API?
- Clear resource modeling, proper use of HTTP methods and status codes, robust error handling, pagination, security, documentation, and consistency.

### Q2: How do you handle API versioning?
- Path versioning (/api/v1) or header-based; version only for breaking changes; prefer additive changes; deprecate with timelines.

### Q3: Explain idempotency in REST
- Same operation repeated yields the same server state; GET/PUT/DELETE are idempotent; POST can be made idempotent using idempotency keys.

### Q4: How do you secure REST APIs?
- HTTPS, OAuth2/JWT, proper authorization, input validation, rate limiting, and secure headers (CORS, CSP).

### Q5: What status code should you return for validation errors?
- 400 Bad Request or 422 Unprocessable Entity; include structured error payload.

### Q6: How to implement pagination?
- page, size params; include metadata (totalElements, totalPages); provide navigation links; enforce limits to prevent abuse.

### Q7: When to use PUT vs PATCH?
- PUT replaces the entire resource; PATCH applies a partial update; both should be idempotent.

### Q8: What is HATEOAS and should we use it?
- Hypermedia links guide clients; beneficial for discoverability but optional; weigh complexity vs benefits.

### Q9: How to cache GET responses?
- Use ETag/If-None-Match, Cache-Control, Expires; validate freshness; avoid caching sensitive data.

### Q10: How do you document REST APIs?
- OpenAPI/Swagger definitions, examples, and live docs; keep synchronized with code; expose via Swagger UI.

---

## Quick Reference
- Nouns, plural resources; sub-resources for hierarchy.
- Methods: GET/POST/PUT/PATCH/DELETE.
- Status codes: 2xx success, 4xx client errors, 5xx server errors.
- Pagination: page/size/sort; include metadata.
- Versioning: /api/v1; break only when necessary.
- Errors: structured payloads with code/message/details.
- Security: HTTPS + OAuth2/JWT + authorization + rate limiting.
- Performance: caching, compression, pagination, async.
- Docs: OpenAPI/Swagger.

---

*Last Updated: January 2026*

