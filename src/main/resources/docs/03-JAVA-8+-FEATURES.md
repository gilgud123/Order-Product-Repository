# Java 8+ Features - Complete Guide

## Overview
This guide summarizes key Java 8 and later features most relevant for technical interviews and modern backend development. It includes practical examples and answers to common questions.

---

## 1) Lambda Expressions & Functional Interfaces

### Summary
- Lambdas provide concise syntax for anonymous functions.
- Functional interfaces have exactly one abstract method (SAM).
- Built-in functional interfaces: Predicate, Function, Consumer, Supplier, BiFunction, UnaryOperator, BinaryOperator.
- Enables functional-style programming, cleaner callbacks, and stream operations.

### Syntax
```java
// Basic forms
() -> 42
x -> x * 2
(x, y) -> x + y

// With type and block
(int x, int y) -> { int sum = x + y; return sum; }

// Method & constructor references
String::isEmpty
Math::max
ArrayList::new
```

### Examples
```java
Predicate<String> nonEmpty = s -> s != null && !s.isEmpty();
Function<String, Integer> lengthFn = String::length;
Consumer<String> printer = System.out::println;
Supplier<UUID> uuidSupplier = UUID::randomUUID;
BinaryOperator<Integer> add = Integer::sum;
```

### Common Questions Answered
- Why use lambdas? Concise, readable, easier functional composition, removal of boilerplate.
- What is a functional interface? Interface with one abstract method; annotated with @FunctionalInterface for intent.
- Difference between lambda and anonymous inner class? Lambdas capture effectively final variables, have simpler syntax and compiled to invokedynamic; inner classes create new class types.

---

## 2) Stream API

### Summary
- Streams provide a high-level, declarative way to process data.
- Operations are lazy; work is done at terminal operations.
- Supports parallel processing, transformations, filtering, and aggregations.

### Core Concepts
- Intermediate ops: map, filter, flatMap, distinct, sorted, peek, limit, skip
- Terminal ops: forEach, collect, reduce, count, anyMatch, allMatch, noneMatch, findFirst, findAny
- Sources: Collection.stream(), Arrays.stream(), Stream.of(), Files.lines()
- Collectors: toList, toSet, toMap, joining, groupingBy, partitioningBy, mapping, summingInt

### Examples
```java
List<Integer> data = Arrays.asList(1, 2, 3, 4, 5);
List<Integer> evensDoubled = data.stream()
    .filter(n -> n % 2 == 0)
    .map(n -> n * 2)
    .collect(Collectors.toList());

// Grouping
Map<Integer, List<String>> byLength = Stream.of("a", "bb", "ccc")
    .collect(Collectors.groupingBy(String::length));

// To map with merge function
Map<Character, Integer> freq = "banana".chars()
    .mapToObj(c -> (char) c)
    .collect(Collectors.toMap(c -> c, c -> 1, Integer::sum));

// FlatMap
List<String> words = Arrays.asList("foo bar", "baz");
List<String> tokens = words.stream()
    .flatMap(w -> Arrays.stream(w.split(" ")))
    .collect(Collectors.toList());

// Reduce
int sum = data.stream().reduce(0, Integer::sum);
```

### Common Questions Answered
- map vs flatMap? map transforms each element to one element; flatMap flattens streams of streams (or lists) into a single stream.
- Intermediate vs terminal? Intermediate builds the pipeline lazily; terminal triggers execution and produces a result or side effect.
- Lazy evaluation? Operations are not executed until a terminal operation is invoked.
- Parallel streams? Use parallelStream(); beneficial for CPU-bound, large datasets but beware contention, ordering, and thread-safety.

---

## 3) Optional

### Summary
- Wrapper to represent presence/absence of a value.
- Encourages null-safety and explicit handling.
- Avoids NullPointerException when used properly.

### Examples
```java
Optional<String> maybeName = Optional.of("Alice");
Optional<String> maybeNull = Optional.ofNullable(getNameOrNull());

String fallback = maybeNull.orElse("unknown");
String computed = maybeNull.orElseGet(() -> expensiveFallback());
String strict = maybeNull.orElseThrow(() -> new IllegalArgumentException("Missing"));

maybeNull.ifPresent(name -> log.info("Name=" + name));

// Transformations
int length = Optional.of("abc").map(String::length).orElse(0);
Optional<String> upper = Optional.of("abc").filter(s -> s.length() > 2).map(String::toUpperCase);
```

### Common Questions Answered
- Why introduced? Make null handling explicit, improve API clarity.
- When to use? Return values where absence is normal; do not use for fields or parameters; avoid Optional in collections.
- What's wrong with optional.get()? Throws NoSuchElementException if empty; prefer orElse, orElseThrow, orElseGet, or ifPresent.

---

## 4) Date & Time API (java.time)

### Summary
- Immutable, thread-safe replacements for java.util.Date/Calendar.
- Clear types: LocalDate, LocalTime, LocalDateTime, ZonedDateTime, Instant, Duration, Period.
- Consistent with ISO-8601; fluent operations.

### Examples
```java
LocalDate date = LocalDate.of(2026, 1, 10);
LocalDate nextWeek = date.plusWeeks(1);

LocalDateTime now = LocalDateTime.now();
ZonedDateTime utc = ZonedDateTime.now(ZoneId.of("UTC"));
Instant instant = Instant.now();

Duration d = Duration.ofMinutes(90);
Period p = Period.ofMonths(2);

DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
String formatted = date.format(fmt);

// Parsing
LocalDate parsed = LocalDate.parse("2026-01-10");
```

### Common Questions Answered
- Why prefer java.time? Immutable, clearer APIs, timezone-aware types, fewer bugs.
- Difference: Instant vs LocalDateTime? Instant is a moment on timeline (UTC-based); LocalDateTime has no timezone.
- How to handle time zones? Use ZonedDateTime with ZoneId; convert between zones via withZoneSameInstant.

---

## 5) CompletableFuture (Java 8) & Reactive Streams (Java 9+)

### Summary
- CompletableFuture: compose async tasks with callbacks and error handling.
- Methods: supplyAsync, thenApply, thenCompose, thenAccept, allOf, anyOf, exceptionally, handle.
- Reactive Streams (Flow API in Java 9): standard async stream processing (Publisher, Subscriber, Subscription, Processor).

### Examples (CompletableFuture)
```java
CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> compute());
CompletableFuture<String> result = future
    .thenApply(Object::toString)
    .exceptionally(ex -> "error:" + ex.getMessage());

CompletableFuture<Integer> f1 = CompletableFuture.supplyAsync(() -> 10);
CompletableFuture<Integer> f2 = CompletableFuture.supplyAsync(() -> 20);
CompletableFuture<Integer> sum = f1.thenCombine(f2, Integer::sum);

CompletableFuture<Void> all = CompletableFuture.allOf(f1, f2);
```

### Common Questions Answered
- thenApply vs thenCompose? thenApply transforms the result; thenCompose flattens nested futures (compose dependent async tasks).
- Error handling? exceptionally for fallback; handle to process both result and exception; whenComplete for side-effects.
- When to use parallel streams vs CompletableFuture? Streams are data-parallel; CompletableFuture is task-parallel and better for IO-bound or orchestrating async tasks.

---

## 6) Interface Enhancements (Default & Static Methods)

### Summary
- Interfaces can declare default methods (with implementation) and static methods.
- Enables evolving APIs without breaking implementers.

### Examples
```java
public interface Calculator {
    int add(int a, int b);
    default int subtract(int a, int b) { return a - b; }
    static int multiply(int a, int b) { return a * b; }
}

class BasicCalculator implements Calculator {
    @Override public int add(int a, int b) { return a + b; }
}
```

### Common Questions Answered
- Why default methods? Backward compatibility and interface evolution.
- Conflict resolution? If multiple interfaces define same default method, class must override and disambiguate.

---

## 7) Method References & Constructor References

### Summary
- Shorthand for lambdas that call existing methods/constructors.
- Types: object::instanceMethod, Class::staticMethod, Class::instanceMethod, Class::new.

### Examples
```java
List<String> items = Arrays.asList("a", "b");
items.forEach(System.out::println);   // Consumer
Function<String, Integer> f = String::length; // Method ref
Supplier<ArrayList<String>> sup = ArrayList::new; // Constructor ref
```

---

## 8) Streams Collectors Deep Dive

### Summary
- Powerful aggregation framework with Collectors.
- Common: toList, toSet, toMap, joining, counting, summingInt, averagingInt.
- Advanced: groupingBy, partitioningBy, mapping, reducing, collectingAndThen.

### Examples
```java
// joining
String csv = Stream.of("a", "b", "c").collect(Collectors.joining(","));

// groupingBy + downstream
Map<Integer, Long> countByLen = Stream.of("a", "aa", "bbb")
    .collect(Collectors.groupingBy(String::length, Collectors.counting()));

// collectingAndThen for immutability
List<String> immutable = Stream.of("a", "b")
    .collect(Collectors.collectingAndThen(Collectors.toList(), List::copyOf));
```

---

## 9) Optional Advanced Patterns

### Summary
- Use map/flatMap/filter composition; avoid get().
- Avoid Optional fields/params; prefer domain-specific null objects or validation.

### Examples
```java
Optional<User> maybeUser = repo.findById(id);
String email = maybeUser
    .filter(User::isActive)
    .map(User::getEmail)
    .orElse("n/a");

Optional<Order> maybeOrder = repo.findById(id);
Optional<String> ref = maybeOrder.flatMap(Order::getReferenceOptional);
```

---

## 10) Java 9-17 Notables (if asked)

### Summary
- Java 9: modules; Flow API; factory methods List.of/Set.of/Map.of.
- Java 10: var for local inference.
- Java 11: HTTP Client; String enhancements; Files methods.
- Java 14: switch expressions (preview ? standard in 14+).
- Java 16/17: records (concise immutable data carriers); sealed classes.

### Examples
```java
// Java 10 var
var list = new ArrayList<String>();

// Java 11 HttpClient
HttpClient client = HttpClient.newHttpClient();
HttpRequest req = HttpRequest.newBuilder(URI.create("https://example.com")).build();
HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

// Java 14 switch expressions
String res = switch (day) {
    case MONDAY, FRIDAY -> "busy";
    case TUESDAY -> "meeting";
    default -> "relaxed";
};

// Java 16/17 records
public record Point(int x, int y) {}
```

---

## Common Interview Questions (Java 8+)

### Q1: map vs flatMap in streams
- map: one-to-one transformation; result type changes but cardinality same.
- flatMap: one-to-many; flattens nested streams/collections.

### Q2: Intermediate vs terminal operations
- Intermediate: lazy, return a Stream, can be chained (map, filter, sorted).
- Terminal: triggers execution and returns a non-stream result (collect, reduce, forEach).

### Q3: Lazy evaluation in streams
- Stream pipelines build descriptions; data is processed only when a terminal op is invoked.
- Optimizations like short-circuiting (anyMatch) and fused operations.

### Q4: Parallel streams pros/cons
- Pros: Easy concurrency for large, CPU-bound tasks.
- Cons: Non-deterministic ordering; overhead; thread-safety issues; poor performance for IO-bound or small datasets.
- Use with caution: measure, avoid shared mutable state, specify ordered collectors if needed.

### Q5: Optional best practices
- Do not call get(); prefer orElse, orElseThrow, ifPresent.
- Do not use Optional for fields, parameters, or in collections.
- Use Optional to signal absence in return types.

### Q6: thenApply vs thenCompose
- thenApply: synchronous transformation of result.
- thenCompose: flatten dependent async computations returning CompletableFuture.

### Q7: Default methods in interfaces
- Purpose: evolve APIs; provide common behavior.
- Diamond conflict: implementer must override to resolve ambiguity.

### Q8: Method references
- Provide readability and reuse existing methods; equivalent to specific lambda shapes.

---

## Best Practices
- Favor immutable data and pure functions in stream pipelines.
- Keep streams short and readable; prefer named methods for complex logic.
- Avoid side effects in stream operations; use collectors and reducers.
- Prefer java.time for date/time; avoid legacy Date/Calendar.
- Be explicit with Optional; never store in fields.
- Use CompletableFuture for async orchestration; avoid blocking.
- Benchmark parallel streams before adopting.

---

## Quick Cheats
- map: transform; flatMap: flatten.
- filter: keep; distinct: unique; sorted: order.
- collect: materialize; reduce: aggregate.
- Optional: orElse, orElseGet, orElseThrow; never get().
- CompletableFuture: supplyAsync ? thenApply/compose ? handle/ex.

---

*Last Updated: January 2026*

