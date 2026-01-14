# Database & JPA/Hibernate - Complete Guide

## Overview
A practical guide to JPA/Hibernate for interviews and production: entity modeling, mappings, repositories, queries, transactions, fetching strategies, performance, caching, locking, and common pitfalls. Includes comprehensive common Q&A.

---

## 1) JPA Basics & Entities

### Summary
- JPA is a specification; Hibernate is a popular implementation.
- Entities map Java objects to database tables.
- Each entity requires an identifier (@Id).

### Examples
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;
}
```

---

## 2) Primary Keys & Generation Strategies

### Summary
- Strategies: IDENTITY (DB auto-increment), SEQUENCE (DB sequence), TABLE, AUTO.
- Choose based on DB and batching needs.

### Examples
```java
@Id
@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
@SequenceGenerator(name = "user_seq", sequenceName = "user_seq", allocationSize = 50)
private Long id;
```

---

## 3) Relationships (Associations)

### Summary
- One-to-One, One-to-Many, Many-to-One, Many-to-Many.
- Manage owning side carefully; use mappedBy on inverse side.
- Prefer Set over List for unordered collections to avoid duplicates.

### Examples
```java
// Many-to-One (owning side)
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id")
private User user;

// One-to-Many (inverse side)
@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Order> orders = new ArrayList<>();

// Many-to-Many with join table
@ManyToMany
@JoinTable(name = "user_roles",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "role_id"))
private Set<Role> roles = new HashSet<>();
```

### Owning vs Inverse Side
- The owning side defines the foreign key (@JoinColumn).
- mappedBy indicates inverse side.

---

## 4) Fetch Types & N+1 Problem

### Summary
- LAZY: load on access; EAGER: load immediately.
- Default: ManyToOne/OneToOne = EAGER; OneToMany/ManyToMany = LAZY (Hibernate defaults may vary; prefer lazy).
- N+1 happens when fetching parent list then lazy-loading each child.

### Solutions
- Use JOIN FETCH in JPQL.
- Use EntityGraph.
- Batch fetching and DTO projections.

### Examples
```java
// JOIN FETCH to avoid N+1
@Query("select u from User u join fetch u.orders where u.id = :id")
User findWithOrders(@Param("id") Long id);

// EntityGraph
@EntityGraph(attributePaths = {"orders", "roles"})
Optional<User> findByEmail(String email);
```

---

## 5) Repositories (Spring Data JPA)

### Summary
- JpaRepository provides CRUD and pagination.
- Derived queries by method name; @Query for custom JPQL/native.
- Projections for DTOs.

### Examples
```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("select new com.example.dto.UserSummary(u.id, u.email) from User u")
    List<UserSummary> findSummaries();
}
```

---

## 6) Transactions (@Transactional)

### Summary
- Atomic units of work; ensure consistency.
- Default rollback on RuntimeException; configure otherwise via rollbackFor.
- Place @Transactional on service layer.

### Examples
```java
@Service
public class UserService {
    private final UserRepository repo;
    public UserService(UserRepository repo) { this.repo = repo; }

    @Transactional
    public User create(User u) { return repo.save(u); }

    @Transactional(readOnly = true)
    public Optional<User> find(Long id) { return repo.findById(id); }
}
```

---

## 7) Query Languages (JPQL, Criteria, Native)

### Summary
- JPQL: object-oriented query language.
- Criteria API: type-safe dynamic queries.
- Native: raw SQL for complex/optimized queries.

### Examples
```java
@Query("select o from Order o where o.status = :status")
List<Order> findByStatus(@Param("status") Status status);

// Native example
@Query(value = "select * from orders where total > :min", nativeQuery = true)
List<Order> findExpensive(@Param("min") BigDecimal min);
```

---

## 8) Cascading & Orphan Removal

### Summary
- CascadeType: PERSIST, MERGE, REMOVE, REFRESH, DETACH, ALL.
- OrphanRemoval removes children when no longer referenced.

### Examples
```java
@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Order> orders;
```

---

## 9) Lifecycle Callbacks & Auditing

### Summary
- @PrePersist, @PostPersist, @PreUpdate, @PostLoad for hooks.
- Spring Data Auditing: @CreatedDate, @LastModifiedDate.

### Examples
```java
@EntityListeners(AuditingEntityListener.class)
public class Order {
    @CreatedDate private Instant createdAt;
    @LastModifiedDate private Instant updatedAt;

    @PrePersist void onCreate() { /* init */ }
}
```

---

## 10) Performance Tuning

### Summary
- Avoid EAGER fetching; prefer LAZY + fetch joins.
- Batch inserts/updates; tune allocationSize for sequences.
- Use projections; minimize select N+1; paginate results.

### Tips
- Enable SQL logging only in dev; use Hibernate Statistics.
- Use indexes at DB level; analyze execution plans.

---

## 11) Caching (1st & 2nd Level)

### Summary
- First-level cache (EntityManager) per transaction; automatic.
- Second-level cache: shared across sessions; requires provider (EHCache, Caffeine).

### Examples
```properties
spring.jpa.properties.hibernate.cache.use_second_level_cache=true
spring.jpa.properties.hibernate.cache.region.factory_class=org.hibernate.cache.jcache.JCacheRegionFactory
```

---

## 12) Locking (Optimistic & Pessimistic)

### Summary
- Optimistic: version field; detects conflicts at commit; scales better.
- Pessimistic: database locks; prevents concurrent updates; risk of deadlocks.

### Examples
```java
// Optimistic
@Version
private Long version;

// Pessimistic
@Lock(LockModeType.PESSIMISTIC_WRITE)
Optional<User> findById(Long id);
```

---

## 13) DTOs & Projections

### Summary
- Avoid exposing entities directly over REST.
- Use projections (interface or class) for read ops.

### Examples
```java
public record UserSummary(Long id, String email) {}

@Query("select new com.example.dto.UserSummary(u.id, u.email) from User u")
List<UserSummary> findSummaries();
```

---

## 14) Common Pitfalls

- EAGER fetching leading to performance issues.
- Bi-directional associations without proper equals/hashCode.
- OrphanRemoval misuse causing unintended deletes.
- N+1 queries due to lazy loading in loops.
- Transactions missing or too broad; read-only not used.
- Using EntityManager outside transaction context.

---

## Common Interview Questions & Answers

### Q1: What is the N+1 query problem? How to solve it?
- N+1 occurs when fetching N parent rows and then lazy-loading children one-by-one, causing N additional queries.
- Solutions: JOIN FETCH, EntityGraph, batch fetching, and DTO projections.

### Q2: Lazy vs eager loading?
- LAZY: load associations only when accessed; better performance and control.
- EAGER: load immediately; can cause heavy queries and unnecessary data load.

### Q3: Cascade types explained?
- Cascade controls how operations on parent propagate to children (PERSIST, MERGE, REMOVE, etc.). Use selectively; avoid CascadeType.ALL on ManyToOne.

### Q4: What is the difference between save() and saveAndFlush()?
- save(): schedules insert/update; flush occurs later as needed.
- saveAndFlush(): immediately flushes changes to DB; useful in tests or when DB-generated values needed.

### Q5: Transaction isolation levels?
- READ_UNCOMMITTED, READ_COMMITTED, REPEATABLE_READ, SERIALIZABLE. Control visibility of uncommitted changes and consistency guarantees.
- Default depends on DB; Spring allows setting via @Transactional(isolation = Isolation.READ_COMMITTED).

### Q6: Optimistic vs pessimistic locking?
- Optimistic: version field, detects concurrent modifications on commit; fewer locks, better scalability.
- Pessimistic: explicit DB locks to prevent concurrent updates; stronger guarantees but risk deadlocks.

### Q7: What is the difference between JPQL and SQL?
- JPQL queries over entities and their fields; SQL over tables/columns. JPQL is portable across databases.

### Q8: How do you avoid exposing entities in APIs?
- Use DTOs and mappers; leverage projections for read-only views; keep entities internal to persistence layer.

### Q9: When to use native queries?
- Complex operations not expressible in JPQL; performance-tuned queries; vendor-specific features. Keep them isolated and tested.

### Q10: How to handle pagination?
- Use Pageable in Spring Data: findAll(Pageable). Combine with sorting and projections; avoid fetching huge result sets.

---

## Quick Reference
- Prefer LAZY fetching; use JOIN FETCH or EntityGraph to optimize.
- Place @Transactional in service layer; use readOnly where applicable.
- Choose key generation strategy aligned with DB; tune sequences.
- Use DTOs/projections; avoid exposing entities.
- Monitor N+1; log SQL cautiously; profile and index.
- Understand locking strategies; default to optimistic.

---

*Last Updated: January 2026*

