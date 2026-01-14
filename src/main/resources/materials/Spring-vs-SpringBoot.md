What is Spring (Spring Framework)?
A comprehensive Java framework for building applications (DI/IoC, AOP, MVC, Data, Security, etc.).
Very flexible but requires manual configuration (XML or Java config).
You assemble dependencies and configure infrastructure yourself.
Typically deploy to an external servlet container (Tomcat/Jetty) unless you wire your own.
What is Spring Boot?
A layer on top of Spring that makes setup and development fast and opinionated.
Auto-configuration: detects libraries and configures common beans for you.
Starter dependencies: curated POMs (spring-boot-starter-*) that pull in compatible libraries.
Embedded server: runs with embedded Tomcat/Jetty/Undertow, so you can run as a fat JAR.
Production-ready features out of the box (Actuator: health, metrics, info, etc.).
Convention over configuration; minimal boilerplate.
Key differences
Setup:
Spring: manual dependency management and configuration.
Boot: starters + sensible defaults + auto-config.
Configuration:
Spring: you define most beans and settings.
Boot: auto-configures based on classpath; override via application.properties/yml.
Server & packaging:
Spring: often WAR to external server.
Boot: fat JAR with embedded server; java -jar to run.
Production tooling:
Spring: you add and wire monitoring yourself.
Boot: Actuator endpoints included and easy to enable.
Philosophy:
Spring: maximum flexibility, minimal assumptions.
Boot: opinionated defaults to get you productive quickly.
When to use which?
Spring Boot: default choice for new apps, microservices, and rapid development.
Spring (without Boot): niche cases with strict custom infrastructure, legacy constraints, or when you explicitly don’t want Boot’s opinionated layer.