# Exception Handling - Complete Guide

## Overview
A practical guide to Java exception handling for interviews and production code: hierarchy, checked vs unchecked, best practices, try-with-resources, custom exceptions, and common pitfalls with answers to typical questions.

---

## 1) Exception Hierarchy

### Summary
- All exceptions derive from `Throwable`.
- `Error`: serious issues not intended to be caught (OutOfMemoryError, StackOverflowError).
- `Exception`: recoverable conditions.
  - Checked exceptions: must be declared or caught (IOException, SQLException).
  - Unchecked exceptions: RuntimeException and its subclasses; don't require handling (NullPointerException, IllegalArgumentException).

### Diagram
```
Throwable
??? Error
?   ??? OutOfMemoryError
?   ??? StackOverflowError
??? Exception
    ??? RuntimeException (unchecked)
    ?   ??? NullPointerException
    ?   ??? IllegalArgumentException
    ?   ??? IllegalStateException
    ??? IOException (checked)
        ??? FileNotFoundException
        ??? EOFException
```

---

## 2) Checked vs Unchecked Exceptions

### Summary
- Checked: represent expected recoverable conditions; method must declare `throws` or handle.
- Unchecked: programming errors; usually indicate bugs; can be thrown anytime.

### Guidance
- Use checked exceptions when caller can reasonably recover.
- Use unchecked exceptions for programmer errors (invalid arguments, bad state).

### Examples
```java
// Checked
void readFile(Path path) throws IOException {
    Files.readAllLines(path);
}

// Unchecked
void setAge(int age) {
    if (age < 0) throw new IllegalArgumentException("Age must be >= 0");
}
```

---

## 3) Try-Catch-Finally & Try-with-Resources

### Summary
- try-catch-finally: handle exceptions and ensure cleanup.
- try-with-resources: auto-closes resources implementing AutoCloseable.
- Finally always runs (unless JVM exits or thread killed abruptly).

### Examples
```java
// Traditional try-catch-finally
BufferedReader br = null;
try {
    br = Files.newBufferedReader(Path.of("data.txt"));
    String line = br.readLine();
} catch (IOException e) {
    // Handle
    log.error("IO error", e);
} finally {
    if (br != null) try { br.close(); } catch (IOException ignore) {}
}

// Try-with-resources (Java 7+)
try (BufferedReader br2 = Files.newBufferedReader(Path.of("data.txt"))) {
    String line = br2.readLine();
} catch (IOException e) {
    log.error("IO error", e);
}

// Multiple resources
try (InputStream in = Files.newInputStream(inPath);
     OutputStream out = Files.newOutputStream(outPath)) {
    in.transferTo(out);
}
```

### Suppressed Exceptions
- TWR captures exceptions thrown by `close()` as suppressed.
```java
try (MyRes r = new MyRes()) {
    // ...
} catch (Exception e) {
    for (Throwable sup : e.getSuppressed()) {
        log.warn("Suppressed:", sup);
    }
}
```

---

## 4) Custom Exceptions

### Summary
- Create domain-specific exceptions to communicate intent.
- Prefer concise, informative messages and cause chaining.

### Examples
```java
public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(Long id) {
        super("Order not found: " + id);
    }
}

public class PaymentException extends Exception { // checked
    public PaymentException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

### Best Practices
- Keep names meaningful (e.g., UserNotAuthorizedException).
- Include context (ids, states) in messages.
- Prefer unchecked for validation/business rule violations; checked for external failure (IO, network).

---

## 5) Exception Propagation & Wrapping

### Summary
- Propagate exceptions when callers can handle them better.
- Wrap low-level exceptions with higher-level context (and preserve cause).

### Examples
```java
public User loadUser(Long id) {
    try {
        return repository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
    } catch (DataAccessException dae) {
        throw new UserRepositoryException("Failed to load user " + id, dae);
    }
}
```

---

## 6) Validation & Defensive Programming

### Summary
- Validate inputs early; fail fast with clear messages.
- Use Objects.requireNonNull, Preconditions, Bean Validation (@Valid).

### Examples
```java
public void createUser(String email) {
    Objects.requireNonNull(email, "email");
    if (!email.contains("@")) throw new IllegalArgumentException("Invalid email");
    // proceed
}
```

---

## 7) Logging vs Swallowing Exceptions

### Summary
- Never silently swallow exceptions.
- Log at appropriate level with context.
- Avoid logging and rethrowing at multiple layers (log once).

### Examples
```java
try {
    risky();
} catch (IOException e) {
    log.warn("Failed to read config {}", path, e);
    // decide: recover or propagate
}
```

---

## 8) Common Pitfalls

### Pitfalls
- Catching `Exception` broadly hides real issues.
- Overusing checked exceptions, causing boilerplate.
- Losing the original cause when rethrowing.
- Using exceptions for control flow (instead of proper logic).
- Ignoring InterruptedException.

### Examples & Fixes
```java
// ? Bad: broad catch
try { doWork(); } catch (Exception e) { /* ignore */ }

// ? Better: catch specific
try { doWork(); } catch (FileNotFoundException e) { /* handle */ }

// Preserving cause
try { io(); } catch (IOException e) { throw new ConfigLoadException("Failed", e); }

// Handle InterruptedException properly
try { Thread.sleep(1000); } catch (InterruptedException ie) {
    Thread.currentThread().interrupt();
    return; // or propagate
}
```

---

## 9) Exception Handling in Streams and Lambdas

### Summary
- Lambdas cannot throw checked exceptions unless the functional method declares it.
- Wrap checked exceptions or use helper functions.

### Examples
```java
// Wrap checked exceptions
List<String> lines = files.stream()
    .map(path -> {
        try { return Files.readString(path); }
        catch (IOException e) { throw new UncheckedIOException(e); }
    })
    .collect(Collectors.toList());
```

---

## 10) Controller/Service Layer Patterns (Spring)

### Summary
- Centralize error handling with @ControllerAdvice.
- Return consistent error responses.
- Use @ResponseStatus or ResponseEntity for mapping codes.

### Examples
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(OrderNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("ORDER_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse("GENERIC_ERROR", "Unexpected error"));
    }
}
```

---

## 11) Best Practices

- Fail fast on invalid input; validate early.
- Use meaningful messages; include context.
- Choose checked vs unchecked appropriately.
- Use try-with-resources for I/O.
- Preserve cause when wrapping exceptions.
- Don’t swallow exceptions; log once with context.
- Handle InterruptedException by re-interrupting the thread.
- Keep catch blocks small and specific.

---

## Common Interview Questions & Answers

### Q1: Checked vs unchecked exceptions
- Checked: must be declared or caught; represent recoverable conditions (e.g., IOException).
- Unchecked: RuntimeException; indicate programming errors (e.g., NullPointerException).
- Guidance: Use checked for external failures; unchecked for validation or bugs.

### Q2: Why try-with-resources? How does it work?
- Ensures resources are closed automatically even if exceptions occur.
- Compiles into a try-finally with resource.close() calls; exceptions during close are added as suppressed to the primary exception.

### Q3: Should you catch Exception or specific exceptions?
- Prefer specific exceptions to handle expected cases.
- Catching Exception can mask bugs; reserve for top-level handlers to return generic responses.

### Q4: How do you preserve the original cause when rethrowing?
- Use constructor with `Throwable cause` or `initCause`:
```java
catch (IOException e) {
    throw new ConfigLoadException("Failed to load config", e);
}
```

### Q5: What is the difference between `throw` and `throws`?
- `throw`: used in code to actually throw an exception instance.
- `throws`: used in method signature to declare possible exceptions.

### Q6: How to handle InterruptedException correctly?
- Catch and call `Thread.currentThread().interrupt()` to restore the interrupt flag.
- Decide to abort or propagate based on context; avoid swallowing.

### Q7: When should you create custom exceptions?
- When domain-specific errors improve clarity and handling.
- Provide meaningful names and messages; avoid generic RuntimeException for everything.

### Q8: How do you handle exceptions in streams/lambdas?
- Wrap checked exceptions in unchecked (UncheckedIOException) or write helper methods that adapt checked exceptions.

### Q9: What is UncheckedIOException? When to use it?
- RuntimeException wrapper for IOException, useful in streams where checked exceptions are cumbersome.

### Q10: What are suppressed exceptions?
- Additional exceptions thrown while closing resources, attached to primary exception via `getSuppressed()`.

---

## Quick Reference
- Hierarchy: Throwable ? Error/Exception ? RuntimeException.
- Checked vs Unchecked: recovery vs programming error.
- TWR: auto-close resources; suppressed exceptions.
- Custom exceptions: meaningful, context-rich, preserve cause.
- Logging: log once, avoid swallow; handle InterruptedException.

---

*Last Updated: January 2026*

