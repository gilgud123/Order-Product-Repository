# Spring Framework (Boot) - Complete Guide

## Overview
A focused guide on Spring and Spring Boot essentials for technical interviews and day-to-day backend development: DI/IoC, configuration, beans, REST, data access, validation, profiles, actuator, testing, security basics, and common pitfalls. Includes a thorough common Q&A section.

---

## 1) Inversion of Control (IoC) & Dependency Injection (DI)

### Summary
- IoC flips control: container instantiates and manages dependencies.
- DI injects required collaborators into classes (constructor, field, setter).
- Promotes testability, loose coupling, and separation of concerns.

### Examples
```java
@Service
public class UserService {
    private final UserRepository repository;
    public UserService(UserRepository repository) { // Constructor injection (preferred)
        this.repository = repository;
    }
}

@Configuration
public class AppConfig {
    @Bean
    public UserService userService(UserRepository repo) { return new UserService(repo); }
}
```

### Best Practices
- Prefer constructor injection.
- Avoid field injection; makes testing/hard to mock.
- Keep beans stateless when possible.

---

## 2) Spring Boot Basics

### Summary
- Opinionated auto-configuration to get started quickly.
- Embedded servers (Tomcat/Jetty/Undertow).
- Starter dependencies (spring-boot-starter-xyz).
- Externalized configuration via application.properties/yml.

### Examples
```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

---

## 3) Configuration & Properties

### Summary
- Externalized configuration: application.yml/properties.
- Bind strongly-typed config using @ConfigurationProperties.
- Profiles for environment-specific overrides.

### Examples
```yaml
# application.yml
server:
  port: 8081
app:
  featureEnabled: true
  welcomeMessage: "Hello"

# application-prod.yml
app:
  featureEnabled: false
```
```java
@ConfigurationProperties(prefix = "app")
@Data
public class AppProps {
    private boolean featureEnabled;
    private String welcomeMessage;
}

@Configuration
@EnableConfigurationProperties(AppProps.class)
class PropsConfig {}
```

---

## 4) Bean Scopes & Lifecycle

### Summary
- Common scopes: singleton (default), prototype, request, session.
- Lifecycle hooks: @PostConstruct, @PreDestroy or InitializingBean/DisposableBean.

### Examples
```java
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
class PrototypeBean {}

@Component
class LifecycleBean {
    @PostConstruct void init() {}
    @PreDestroy void destroy() {}
}
```

---

## 5) REST Controllers

### Summary
- Build REST APIs with @RestController and request mapping annotations.
- Use ResponseEntity for status codes/headers.
- Validate requests with @Valid and DTOs.

### Examples
```java
@RestController
@RequestMapping("/api/users")
class UserController {
    private final UserService service;
    UserController(UserService service) { this.service = service; }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.findDto(id));
    }

    @PostMapping
    public ResponseEntity<UserDTO> create(@Valid @RequestBody UserDTO dto) {
        UserDTO created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
```

---

## 6) Data Access (Spring Data JPA)

### Summary
- Repository abstraction: JpaRepository, CrudRepository.
- Derived queries, @Query for JPQL/Native.
- Entity mappings and transactions (@Transactional).

### Examples
```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    @Query("select u from User u where u.age > :age")
    List<User> findOlderThan(@Param("age") int age);
}

@Service
@Transactional
class UserService {
    private final UserRepository repo;
    UserService(UserRepository repo) { this.repo = repo; }
    public UserDTO create(UserDTO dto) { /* map, repo.save, return */ return dto; }
}
```

---

## 7) Validation (Bean Validation)

### Summary
- Use Jakarta/JSR-380 annotations (@NotNull, @Email, @Size).
- Apply @Valid on controller method parameters.
- Handle validation errors with @ControllerAdvice.

### Examples
```java
@Data
class UserDTO {
    @NotBlank @Email
    private String email;
    @Size(min = 2, max = 50)
    private String name;
}

@RestControllerAdvice
class ValidationHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, String>> handle(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (a,b)->a));
        return ResponseEntity.badRequest().body(errors);
    }
}
```

---

## 8) Error Handling

### Summary
- Use @ControllerAdvice for centralized exception handling.
- Return consistent error structure.

### Examples
```java
@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<ErrorResponse> notFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> generic(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse("ERROR", "Unexpected error"));
    }
}
```

---

## 9) Actuator

### Summary
- Production-ready endpoints for health, info, metrics.
- Customize exposure via management.endpoints.web.exposure.include.

### Examples
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
```

---

## 10) Profiles

### Summary
- Separate configuration per environment (dev, test, prod).
- Activate via spring.profiles.active or @Profile.

### Examples
```yaml
spring:
  profiles:
    active: dev
```
```java
@Profile("prod")
@Component
class ProdOnlyBean {}
```

---

## 11) Testing (Spring Boot)

### Summary
- Unit tests with JUnit/Mockito; slice tests with @WebMvcTest; integration with @SpringBootTest.
- MockMvc for controller tests; TestEntityManager for JPA.

### Examples
```java
@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired MockMvc mvc;
    @MockBean UserService service;

    @Test void getUser() throws Exception {
        when(service.findDto(1L)).thenReturn(new UserDTO("john@example.com"));
        mvc.perform(get("/api/users/1"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.email").value("john@example.com"));
    }
}

@SpringBootTest
class AppIT { @Test void contextLoads() {} }
```

---

## 12) Security (Spring Security Basics)

### Summary
- Filter chain with SecurityFilterChain bean.
- Configure authorization rules and resource server.

### Examples
```java
@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .oauth2ResourceServer(oauth2 -> oauth2.jwt());
        return http.build();
    }
}
```

---

## 13) Common Pitfalls

- Misusing field injection; prefer constructor.
- Overly broad @Component scanning; control package boundaries.
- Forgetting to close resources; prefer try-with-resources.
- Mixing entity and DTO layers; use mappers.
- Ignoring transaction boundaries; annotate service layer.
- Exposing internal errors; sanitize error responses.

---

## Common Interview Questions & Answers

### Q1: What is dependency injection and why use it?
- DI injects dependencies, removing responsibility from classes to look up/create them.
- Benefits: loose coupling, testability, maintainability.

### Q2: Difference between @Component, @Service, and @Repository?
- All are stereotypes, functionally similar for component scanning.
- @Repository additionally translates persistence exceptions into Spring's DataAccessException.
- @Service conveys business logic; @Component is generic.

### Q3: How does @Transactional work?
- Wraps method in a transaction via proxies/AOP.
- On RuntimeException or Error, transaction rolls back by default; checked exceptions require explicit rollbackFor.
- Transaction boundaries typically at service layer.

### Q4: @RestController vs @Controller?
- @RestController = @Controller + @ResponseBody; returns objects serialized to JSON by default.
- @Controller used with views/templates.

### Q5: How does Spring Boot auto-configuration work?
- Conditional beans configured based on classpath, properties, and environment using @Conditional annotations.
- Provided by spring-boot-autoconfigure module.

### Q6: How do you validate request payloads?
- Use Bean Validation annotations in DTOs and @Valid in controller method signatures.
- Handle MethodArgumentNotValidException in @ControllerAdvice.

### Q7: What is the difference between Singleton and Prototype scope?
- Singleton: one bean instance per container.
- Prototype: new instance for each injection or lookup.

### Q8: How do you manage environment-specific configurations?
- Profiles (application-{profile}.yml) and spring.profiles.active; optionally @Profile on beans.

### Q9: How do you secure REST APIs?
- Configure authorization rules, authenticate with JWT/OAuth2 using Spring Security.
- Validate inputs, avoid exposing sensitive data, enable HTTPS at infrastructure level.

### Q10: How do you handle exceptions in Spring?
- Centralize with @RestControllerAdvice/@ControllerAdvice, return consistent ErrorResponse payloads with proper status codes.

---

## Quick Reference
- DI: constructor injection; avoid field injection.
- Auto-config: Spring Boot starters + conditional beans.
- Profiles: application-{profile}.yml; @Profile annotation.
- REST: @RestController + ResponseEntity + validation.
- Data: Spring Data JPA repos + transactions.
- Actuator: health/info/metrics endpoints.
- Testing: @WebMvcTest, @SpringBootTest.
- Security: SecurityFilterChain + JWT resource server.

---

*Last Updated: January 2026*

