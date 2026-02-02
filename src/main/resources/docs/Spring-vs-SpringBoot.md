# Spring Framework vs Spring Boot - Quick Reference

## What is Spring (Spring Framework)?
A comprehensive, modular Java framework for building applications. Core responsibilities include:
- Inversion of Control (IoC) / Dependency Injection (DI)
- Aspect-Oriented Programming (AOP)
- Declarative transaction management
- Web frameworks (Spring MVC, WebFlux)
- Data access integration (JDBC, JPA/Hibernate)
- Security (Spring Security)
- Integration, Messaging, Scheduling, Cache, Batch

Spring is unopinionated: you pick libraries, versions and configure wiring (XML or Java `@Configuration`). It's ideal where you need full control over dependencies and runtime packaging.

---

## What is Spring Boot?
Spring Boot is an opinionated layer on top of the Spring Framework whose goal is to make creating production-ready Spring applications fast and easy.
Key capabilities:
- Auto-configuration: auto-registers common beans based on classpath and properties
- Starter dependencies: curated "starters" (e.g., `spring-boot-starter-web`) that pull in compatible libraries
- Embedded server: run as a self-contained fat JAR with an embedded servlet container (Tomcat/Jetty/Undertow)
- Actuator: production endpoints for health, metrics, info, traces
- Devtools: improved developer experience (restart/live reload)
- Convention-over-configuration defaults while allowing overrides

Spring Boot lets you focus on application code rather than build and wiring details.

---

## Main Spring Framework Features (overview)
- Core / IoC (ApplicationContext, Bean lifecycle)
- AOP (interceptors, proxies)
- Data & Transactions (JdbcTemplate, PlatformTransactionManager, JPA)
- Spring MVC (controllers, view resolution, validation)
- WebFlux (reactive, non-blocking)
- Spring Security (authentication/authorization, OAuth2)
- Spring Data (repositories and query derivation for JPA, MongoDB, Redis, etc.)
- Integration & Messaging (Spring Integration, AMQP, Kafka)
- Scheduling & Batch (Spring Batch, `@Scheduled`)
- Caching abstraction
- Testing support (TestContext framework, slice testing)

---

## Area-by-area comparison (Spring vs Spring Boot)

- Project Setup & Dependencies
  - Spring: Add individual module dependencies and manage versions yourself.
  - Boot: Use starters (e.g., `spring-boot-starter-web`, `spring-boot-starter-data-jpa`) and a BOM for compatible versions.

- Configuration & Wiring
  - Spring: You explicitly register `@Configuration` / `@Bean` (or XML).
  - Boot: Auto-configuration supplies sensible beans; you override via `@Configuration` or properties.

- Packaging & Server
  - Spring: Typically WAR deployed into external servlet container. You control container.
  - Boot: Executable JAR with embedded container by default; can still build WAR if needed.

- Bootstrapping & Startup
  - Spring: Manually create and manage the `ApplicationContext` or rely on a container.
  - Boot: `SpringApplication.run(...)` bootstraps context, loads properties, and applies auto-config.

- Production tooling
  - Spring: Add libraries (Micrometer, health checks) yourself.
  - Boot: Actuator + Micrometer integration out-of-the-box.

- Developer experience
  - Spring: More boilerplate for small apps.
  - Boot: Devtools, CLI, quickstart templates speed up development.

- Testing
  - Spring: Flexible test support, but you wire more manually.
  - Boot: `@SpringBootTest` and specialized test slices (`@WebMvcTest`, `@DataJpaTest`) simplify tests.

- Opinionation
  - Spring: Unopinionated (maximum flexibility).
  - Boot: Opinionated defaults (convention-over-configuration).

---

## Concrete examples

### Maven dependency (Spring Boot starter)

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```
This starter brings Spring MVC, Jackson, validation, logging and embedded Tomcat (by default).


### Main class (Boot)

```java
@SpringBootApplication
public class ProductApplication {
  public static void main(String[] args) {
    SpringApplication.run(ProductApplication.class, args);
  }
}
```
`@SpringBootApplication` is shorthand for `@Configuration`, `@EnableAutoConfiguration`, and `@ComponentScan`.


### Simple Controller (Boot or Spring MVC)

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
  private final ProductService svc;
  public ProductController(ProductService svc) { this.svc = svc; }

  @GetMapping
  public List<Product> list() { return svc.findAll(); }
}
```

(The same controller works with plain Spring MVC if you wire the DispatcherServlet and beans.)

---

## Why choose Spring Boot (advantages)
- Rapid project setup and consistent dependency versions
- Minimal boilerplate—focus on business code
- Built-in production features (Actuator, metrics)
- Embedded server for easy local run and CI
- Great DX: devtools, auto-reload, CLI
- Test slices and opinionated testing support

---

## When to use plain Spring (no Boot)
- You need *very* fine-grained control over dependency versions and wiring.
- You're deploying to enterprise-managed application servers and must produce WARs without embedded servers.
- You maintain a legacy large app where migrating to Boot would be disruptive.
- You build a framework or library where Boot's auto-config might be undesirable.

Note: Even in these cases you can often adopt selective Boot features (dependency management/BOM) without full Boot auto-config.

---

## Pros & Cons (short)

Spring Framework (core)
- Pros: Flexible, modular, minimal magic, ideal for custom infra
- Cons: More boilerplate, slower to bootstrap a new app

Spring Boot
- Pros: Fast to start, batteries-included, consistent and production-ready defaults
- Cons: Opinionated defaults can surprise; slightly larger artifact sizes; you must understand how to opt out of auto-configuration

---

## Migration notes: Spring ? Spring Boot (practical steps)
1. Add Spring Boot parent or BOM and Boot plugin to `pom.xml`.
2. Replace sets of dependencies with starters where appropriate.
3. Add a `@SpringBootApplication` main class and `SpringApplication.run()` entry point.
4. Run the app as an executable JAR; migrate any external server configuration to Boot properties.
5. Remove or adapt manual wiring that Boot auto-configures (data sources, object mapper, security adapters).
6. Use `spring.autoconfigure.exclude` to opt out of problematic auto-configurations.
7. Test thoroughly and remove temporary `--add-opens` flags if used.

---

## Decision checklist (quick)
- Greenfield microservice or REST API ? Spring Boot
- Need embedded server for local/CI convenience ? Spring Boot
- Legacy enterprise WAR deployment with strict server controls ? Plain Spring
- Library or framework intended for other apps to extend ? Plain Spring

---

## Extra references
- Spring Framework: https://spring.io/projects/spring-framework
- Spring Boot: https://spring.io/projects/spring-boot
- Guides: https://spring.io/guides

---

*Last updated: January 2026*

