# System Design & Architecture - Complete Guide

## Overview
A practical guide to system design and architecture for backend developers. Covers monoliths vs microservices, communication patterns, scalability, availability, partitioning, data storage, caching, consistency, messaging, observability, security, cloud-native patterns, and trade-offs. Includes common interview Q&A.

---

## 1) Architecture Styles

### Monolith
- Single deployable unit; simpler development/deployment.
- Pros: easier local dev, fewer distributed issues.
- Cons: scaling whole app, slower deployments, tight coupling.

### Microservices
- Independent services communicating via APIs/events.
- Pros: independent deployability, scaling per service, fault isolation, team autonomy.
- Cons: complexity (distributed systems), observability, data consistency, operational overhead.

### Modular Monolith
- Well-defined modules inside a single deployable; avoids premature microservices.

---

## 2) Communication Patterns

### Synchronous (Request/Response)
- REST/HTTP, gRPC.
- Simpler, but coupling via availability and latency.

### Asynchronous (Event/Messaging)
- Kafka/RabbitMQ; publish/subscribe, event sourcing.
- Decouples producers/consumers; improves resilience, supports backpressure.

### API Gateway
- Entry point for clients; routing, rate limiting, auth, aggregation.

---

## 3) Scalability & Performance

### Vertical vs Horizontal Scaling
- Vertical: increase resources of a single node.
- Horizontal: add more nodes; scale out.

### Load Balancing
- Distribute traffic across instances (round-robin, least connections).
- Use sticky sessions only if necessary; prefer stateless services.

### Caching
- Client-side (HTTP cache), CDN, server-side (in-memory), distributed (Redis).
- Patterns: cache-aside, write-through, write-behind.

### Performance Tips
- Use pagination, efficient queries, indexes.
- Avoid N+1; use batching and projections.
- Profile hot paths; use async processing for long tasks.

---

## 4) Availability & Reliability

### HA Strategies
- Redundancy, multi-zone/region deployments.
- Health checks and auto-restarts.

### Fault Tolerance
- Circuit Breakers (resilience4j), retries with backoff, bulkheads, timeouts.

### Graceful Degradation
- Serve cached/stale data; disable non-critical features.

---

## 5) Consistency Models

### CAP Theorem
- In the presence of a partition, choose Consistency or Availability.

### Consistency
- Strong: linearizable; reads see latest writes.
- Eventual: reads may see stale data; converges over time.

### Patterns
- Saga: distributed transactions via coordinated local transactions.
- Outbox: reliably publish events alongside DB changes.
- CQRS: separate read/write models for scalability and flexibility.

---

## 6) Data Storage & Modeling

### Relational (SQL)
- ACID transactions; normalized schemas; joins; strong consistency.

### NoSQL
- Key-Value (Redis), Document (MongoDB), Column (Cassandra), Graph (Neo4j).
- Choose based on access patterns and scalability needs.

### Partitioning & Sharding
- Split data across nodes; choose keys that avoid hotspots.

### Indexing & Query Optimization
- Create targeted indexes; analyze execution plans; avoid full scans.

---

## 7) Messaging & Event-Driven

### Brokers
- Kafka (high-throughput, durable logs), RabbitMQ (flexible routing), SQS.

### Patterns
- Publish/Subscribe, Event Sourcing, Stream Processing (Kafka Streams, Flink).

### Idempotency
- Use unique keys or deduplication to handle retries without duplicate effects.

---

## 8) Observability

### Logging
- Structured logs with correlation IDs; centralize (ELK, Loki).

### Metrics
- Prometheus/Grafana; track latency, throughput, error rates, saturation.

### Tracing
- Distributed tracing (OpenTelemetry, Jaeger, Zipkin); propagate trace IDs.

### Health & Readiness
- /health, /ready endpoints; integrate with orchestrators.

---

## 9) Security & Compliance

### Principles
- Least privilege; defense in depth; secure defaults.

### Practices
- HTTPS everywhere; OAuth2/JWT; secrets management; encryption at rest and in transit.
- Validate inputs; sanitize outputs; secure headers; audit logs.

### Compliance
- GDPR, PCI-DSS; data retention and access controls.

---

## 10) Cloud-Native & Deployment

### Containers & Orchestrators
- Docker containers; Kubernetes for orchestration (deployments, services, autoscaling).

### CI/CD
- Automate builds, tests, deployments; blue/green, canary releases.

### Infrastructure as Code
- Terraform, CloudFormation; versioned and repeatable infra.

### Service Mesh
- Istio/Linkerd: traffic management, mTLS, observability across services.

---

## 11) API Design & Contracts

### Versioning & Backward Compatibility
- Prefer additive changes; version only for breaking changes.

### Contracts
- OpenAPI/Swagger; consumer-driven contracts (Spring Cloud Contract).

### Idempotency & Rate Limiting
- Idempotency keys; 429 Too Many Requests and retry headers.

---

## 12) Reliability Engineering

### Chaos Engineering
- Inject failures to test resilience.

### SLOs/SLIs/SLAs
- Define objective metrics (availability, latency) and targets.

### Backpressure & Queues
- Use queues to buffer load; apply backpressure to avoid overload.

---

## 13) Patterns & Trade-offs

### Common Patterns
- Circuit Breaker, Bulkhead, Retry, Timeout, Cache Aside, Saga, Outbox, CQRS, Strangler Fig (migrate monolith), Sidecar.

### Trade-offs
- Consistency vs availability; latency vs throughput; flexibility vs simplicity; build vs buy.

---

## 14) Example Reference Architecture (E-Commerce)
- API Gateway ? Auth Service (JWT) ? Product Service ? Order Service ? Payment Service ? Inventory Service.
- Databases: Relational (orders/payments), NoSQL (catalog/cache), Redis (sessions/cache).
- Messaging: Kafka for order events; outbox pattern for reliable event publishing.
- Observability: Prometheus + Grafana; ELK; Jaeger for tracing.
- Deployment: Kubernetes with HPA; blue/green deploys; canary for sensitive services.

---

## Common Interview Questions & Answers

### Q1: Monolith vs Microservices—when to choose which?
- Start with a modular monolith for small teams and evolving requirements; move to microservices when independent scaling, faster deployments, and team autonomy justify the operational complexity.

### Q2: How do you ensure data consistency across microservices?
- Use Saga for distributed workflows, Outbox to reliably publish events, and idempotent consumers; avoid distributed transactions; accept eventual consistency where appropriate.

### Q3: How do you handle failures and improve resilience?
- Circuit breakers, retries with exponential backoff, timeouts, bulkheads, fallbacks; degrade gracefully by serving cached data or limiting features.

### Q4: How would you scale read-heavy endpoints?
- Add caching layers (CDN, Redis), read replicas, denormalized read models (CQRS), pagination, and efficient indexing.

### Q5: What’s the role of API Gateway?
- Central entry point that handles routing, authentication, rate limiting, aggregation, and cross-cutting concerns for external clients.

### Q6: How do you design idempotent operations?
- For POST critical operations, use idempotency keys; for PUT/DELETE, ensure repeated calls produce the same state; track operations with unique identifiers.

### Q7: How do you monitor and trace microservices?
- Centralized logging with correlation IDs, metrics per service (latency, error rates), distributed tracing across requests; set SLOs and alerts.

### Q8: Explain CAP and PACELC.
- CAP: under partition, trade consistency vs availability. PACELC: Else (no partition), trade latency vs consistency; during partition, availability vs consistency.

### Q9: How do you secure services?
- mTLS between services, OAuth2/JWT for clients, least privilege IAM, secrets management, input validation, and hardened configurations.

### Q10: How to migrate a monolith to microservices?
- Use Strangler Fig: carve out functionality behind a proxy, build new services, route traffic incrementally; ensure data decoupling and contract stability.

---

## Quick Reference
- Prefer modular monolith early; microservices for scale/autonomy.
- Async messaging improves resilience; use outbox and idempotency.
- Cache smartly; paginate; index; avoid N+1.
- Apply resilience patterns: circuit breaker, retries, timeouts.
- Observe: logs + metrics + traces + health.
- Secure: HTTPS, JWT/OAuth2, secrets, least privilege.
- Cloud-native: containers, Kubernetes, CI/CD, IaC.

---

*Last Updated: January 2026*

