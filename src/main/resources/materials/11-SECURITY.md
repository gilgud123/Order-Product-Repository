# Security - Complete Guide

## Overview
A practical guide to application security for Java/Spring developers: authentication, authorization, JWT/OAuth2, CSRF/CORS, secure headers, common vulnerabilities (OWASP Top 10), input validation, encryption, secrets handling, and Spring Security configuration. Includes answers to common interview questions.

---

## 1) Authentication vs Authorization

### Summary
- Authentication: verifying identity (who you are).
- Authorization: checking permissions (what you can do).
- Often implemented together; separation of concerns is key.

### Examples
- Authentication: login with username/password ? issue JWT.
- Authorization: allow /api/admin/** only for ROLE_ADMIN.

---

## 2) Spring Security Basics

### Summary
- SecurityFilterChain processes requests through a chain of filters.
- Configure HTTP security with HttpSecurity.
- Use method-level security with @EnableMethodSecurity and @PreAuthorize.

### Example Configuration
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .csrf(csrf -> csrf.disable()) // enable for browser apps; disable for stateless APIs
            .oauth2ResourceServer(oauth2 -> oauth2.jwt());
        return http.build();
    }
}
```

### Method Security
```java
@PreAuthorize("hasRole('ADMIN')")
public void deleteUser(Long id) { /* ... */ }
```

---

## 3) JWT (JSON Web Tokens)

### Summary
- Self-contained tokens with claims, signed (HS256/RS256) to prevent tampering.
- Stateless: server validates signature and claims; no server-side session.
- Include expiration (exp), issuer (iss), subject (sub), and custom claims.

### Structure
- Header.Payload.Signature (Base64Url encoded).

### Example Claims
```json
{
  "sub": "123",
  "roles": ["USER", "ADMIN"],
  "iat": 1690000000,
  "exp": 1690003600
}
```

### Best Practices
- Short-lived access tokens; use refresh tokens.
- Store secrets securely; rotate keys.
- Validate exp, iss, aud; avoid putting sensitive data in tokens.

---

## 4) OAuth2 Basics

### Summary
- Authorization framework to grant limited access to resources.
- Flows: Authorization Code (with PKCE), Client Credentials, Resource Owner Password (legacy), Implicit (deprecated).
- Use an authorization server (e.g., Keycloak, Auth0, Spring Authorization Server).

### Spring Resource Server
```java
http.oauth2ResourceServer(oauth2 -> oauth2.jwt());
```

---

## 5) CSRF & CORS

### CSRF (Cross-Site Request Forgery)
- Targets browser-based apps with cookies; attacker tricks victim’s browser to send authenticated requests.
- Protection: CSRF tokens, SameSite cookies, double-submit.
- For stateless APIs consumed by SPAs with tokens, CSRF can be disabled.

### CORS (Cross-Origin Resource Sharing)
- Controls which origins can call your API from browsers.
- Configure allowed origins, methods, headers; avoid wildcard in production.

### Example CORS Config (Spring)
```java
@Bean
CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("https://app.example.com"));
    config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","PATCH"));
    config.setAllowedHeaders(List.of("Authorization","Content-Type"));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
}
```

---

## 6) Secure Headers

### Summary
- Add headers to defend against common attacks.
- Examples: Content-Security-Policy, X-Content-Type-Options, X-Frame-Options, Strict-Transport-Security.

### Spring Security Defaults
- Many headers enabled by default; customize as needed.

---

## 7) Input Validation & Output Encoding

### Summary
- Validate all inputs (body, query params, headers) using Bean Validation.
- Encode outputs to prevent XSS; avoid reflecting untrusted input.
- Sanitize file names, paths; restrict file uploads.

### Example Validation
```java
class RegisterRequest {
    @NotBlank @Email String email;
    @Size(min=8, max=64) String password;
}
```

---

## 8) Common Vulnerabilities (OWASP Top 10)

### Summary
- Injection (SQL, NoSQL, OS): use parameterized queries, validate input.
- Broken Authentication: secure credential storage, rate limiting, MFA.
- Sensitive Data Exposure: TLS, encryption at rest, avoid logging secrets.
- XML External Entities (XXE): disable external entities.
- Security Misconfiguration: default creds, over-broad CORS, verbose errors.
- XSS: output encoding, CSP, sanitize.
- Insecure Deserialization: avoid, validate types, use safe formats (JSON).
- Using Components with Known Vulnerabilities: dependency scanning.
- Insufficient Logging & Monitoring: audit access, alerts.
- Server-Side Request Forgery (SSRF): limit outbound requests, validate URLs.

---

## 9) Encryption & Hashing

### Summary
- Use strong password hashing (bcrypt, scrypt, Argon2) with salt.
- Use AES-GCM for symmetric encryption; RSA/ECDSA for asymmetric.
- Manage keys with a KMS; never hardcode keys.

### Example (Spring PasswordEncoder)
```java
@Bean
PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

---

## 10) Secrets Management

### Summary
- Don’t store secrets in source code or plaintext config.
- Use environment variables, vaults (HashiCorp Vault, AWS Secrets Manager), Spring Cloud Config with encryption.
- Rotate credentials; least-privilege for service accounts.

---

## 11) Rate Limiting & Throttling

### Summary
- Prevent abuse with rate limits (e.g., token bucket, leaky bucket).
- Return 429 Too Many Requests; include retry headers.
- Implement at gateway/reverse proxy (Nginx, API Gateway) or in-app libraries.

---

## 12) Auditing & Monitoring

### Summary
- Log security-relevant events (login, failed attempts, admin actions).
- Correlate requests with IDs; use centralized logging and alerts.
- Monitor metrics (auth failures, 401/403 rates) and set thresholds.

---

## 13) Testing Security

### Summary
- Unit/integration tests for auth/authorization rules.
- Security tests with Spring Security’s test utilities.
- Dynamic scanning tools (OWASP ZAP); dependency scanners (OWASP Dependency-Check).

### Examples
```java
@Test
void adminEndpoint_requiresAdminRole() throws Exception {
    mockMvc.perform(get("/api/admin/stats").with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
           .andExpect(status().isOk());
}
```

---

## 14) Common Pitfalls

- Disabling security globally for convenience.
- Using wildcard CORS in production.
- Storing passwords in plaintext.
- Long-lived JWTs without revocation.
- Leaking internal stack traces to clients.
- Missing validation for inputs and file uploads.

---

## Common Interview Questions & Answers

### Q1: Difference between authentication and authorization?
- Authentication verifies identity; authorization checks permissions for actions/resources.

### Q2: How does JWT work and what are its pros/cons?
- JWT is a signed token with claims; server validates signature. Pros: stateless, scalable. Cons: revocation hard, token bloat, must secure storage and expiration.

### Q3: When should you enable/disable CSRF?
- Enable for browser apps using cookies and forms. Disable for stateless APIs using bearer tokens.

### Q4: How do you configure CORS securely?
- Allow only trusted origins, restrict methods/headers, no wildcard in prod, support credentials only when necessary.

### Q5: How to store passwords securely?
- Use strong hashing (bcrypt/Argon2) with salt, never plaintext, enforce password policies.

### Q6: What is OAuth2 and which flow to use?
- Framework to delegate authorization. Use Authorization Code with PKCE for SPAs/mobile; Client Credentials for server-to-server; avoid Implicit and Resource Owner Password.

### Q7: How do you secure REST APIs in Spring Boot?
- Enforce HTTPS, configure SecurityFilterChain, JWT/OAuth2 resource server, validate inputs, apply role-based access, rate limit, log and monitor.

### Q8: What are common OWASP Top 10 issues you watch for?
- Injection, broken auth, sensitive data exposure, XSS, misconfiguration, insecure deserialization, vulnerable components, insufficient logging/monitoring, SSRF.

### Q9: How do you handle secrets?
- Use environment variables or secrets managers; avoid hardcoding; rotate regularly; restrict access via IAM.

### Q10: How do you test security rules?
- Use Spring Security test utilities (with(jwt()...)), unit tests for authorization, integration tests for endpoints, dynamic scanners.

---

## Quick Reference
- Authentication vs Authorization.
- Spring Security: SecurityFilterChain, method security.
- JWT/OAuth2: short-lived tokens, validate claims.
- CSRF for browser apps; CORS restrict origins.
- Secure headers, validation, and encoding.
- Password hashing with BCrypt/Argon2.
- Secrets in vaults; no hardcoding.
- Rate limiting and monitoring.
- OWASP Top 10 awareness.

---

*Last Updated: January 2026*

