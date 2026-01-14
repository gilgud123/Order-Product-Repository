# Testing - Complete Guide

## Overview
A practical guide to testing in Java and Spring Boot: unit, integration, and end-to-end testing; mocking; test organization; Spring testing annotations and tools; test data strategies; and common interview questions.

---

## 1) Testing Pyramid & Types

### Summary
- Unit tests: fast, isolated tests of small pieces (methods/classes).
- Integration tests: verify interactions between components (DB, web layer).
- End-to-end (system) tests: test full application workflow.
- Aim for more unit tests than integration; keep e2e minimal and critical.

---

## 2) Unit Testing (JUnit 5)

### Summary
- Use JUnit 5 (Jupiter) for modern testing.
- Keep tests deterministic, small, and independent.
- Structure: Arrange-Act-Assert (Given-When-Then).

### Examples
```java
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class MathUtilsTest {
    @Test
    void add_shouldReturnSum() {
        // Arrange
        int a = 2, b = 3;
        // Act
        int sum = a + b;
        // Assert
        assertEquals(5, sum);
    }
}
```

---

## 3) Mocking & Stubbing (Mockito)

### Summary
- Use Mockito to isolate unit tests by mocking dependencies.
- Mock behavior with when(...).thenReturn(...).
- Verify interactions with verify(...).

### Examples
```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock UserRepository repo;
    @InjectMocks UserService service;

    @Test
    void create_shouldSaveUser() {
        User input = new User("john@example.com");
        when(repo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User saved = service.create(input);

        assertEquals("john@example.com", saved.getEmail());
        verify(repo, times(1)).save(any(User.class));
    }
}
```

### Tips
- Avoid over-mocking; prefer real objects where simple.
- Mock external dependencies (DB, HTTP clients), not your domain logic.

---

## 4) Parameterized & Nested Tests

### Summary
- Parameterized tests for running the same test with multiple inputs.
- Nested tests for grouping related scenarios.

### Examples
```java
@ParameterizedTest
@CsvSource({"2,3,5", "-1,1,0"})
void add_params(int a, int b, int expected) {
    assertEquals(expected, a + b);
}

@Nested
class AuthenticationTests {
    @Test void validCredentials() { /* ... */ }
    @Test void invalidCredentials() { /* ... */ }
}
```

---

## 5) Integration Testing (Spring Boot)

### Summary
- Use @SpringBootTest to load full context; slower but comprehensive.
- For web layer only, use @WebMvcTest.
- Use TestRestTemplate or MockMvc for REST API tests.

### Examples
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserApiIT {
    @Autowired TestRestTemplate rest;

    @Test
    void getUser_shouldReturn200() {
        ResponseEntity<UserDTO> resp = rest.getForEntity("/api/users/1", UserDTO.class);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }
}

@WebMvcTest(UserController.class)
class UserControllerSliceTest {
    @Autowired MockMvc mvc;
    @MockBean UserService service;

    @Test
    void getUser_returnsUser() throws Exception {
        when(service.findDto(1L)).thenReturn(new UserDTO("john@example.com"));
        mvc.perform(get("/api/users/1"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.email").value("john@example.com"));
    }
}
```

---

## 6) Data Layer Testing (JPA)

### Summary
- Use @DataJpaTest for repository tests with in-memory DB.
- TestEntityManager provides helpers for persisting/finding entities.

### Examples
```java
@DataJpaTest
class UserRepositoryTest {
    @Autowired UserRepository repo;

    @Test
    void findByEmail_shouldReturnUser() {
        User u = new User(); u.setEmail("john@example.com");
        repo.save(u);
        assertTrue(repo.findByEmail("john@example.com").isPresent());
    }
}
```

---

## 7) Test Data Strategies

### Summary
- Builders/test factories for consistent data creation.
- Use in-memory database (H2) for data tests; load schema via schema.sql.
- Testcontainers for real DBs in integration tests.

### Examples
```java
class UserFactory {
    static User user(String email) {
        User u = new User();
        u.setEmail(email);
        u.setName("John");
        return u;
    }
}
```

---

## 8) Assertions & Matchers

### Summary
- JUnit assertions: assertEquals, assertTrue, assertThrows.
- AssertJ/Hamcrest for fluent matchers and readability.

### Examples
```java
import static org.assertj.core.api.Assertions.*;

@Test
void user_hasEmail() {
    User u = new User(); u.setEmail("a@b.com");
    assertThat(u.getEmail()).isNotBlank().contains("@");
}

@Test
void throws_whenInvalid() {
    assertThrows(IllegalArgumentException.class, () -> validateAge(-1));
}
```

---

## 9) Testing REST APIs

### Summary
- MockMvc for controller layer tests without starting server.
- TestRestTemplate/WebTestClient for starting the app and testing endpoints.
- Validate status, headers, and JSON payloads.

### Examples
```java
mvc.perform(post("/api/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"email\":\"a@b.com\"}"))
   .andExpect(status().isCreated())
   .andExpect(header().exists("Location"))
   .andExpect(jsonPath("$.email").value("a@b.com"));
```

---

## 10) Coverage & CI

### Summary
- Use code coverage tools (JaCoCo) to gauge test completeness.
- Integrate tests into CI pipelines (GitHub Actions, Jenkins).

### Examples (Maven)
```xml
<plugin>
  <groupId>org.jacoco</groupId>
  <artifactId>jacoco-maven-plugin</artifactId>
  <version>0.8.11</version>
  <executions>
    <execution>
      <goals><goal>prepare-agent</goal></goals>
    </execution>
    <execution>
      <id>report</id>
      <phase>test</phase>
      <goals><goal>report</goal></goals>
    </execution>
  </executions>
</plugin>
```

---

## 11) Best Practices
- Write tests first for tricky logic; keep them fast and deterministic.
- Isolate unit tests with mocks; integration tests use real components.
- Name tests clearly: method_shouldExpect_case.
- Avoid brittle tests relying on timing or external systems.
- Keep test data minimal and relevant; use builders/factories.
- Assert one main behavior per test; avoid logic in tests.

---

## Common Interview Questions & Answers

### Q1: Difference between unit and integration tests?
- Unit tests isolate a small unit with mocked dependencies.
- Integration tests verify that parts work together (e.g., repository + DB, controller + service).

### Q2: When should you mock? What shouldn’t you mock?
- Mock external dependencies (DB, HTTP clients, message brokers).
- Don’t mock value objects or simple domain logic; use real instances.

### Q3: How do you test REST APIs in Spring?
- Controller slice: @WebMvcTest + MockMvc.
- Full context: @SpringBootTest + TestRestTemplate/WebTestClient.
- Validate status, headers, body with JSON matchers.

### Q4: What is @SpringBootTest vs @WebMvcTest?
- @SpringBootTest: loads full app; slower; good for integration.
- @WebMvcTest: loads only web slice (controllers, MVC); faster; mocks service layer.

### Q5: How to handle test data?
- Use builders/factories; in-memory DB for JPA; Testcontainers for realistic DB tests.
- Reset state between tests; use @DirtiesContext sparingly.

### Q6: What is code coverage and how much is enough?
- Coverage measures executed code by tests; JaCoCo reports.
- Aim for meaningful coverage (e.g., >80%) but prioritize critical paths and behavior over chasing numbers.

### Q7: How do you test asynchronous code?
- Use Awaitility or CompletableFuture APIs (join/thenCompose); provide timeouts; avoid sleeps.

### Q8: How do you structure test packages?
- Mirror main source packages under src/test/java; keep clear naming and separation.

### Q9: What is @DataJpaTest used for?
- Tests repository layer with in-memory DB, scanning JPA components only; faster and focused.

### Q10: How to ensure tests are reliable in CI?
- Avoid external dependencies; use mocks/Testcontainers; deterministic data; proper timeouts; parallel-friendly tests.

---

## Quick Reference
- JUnit 5 + Mockito for unit tests.
- @WebMvcTest for controller slices; @SpringBootTest for full integration.
- @DataJpaTest for repositories.
- MockMvc vs TestRestTemplate/WebTestClient.
- JaCoCo for coverage; integrate with CI.

---

*Last Updated: January 2026*

