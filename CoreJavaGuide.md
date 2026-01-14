# Comprehensive Core Java Knowledge Guide for Medior Java Developer

## Table of Contents
1. [Java 8+ Features](#1-java-8-features)
2. [Collections Framework](#2-collections-framework)
3. [Exception Handling](#3-exception-handling)
4. [Multithreading and Concurrency](#4-multithreading-and-concurrency)
5. [JVM Internals](#5-jvm-internals)
6. [String Handling](#6-string-handling)
7. [Input/Output](#7-inputoutput)
8. [Reflection and Annotations](#8-reflection-and-annotations)

---

## 1. Java 8+ Features

### Lambda Expressions
Lambda expressions provide a concise way to represent anonymous functions. They enable functional programming in Java.

**Syntax:**
```java
// Single parameter, no type declaration
(x) -> x * x

// Multiple parameters with type declaration
(int a, int b) -> a + b

// No parameters
() -> System.out.println("Hello")

// Multiple statements with curly braces
(x, y) -> {
    int sum = x + y;
    return sum * 2;
}
```

**Method References:**
```java
// Static method reference
Function<String, Integer> parser = Integer::parseInt;

// Instance method reference
String str = "Hello";
Supplier<Integer> lengthSupplier = str::length;

// Constructor reference
Supplier<ArrayList<String>> listSupplier = ArrayList::new;
```

### Stream API
Streams provide a functional approach to processing collections of objects.

**Creating Streams:**
```java
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
Stream<String> stream = names.stream();
Stream<Integer> numbers = Stream.of(1, 2, 3, 4, 5);
Stream<Integer> infinite = Stream.iterate(0, n -> n + 2);
```

**Intermediate Operations (return a new stream):**
```java
// filter - select elements matching a predicate
List<Integer> evens = numbers.stream()
    .filter(n -> n % 2 == 0)
    .collect(Collectors.toList());

// map - transform each element
List<String> upperNames = names.stream()
    .map(String::toUpperCase)
    .collect(Collectors.toList());

// flatMap - flatten nested structures
List<List<Integer>> nested = Arrays.asList(
    Arrays.asList(1, 2), 
    Arrays.asList(3, 4)
);
List<Integer> flattened = nested.stream()
    .flatMap(List::stream)
    .collect(Collectors.toList());

// sorted - sort elements
List<String> sorted = names.stream()
    .sorted()
    .collect(Collectors.toList());

// distinct - remove duplicates
List<Integer> unique = numbers.stream()
    .distinct()
    .collect(Collectors.toList());

// limit - restrict stream size
List<Integer> first3 = numbers.stream()
    .limit(3)
    .collect(Collectors.toList());

// skip - skip first n elements
List<Integer> afterFirst2 = numbers.stream()
    .skip(2)
    .collect(Collectors.toList());
```

**Terminal Operations (produce a result):**
```java
// collect - gather results into a collection
List<String> list = stream.collect(Collectors.toList());
Set<String> set = stream.collect(Collectors.toSet());
Map<Integer, String> map = stream.collect(
    Collectors.toMap(String::length, s -> s)
);

// reduce - combine elements into single result
Optional<Integer> sum = numbers.stream()
    .reduce((a, b) -> a + b);
int total = numbers.stream()
    .reduce(0, Integer::sum);

// forEach - perform action on each element
names.stream().forEach(System.out::println);

// count - count elements
long count = stream.count();

// anyMatch, allMatch, noneMatch - test predicates
boolean hasLongName = names.stream()
    .anyMatch(name -> name.length() > 5);

// findFirst, findAny - retrieve elements
Optional<String> first = names.stream().findFirst();
```

**Parallel Streams:**
```java
// Convert to parallel stream for concurrent processing
List<Integer> results = numbers.parallelStream()
    .map(n -> n * n)
    .collect(Collectors.toList());

// Use when: processing is CPU-intensive and dataset is large
// Avoid when: operations have side effects or order matters
```

### Optional
Optional is a container object that may or may not contain a non-null value.

```java
// Creating Optional
Optional<String> empty = Optional.empty();
Optional<String> present = Optional.of("value");
Optional<String> nullable = Optional.ofNullable(nullableString);

// Checking presence
if (optional.isPresent()) {
    String value = optional.get();
}

// Modern approach - avoiding get()
optional.ifPresent(value -> System.out.println(value));

// Providing defaults
String result = optional.orElse("default");
String result2 = optional.orElseGet(() -> computeDefault());
String result3 = optional.orElseThrow(() -> new IllegalStateException());

// Transformation
Optional<Integer> length = optional.map(String::length);
Optional<String> upper = optional
    .filter(s -> s.length() > 3)
    .map(String::toUpperCase);

// flatMap for nested Optionals
Optional<Optional<String>> nested = Optional.of(Optional.of("value"));
Optional<String> flattened = nested.flatMap(o -> o);

// Chaining operations
String result = repository.findById(id)
    .map(Entity::getName)
    .filter(name -> name.startsWith("A"))
    .orElseThrow(() -> new ResourceNotFoundException("Entity", id));
```

### Functional Interfaces
Interfaces with exactly one abstract method, used as lambda target types.

```java
// Predicate<T> - tests a condition
Predicate<String> isEmpty = String::isEmpty;
Predicate<Integer> isEven = n -> n % 2 == 0;
list.stream().filter(isEven).collect(Collectors.toList());

// Function<T, R> - transforms input to output
Function<String, Integer> toLength = String::length;
Function<Integer, String> toString = Object::toString;
list.stream().map(toLength).collect(Collectors.toList());

// Consumer<T> - performs action on input
Consumer<String> printer = System.out::println;
list.forEach(printer);

// Supplier<T> - provides a value
Supplier<LocalDateTime> currentTime = LocalDateTime::now;
LocalDateTime now = currentTime.get();

// BiFunction<T, U, R> - two inputs, one output
BiFunction<Integer, Integer, Integer> add = (a, b) -> a + b;
Integer sum = add.apply(5, 3);

// UnaryOperator<T> - Function where input and output are same type
UnaryOperator<Integer> square = n -> n * n;

// BinaryOperator<T> - BiFunction where all types are the same
BinaryOperator<Integer> multiply = (a, b) -> a * b;

// Custom functional interfaces
@FunctionalInterface
public interface Processor<T> {
    T process(T input);
    
    // Default methods are allowed
    default T processWithLogging(T input) {
        System.out.println("Processing: " + input);
        return process(input);
    }
}
```

### Default and Static Methods in Interfaces
Java 8 allows interfaces to have method implementations.

```java
public interface Vehicle {
    // Abstract method
    void start();
    
    // Default method - provides default implementation
    default void stop() {
        System.out.println("Vehicle stopped");
    }
    
    default void honk() {
        System.out.println("Beep beep!");
    }
    
    // Static method - utility method
    static boolean isValidSpeed(int speed) {
        return speed >= 0 && speed <= 200;
    }
}

public class Car implements Vehicle {
    @Override
    public void start() {
        System.out.println("Car started");
    }
    
    // Can override default methods
    @Override
    public void stop() {
        System.out.println("Car stopped with brakes");
    }
    
    // honk() is inherited from interface
}

// Usage
Car car = new Car();
car.start();
car.stop();
car.honk(); // Uses default implementation
boolean valid = Vehicle.isValidSpeed(100); // Call static method
```

### Date/Time API
Modern date and time handling introduced in Java 8.

```java
// LocalDate - date without time
LocalDate today = LocalDate.now();
LocalDate specificDate = LocalDate.of(2024, 12, 25);
LocalDate parsed = LocalDate.parse("2024-12-25");

LocalDate tomorrow = today.plusDays(1);
LocalDate nextWeek = today.plusWeeks(1);
LocalDate lastMonth = today.minusMonths(1);

int year = today.getYear();
Month month = today.getMonth();
int day = today.getDayOfMonth();

// LocalTime - time without date
LocalTime now = LocalTime.now();
LocalTime specificTime = LocalTime.of(14, 30, 0);
LocalTime inTwoHours = now.plusHours(2);

// LocalDateTime - date and time without timezone
LocalDateTime dateTime = LocalDateTime.now();
LocalDateTime specific = LocalDateTime.of(2024, 12, 25, 14, 30);
LocalDateTime combined = LocalDateTime.of(today, now);

// ZonedDateTime - date, time, and timezone
ZonedDateTime zonedNow = ZonedDateTime.now();
ZonedDateTime tokyo = ZonedDateTime.now(ZoneId.of("Asia/Tokyo"));
ZonedDateTime withZone = dateTime.atZone(ZoneId.of("Europe/Paris"));

// Period - date-based amount (years, months, days)
Period period = Period.between(LocalDate.of(2020, 1, 1), today);
int years = period.getYears();

// Duration - time-based amount (hours, minutes, seconds)
Duration duration = Duration.between(
    LocalTime.of(9, 0), 
    LocalTime.of(17, 30)
);
long hours = duration.toHours();

// Formatting
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
String formatted = dateTime.format(formatter);
LocalDateTime parsedDateTime = LocalDateTime.parse("25/12/2024 14:30", formatter);

// Common patterns
DateTimeFormatter iso = DateTimeFormatter.ISO_LOCAL_DATE;
DateTimeFormatter custom = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
```

---

## 2. Collections Framework

### List Implementations

**ArrayList:**
```java
// Backed by resizable array
List<String> arrayList = new ArrayList<>();

// Pros:
// - Fast random access: O(1)
// - Memory efficient for storage
// - Good for iteration

// Cons:
// - Slow insertion/deletion in middle: O(n)
// - Resizing overhead when capacity exceeded

// Use when: Random access is frequent, size is relatively stable
```

**LinkedList:**
```java
// Doubly-linked list implementation
List<String> linkedList = new LinkedList<>();

// Pros:
// - Fast insertion/deletion: O(1)
// - No resizing needed
// - Implements Queue and Deque

// Cons:
// - Slow random access: O(n)
// - Higher memory overhead (node objects)

// Use when: Frequent insertions/deletions, sequential access
```

### Set Implementations

**HashSet:**
```java
Set<String> hashSet = new HashSet<>();

// Characteristics:
// - Backed by HashMap
// - No ordering guarantee
// - O(1) add, remove, contains
// - Allows one null element

// Use when: Fast lookups needed, order doesn't matter
```

**TreeSet:**
```java
Set<String> treeSet = new TreeSet<>();

// Characteristics:
// - Backed by TreeMap (Red-Black tree)
// - Sorted order (natural or custom Comparator)
// - O(log n) add, remove, contains
// - Does not allow null

// Use when: Sorted order required
```

**LinkedHashSet:**
```java
Set<String> linkedHashSet = new LinkedHashSet<>();

// Characteristics:
// - Maintains insertion order
// - Slightly slower than HashSet
// - O(1) operations like HashSet

// Use when: Need both fast access and predictable iteration order
```

### Map Implementations

**HashMap:**
```java
Map<String, Integer> hashMap = new HashMap<>();

// Characteristics:
// - No ordering
// - O(1) get, put operations
// - One null key, multiple null values
// - Not thread-safe

// Use when: Fast key-value lookups, no ordering needed
```

**TreeMap:**
```java
Map<String, Integer> treeMap = new TreeMap<>();

// Characteristics:
// - Sorted by keys (natural or Comparator)
// - O(log n) operations
// - No null keys
// - Not thread-safe

// Use when: Sorted key order required
```

**LinkedHashMap:**
```java
Map<String, Integer> linkedHashMap = new LinkedHashMap<>();

// Characteristics:
// - Maintains insertion order
// - Slightly slower than HashMap
// - Can be used for LRU cache

// LRU Cache example:
Map<String, Integer> lruCache = new LinkedHashMap<>(16, 0.75f, true) {
    protected boolean removeEldestEntry(Map.Entry eldest) {
        return size() > 100;
    }
};
```

**ConcurrentHashMap:**
```java
Map<String, Integer> concurrentMap = new ConcurrentHashMap<>();

// Characteristics:
// - Thread-safe without locking entire map
// - Better performance than synchronized HashMap
// - No null keys or values
// - Weakly consistent iterators

// Use when: High concurrency scenarios
```

### Queue and Deque

```java
// Queue - FIFO
Queue<String> queue = new LinkedList<>();
queue.offer("first");
String head = queue.poll();
String peek = queue.peek();

// Deque - double-ended queue
Deque<String> deque = new ArrayDeque<>();
deque.offerFirst("front");
deque.offerLast("back");
String first = deque.pollFirst();
String last = deque.pollLast();

// PriorityQueue - orders by priority
Queue<Integer> priorityQueue = new PriorityQueue<>();
priorityQueue.offer(5);
priorityQueue.offer(1);
priorityQueue.offer(3);
int min = priorityQueue.poll(); // Returns 1
```

### Iteration

```java
List<String> list = Arrays.asList("A", "B", "C");

// Enhanced for loop
for (String item : list) {
    System.out.println(item);
}

// Iterator
Iterator<String> iterator = list.iterator();
while (iterator.hasNext()) {
    String item = iterator.next();
    // Can safely remove during iteration
    if (item.equals("B")) {
        iterator.remove();
    }
}

// ListIterator - bidirectional
ListIterator<String> listIterator = list.listIterator();
while (listIterator.hasNext()) {
    String item = listIterator.next();
    listIterator.set("Modified"); // Modify during iteration
}

// forEach method
list.forEach(System.out::println);
```

### Sorting and Searching

```java
// Comparable - natural ordering
public class Person implements Comparable<Person> {
    private String name;
    private int age;
    
    @Override
    public int compareTo(Person other) {
        return this.age - other.age;
    }
}

List<Person> people = new ArrayList<>();
Collections.sort(people); // Uses Comparable

// Comparator - custom ordering
Comparator<Person> byName = Comparator.comparing(Person::getName);
Comparator<Person> byAgeDesc = Comparator.comparing(Person::getAge).reversed();
Comparator<Person> complex = Comparator
    .comparing(Person::getAge)
    .thenComparing(Person::getName);

Collections.sort(people, byName);
people.sort(byAgeDesc); // Java 8+ list method

// Searching
int index = Collections.binarySearch(people, targetPerson, byName);
```

### Generics

```java
// Type parameters
public class Box<T> {
    private T content;
    
    public void set(T content) {
        this.content = content;
    }
    
    public T get() {
        return content;
    }
}

// Multiple type parameters
public class Pair<K, V> {
    private K key;
    private V value;
}

// Bounded type parameters
public class NumberBox<T extends Number> {
    private T number;
    
    public double doubleValue() {
        return number.doubleValue();
    }
}

// Wildcards
// ? extends T - upper bounded (read-only)
public void processNumbers(List<? extends Number> numbers) {
    for (Number n : numbers) {
        System.out.println(n.doubleValue());
    }
    // Cannot add to list (except null)
}

// ? super T - lower bounded (write-only)
public void addIntegers(List<? super Integer> list) {
    list.add(42);
    // Cannot read specific type from list
}

// Unbounded wildcard
public void printList(List<?> list) {
    for (Object item : list) {
        System.out.println(item);
    }
}

// Type Erasure - generics removed at runtime
// List<String> and List<Integer> become List
// Cannot do: new T(), T.class, instanceof T
```

---

## 3. Exception Handling

### Checked vs Unchecked Exceptions

```java
// Checked exceptions - must be caught or declared
public void readFile(String path) throws IOException {
    Files.readAllLines(Paths.get(path));
}

// Common checked exceptions:
// - IOException, SQLException, ClassNotFoundException

// Unchecked exceptions - not required to catch
public void divide(int a, int b) {
    int result = a / b; // Can throw ArithmeticException
}

// Common unchecked exceptions:
// - NullPointerException, IllegalArgumentException
// - IndexOutOfBoundsException, IllegalStateException

// When to use checked:
// - Recoverable conditions
// - Expected as part of normal operation
// - Caller should handle

// When to use unchecked:
// - Programming errors
// - Should not occur in normal operation
// - Recovery usually not possible
```

### Try-Catch-Finally

```java
public void processFile(String path) {
    FileInputStream fis = null;
    try {
        fis = new FileInputStream(path);
        // Process file
    } catch (FileNotFoundException e) {
        System.err.println("File not found: " + path);
        // Handle specific exception
    } catch (IOException e) {
        System.err.println("Error reading file");
        // Handle general exception
    } finally {
        // Always executed, even if exception occurs
        if (fis != null) {
            try {
                fis.close();
            } catch (IOException e) {
                // Handle close exception
            }
        }
    }
}
```

### Try-With-Resources

```java
// Automatic resource management for AutoCloseable
public void readFile(String path) throws IOException {
    try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        // reader.close() called automatically
    }
}

// Multiple resources
public void copyFile(String src, String dest) throws IOException {
    try (InputStream in = new FileInputStream(src);
         OutputStream out = new FileOutputStream(dest)) {
        byte[] buffer = new byte[1024];
        int length;
        while ((length = in.read(buffer)) > 0) {
            out.write(buffer, 0, length);
        }
    }
}

// Custom AutoCloseable
public class Connection implements AutoCloseable {
    @Override
    public void close() {
        System.out.println("Connection closed");
    }
}
```

### Custom Exceptions

```java
// Domain-specific exception
public class OrderNotFoundException extends RuntimeException {
    private final Long orderId;
    
    public OrderNotFoundException(Long orderId) {
        super("Order not found: " + orderId);
        this.orderId = orderId;
    }
    
    public Long getOrderId() {
        return orderId;
    }
}

// With additional context
public class ValidationException extends Exception {
    private final Map<String, String> errors;
    
    public ValidationException(Map<String, String> errors) {
        super("Validation failed");
        this.errors = errors;
    }
    
    public Map<String, String> getErrors() {
        return errors;
    }
}

// Usage in service
@Service
public class OrderService {
    public Order getOrder(Long id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException(id));
    }
}
```

### Exception Best Practices

```java
// 1. Don't swallow exceptions
try {
    // code
} catch (Exception e) {
    // BAD: Empty catch block
}

// 2. Catch specific exceptions first
try {
    // code
} catch (FileNotFoundException e) {
    // Handle specific case
} catch (IOException e) {
    // Handle general case
}

// 3. Include context in exception messages
throw new IllegalArgumentException(
    "Invalid age: " + age + ". Must be between 0 and 150"
);

// 4. Don't use exceptions for flow control
// BAD
try {
    return list.get(index);
} catch (IndexOutOfBoundsException e) {
    return null;
}

// GOOD
if (index >= 0 && index < list.size()) {
    return list.get(index);
}
return null;

// 5. Log before rethrowing
catch (Exception e) {
    logger.error("Failed to process order", e);
    throw new OrderProcessingException("Order processing failed", e);
}
```

### Multi-Catch Blocks

```java
// Handle multiple exception types the same way
try {
    // code that might throw multiple exceptions
} catch (IOException | SQLException e) {
    logger.error("Database or file error", e);
    throw new DataAccessException(e);
}

// Note: Exception variable is effectively final
// Cannot reassign e in the catch block
```

---

## 4. Multithreading and Concurrency

### Thread Creation

```java
// Method 1: Extending Thread
class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("Thread running: " + Thread.currentThread().getName());
    }
}

MyThread thread = new MyThread();
thread.start();

// Method 2: Implementing Runnable
class MyRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println("Runnable running");
    }
}

Thread thread = new Thread(new MyRunnable());
thread.start();

// Method 3: Lambda (most common)
Thread thread = new Thread(() -> {
    System.out.println("Lambda thread");
});
thread.start();

// Method 4: Callable (returns result)
Callable<Integer> task = () -> {
    return 42;
};

ExecutorService executor = Executors.newSingleThreadExecutor();
Future<Integer> future = executor.submit(task);
Integer result = future.get(); // Blocks until complete
```

### Thread Lifecycle

```java
// States: NEW -> RUNNABLE -> (BLOCKED/WAITING/TIMED_WAITING) -> TERMINATED

Thread thread = new Thread(() -> {
    try {
        Thread.sleep(1000); // TIMED_WAITING
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
});

Thread.State state = thread.getState(); // NEW
thread.start(); // RUNNABLE
thread.join(); // Wait for completion
state = thread.getState(); // TERMINATED
```

### Synchronization

```java
// Synchronized method
public class Counter {
    private int count = 0;
    
    public synchronized void increment() {
        count++;
    }
    
    public synchronized int getCount() {
        return count;
    }
}

// Synchronized block
public void increment() {
    synchronized(this) {
        count++;
    }
}

// Static synchronization
public class Counter {
    private static int count = 0;
    
    public static synchronized void increment() {
        count++;
    }
}

// Locks - more flexible than synchronized
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Counter {
    private int count = 0;
    private final Lock lock = new ReentrantLock();
    
    public void increment() {
        lock.lock();
        try {
            count++;
        } finally {
            lock.unlock();
        }
    }
    
    // Try lock with timeout
    public void safeIncrement() throws InterruptedException {
        if (lock.tryLock(1, TimeUnit.SECONDS)) {
            try {
                count++;
            } finally {
                lock.unlock();
            }
        }
    }
}

// Volatile - ensures visibility across threads
public class Flag {
    private volatile boolean running = true;
    
    public void stop() {
        running = false;
    }
    
    public void run() {
        while (running) {
            // do work
        }
    }
}
```

### Thread Safety

```java
// Immutable objects are thread-safe
public final class ImmutablePerson {
    private final String name;
    private final int age;
    
    public ImmutablePerson(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    public String getName() { return name; }
    public int getAge() { return age; }
}

// ThreadLocal - separate copy per thread
public class UserContext {
    private static final ThreadLocal<String> currentUser = 
        ThreadLocal.withInitial(() -> "guest");
    
    public static String getCurrentUser() {
        return currentUser.get();
    }
    
    public static void setCurrentUser(String user) {
        currentUser.set(user);
    }
    
    public static void clear() {
        currentUser.remove();
    }
}
```

### ExecutorService

```java
// Fixed thread pool
ExecutorService executor = Executors.newFixedThreadPool(4);

// Submit tasks
executor.submit(() -> System.out.println("Task 1"));
executor.submit(() -> System.out.println("Task 2"));

// Execute (no return value)
executor.execute(() -> System.out.println("Fire and forget"));

// Submit with result
Future<Integer> future = executor.submit(() -> {
    Thread.sleep(1000);
    return 42;
});

Integer result = future.get(); // Blocking
Integer result = future.get(2, TimeUnit.SECONDS); // With timeout

// Shutdown
executor.shutdown(); // No new tasks accepted
executor.awaitTermination(1, TimeUnit.MINUTES);
executor.shutdownNow(); // Interrupt running tasks

// Single thread executor
ExecutorService single = Executors.newSingleThreadExecutor();

// Cached thread pool - creates threads as needed
ExecutorService cached = Executors.newCachedThreadPool();

// Scheduled executor
ScheduledExecutorService scheduler = 
    Executors.newScheduledThreadPool(2);
scheduler.schedule(() -> System.out.println("Delayed"), 5, TimeUnit.SECONDS);
scheduler.scheduleAtFixedRate(() -> 
    System.out.println("Periodic"), 0, 1, TimeUnit.SECONDS);
```

### Concurrent Collections

```java
// ConcurrentHashMap
Map<String, Integer> map = new ConcurrentHashMap<>();
map.put("key", 1);
map.computeIfAbsent("key", k -> expensiveComputation());
map.merge("key", 1, Integer::sum); // Atomic increment

// CopyOnWriteArrayList - expensive writes, cheap reads
List<String> list = new CopyOnWriteArrayList<>();
list.add("item"); // Creates new copy of array

// BlockingQueue - producer-consumer
BlockingQueue<String> queue = new LinkedBlockingQueue<>();

// Producer
new Thread(() -> {
    try {
        queue.put("item"); // Blocks if queue is full
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
}).start();

// Consumer
new Thread(() -> {
    try {
        String item = queue.take(); // Blocks if queue is empty
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
}).start();

// ConcurrentSkipListMap - concurrent sorted map
NavigableMap<String, Integer> sortedMap = new ConcurrentSkipListMap<>();
```

### CompletableFuture

```java
// Asynchronous computation
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
    // Runs in ForkJoinPool.commonPool()
    return "Hello";
});

// Chaining operations
CompletableFuture<String> result = future
    .thenApply(s -> s + " World")
    .thenApply(String::toUpperCase);

// Combining futures
CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> 10);
CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> 20);

CompletableFuture<Integer> combined = future1.thenCombine(future2, (a, b) -> a + b);

// Exception handling
CompletableFuture<String> withError = CompletableFuture
    .supplyAsync(() -> {
        if (true) throw new RuntimeException("Error");
        return "Success";
    })
    .exceptionally(ex -> "Recovered from: " + ex.getMessage())
    .thenApply(String::toUpperCase);

// All of / Any of
CompletableFuture<Void> all = CompletableFuture.allOf(future1, future2);
CompletableFuture<Object> any = CompletableFuture.anyOf(future1, future2);

// Timeout
CompletableFuture<String> withTimeout = future
    .orTimeout(5, TimeUnit.SECONDS);
```

### Deadlocks

```java
// Deadlock example
Object lock1 = new Object();
Object lock2 = new Object();

Thread t1 = new Thread(() -> {
    synchronized(lock1) {
        System.out.println("Thread 1: locked lock1");
        Thread.sleep(100);
        synchronized(lock2) {
            System.out.println("Thread 1: locked lock2");
        }
    }
});

Thread t2 = new Thread(() -> {
    synchronized(lock2) {
        System.out.println("Thread 2: locked lock2");
        Thread.sleep(100);
        synchronized(lock1) {
            System.out.println("Thread 2: locked lock1");
        }
    }
});

// Prevention strategies:
// 1. Lock ordering - always acquire locks in same order
synchronized(getLock(id1, id2)) { // Lower ID first
    synchronized(getLock(id2, id1)) {
        // work
    }
}

// 2. Lock timeout
if (lock.tryLock(1, TimeUnit.SECONDS)) {
    try {
        // work
    } finally {
        lock.unlock();
    }
}

// 3. Avoid nested locks when possible
```

---

## 5. JVM Internals

### Memory Structure

```java
// Heap - object instances and arrays
Person person = new Person(); // Allocated on heap

// Stack - method frames, local variables, references
public void method() {
    int x = 5; // Primitive on stack
    Person p = new Person(); // Reference on stack, object on heap
}

// Method Area (Metaspace in Java 8+)
// - Class metadata, static variables, constant pool

// String Pool - special heap area for string literals
String s1 = "hello"; // In string pool
String s2 = "hello"; // Points to same pool instance
String s3 = new String("hello"); // New object on heap
String s4 = s3.intern(); // Adds to pool if not exists
```

### Garbage Collection

```java
// Types of GC:
// 1. Serial GC - single thread
// -XX:+UseSerialGC

// 2. Parallel GC - multiple threads (default in Java 8)
// -XX:+UseParallelGC

// 3. G1 GC - region-based, low pause (default in Java 9+)
// -XX:+UseG1GC

// 4. ZGC - ultra-low pause times
// -XX:+UseZGC

// GC Roots:
// - Local variables in active threads
// - Static variables
// - JNI references

// Example: Object eligible for GC
public void method() {
    Person p = new Person();
    p = null; // Original object now eligible for GC
}

// Finalization (deprecated)
@Override
protected void finalize() throws Throwable {
    // Called before GC (unreliable)
    super.finalize();
}

// Better: Use try-with-resources or explicit cleanup
```

### JVM Arguments

```java
// Heap size
// -Xms512m (initial heap)
// -Xmx2g (maximum heap)

// GC logging
// -Xlog:gc* (Java 9+)
// -XX:+PrintGCDetails (Java 8)

// Thread stack size
// -Xss256k

// Metaspace (Java 8+)
// -XX:MetaspaceSize=128m
// -XX:MaxMetaspaceSize=512m

// GC tuning
// -XX:MaxGCPauseMillis=200 (G1 pause time goal)
// -XX:G1HeapRegionSize=16m

// Example application startup:
// java -Xms1g -Xmx4g -XX:+UseG1GC -XX:MaxGCPauseMillis=100 -jar app.jar
```

### ClassLoader

```java
// Hierarchy:
// Bootstrap ClassLoader (JDK classes)
//   ??? Extension ClassLoader (lib/ext)
//       ??? Application ClassLoader (classpath)
//           ??? Custom ClassLoader

// Delegation model: child asks parent first

// Get class loader
ClassLoader cl = MyClass.class.getClassLoader();

// Load class
Class<?> clazz = cl.loadClass("com.example.MyClass");

// Custom class loader
public class MyClassLoader extends ClassLoader {
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] classBytes = loadClassBytes(name);
        return defineClass(name, classBytes, 0, classBytes.length);
    }
    
    private byte[] loadClassBytes(String name) {
        // Load from custom source (database, network, etc.)
        return null;
    }
}
```

### Memory Leaks

```java
// Common causes:

// 1. Unclosed resources
public void readFile() {
    FileInputStream fis = new FileInputStream("file.txt");
    // Missing fis.close() - file handle leaked
}

// 2. Static collections
public class Cache {
    private static Map<String, Object> cache = new HashMap<>();
    
    public void add(String key, Object value) {
        cache.put(key, value); // Never removed
    }
}

// 3. Listeners not removed
button.addActionListener(listener);
// Missing: button.removeActionListener(listener)

// 4. ThreadLocal not cleaned
ThreadLocal<Connection> threadLocal = new ThreadLocal<>();
threadLocal.set(connection);
// Missing: threadLocal.remove()

// Detection:
// - Use heap dumps (jmap -dump)
// - Analyze with tools (VisualVM, Eclipse MAT)
// - Monitor with JConsole/JVisualVM

// Prevention:
// - Use try-with-resources
// - Implement proper cleanup methods
// - Use weak references for caches
// - Profile regularly
```

---

## 6. String Handling

### String Immutability

```java
// Strings are immutable - once created, cannot be changed
String s1 = "hello";
String s2 = s1.toUpperCase(); // Creates new string
System.out.println(s1); // Still "hello"

// Why immutable:
// - Thread-safe without synchronization
// - Hashcode can be cached
// - Security (can't modify passed strings)
// - String pool optimization

// Implications:
String result = "";
for (int i = 0; i < 1000; i++) {
    result += i; // Creates 1000 new String objects - inefficient!
}
```

### String vs StringBuilder vs StringBuffer

```java
// String - immutable
String s = "hello";
s = s + " world"; // New object created

// StringBuilder - mutable, not thread-safe (fastest)
StringBuilder sb = new StringBuilder();
sb.append("hello");
sb.append(" world");
String result = sb.toString();

// StringBuffer - mutable, thread-safe (slower)
StringBuffer buffer = new StringBuffer();
buffer.append("hello"); // Synchronized
buffer.append(" world");

// Performance comparison:
// String: Use for small, fixed text
// StringBuilder: Use for string building in single thread
// StringBuffer: Use for string building in multi-threaded context

// Example: Building SQL query
StringBuilder query = new StringBuilder();
query.append("SELECT * FROM users WHERE ");
query.append("age > ").append(18);
query.append(" AND country = '").append("US").append("'");
```

### String Pool

```java
// String literals go to pool
String s1 = "hello";
String s2 = "hello";
System.out.println(s1 == s2); // true - same object

// new String() creates heap object
String s3 = new String("hello");
System.out.println(s1 == s3); // false - different objects
System.out.println(s1.equals(s3)); // true - same content

// intern() adds to pool or returns existing
String s4 = s3.intern();
System.out.println(s1 == s4); // true - same pool object

// Concatenation at compile time
String s5 = "hel" + "lo"; // Compiled to "hello"
System.out.println(s1 == s5); // true

// Runtime concatenation creates new object
String part = "lo";
String s6 = "hel" + part; // Runtime concatenation
System.out.println(s1 == s6); // false
```

### String Methods

```java
String str = "Hello World";

// Length and character access
int length = str.length(); // 11
char ch = str.charAt(0); // 'H'

// Substring
String sub = str.substring(0, 5); // "Hello"
String sub2 = str.substring(6); // "World"

// Search
int index = str.indexOf("World"); // 6
boolean contains = str.contains("World"); // true
boolean starts = str.startsWith("Hello"); // true
boolean ends = str.endsWith("World"); // true

// Modification (returns new string)
String upper = str.toUpperCase(); // "HELLO WORLD"
String lower = str.toLowerCase(); // "hello world"
String trimmed = "  hello  ".trim(); // "hello"
String replaced = str.replace("World", "Java"); // "Hello Java"

// Split
String csv = "a,b,c,d";
String[] parts = csv.split(","); // ["a", "b", "c", "d"]

// Join
String joined = String.join("-", "a", "b", "c"); // "a-b-c"
List<String> list = Arrays.asList("a", "b", "c");
String joined2 = String.join(",", list); // "a,b,c"

// Format
String formatted = String.format("Name: %s, Age: %d", "John", 30);

// Comparison
boolean equals = str.equals("Hello World");
boolean equalsIgnore = str.equalsIgnoreCase("hello world");
int comparison = str.compareTo("Hello Java"); // Positive if greater

// Empty checks
boolean isEmpty = "".isEmpty(); // true
boolean isBlank = "  ".isBlank(); // true (Java 11+)
```

---

## 7. Input/Output

### Streams

```java
// Byte streams
try (FileInputStream fis = new FileInputStream("input.txt");
     FileOutputStream fos = new FileOutputStream("output.txt")) {
    int data;
    while ((data = fis.read()) != -1) {
        fos.write(data);
    }
}

// Character streams
try (FileReader reader = new FileReader("input.txt");
     FileWriter writer = new FileWriter("output.txt")) {
    int data;
    while ((data = reader.read()) != -1) {
        writer.write(data);
    }
}

// Buffered streams (more efficient)
try (BufferedReader br = new BufferedReader(new FileReader("input.txt"));
     BufferedWriter bw = new BufferedWriter(new FileWriter("output.txt"))) {
    String line;
    while ((line = br.readLine()) != null) {
        bw.write(line);
        bw.newLine();
    }
}
```

### File Operations (NIO.2)

```java
// Path - file or directory location
Path path = Paths.get("folder", "file.txt");
Path absolute = path.toAbsolutePath();
Path parent = path.getParent();
String fileName = path.getFileName().toString();

// Files class - static utility methods

// Read entire file
List<String> lines = Files.readAllLines(path);
String content = Files.readString(path);

// Write to file
Files.writeString(path, "content");
Files.write(path, lines);

// Copy, move, delete
Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
Files.move(source, target);
Files.delete(path);
Files.deleteIfExists(path);

// Check existence and properties
boolean exists = Files.exists(path);
boolean isDirectory = Files.isDirectory(path);
boolean isReadable = Files.isReadable(path);
long size = Files.size(path);

// Create directories
Files.createDirectory(path);
Files.createDirectories(path); // Creates parent dirs too

// List directory contents
try (Stream<Path> paths = Files.list(directory)) {
    paths.forEach(System.out::println);
}

// Walk file tree
try (Stream<Path> paths = Files.walk(rootPath)) {
    paths.filter(Files::isRegularFile)
         .forEach(System.out::println);
}

// Watch for file changes
WatchService watcher = FileSystems.getDefault().newWatchService();
path.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
```

### Serialization

```java
// Serializable interface
public class Person implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private int age;
    private transient String password; // Not serialized
    
    // Constructors, getters, setters
}

// Writing object
try (ObjectOutputStream oos = new ObjectOutputStream(
        new FileOutputStream("person.ser"))) {
    Person person = new Person("John", 30);
    oos.writeObject(person);
}

// Reading object
try (ObjectInputStream ois = new ObjectInputStream(
        new FileInputStream("person.ser"))) {
    Person person = (Person) ois.readObject();
}

// Custom serialization
private void writeObject(ObjectOutputStream oos) throws IOException {
    oos.defaultWriteObject();
    oos.writeObject(encrypt(password));
}

private void readObject(ObjectInputStream ois) 
        throws IOException, ClassNotFoundException {
    ois.defaultReadObject();
    password = decrypt((String) ois.readObject());
}

// Alternative: Externalizable for full control
public class Person implements Externalizable {
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(name);
        out.writeInt(age);
    }
    
    @Override
    public void readExternal(ObjectInput in) 
            throws IOException, ClassNotFoundException {
        name = (String) in.readObject();
        age = in.readInt();
    }
}
```

---

## 8. Reflection and Annotations

### Reflection API

```java
// Get Class object
Class<?> clazz1 = Person.class;
Class<?> clazz2 = person.getClass();
Class<?> clazz3 = Class.forName("com.example.Person");

// Get class information
String name = clazz.getName();
String simpleName = clazz.getSimpleName();
Package pkg = clazz.getPackage();

// Get fields
Field[] fields = clazz.getDeclaredFields();
Field nameField = clazz.getDeclaredField("name");
nameField.setAccessible(true); // Access private field
String value = (String) nameField.get(person);
nameField.set(person, "New Name");

// Get methods
Method[] methods = clazz.getDeclaredMethods();
Method setName = clazz.getMethod("setName", String.class);
setName.invoke(person, "John");

// Get constructors
Constructor<?>[] constructors = clazz.getDeclaredConstructors();
Constructor<?> constructor = clazz.getConstructor(String.class, int.class);
Object newPerson = constructor.newInstance("Jane", 25);

// Check modifiers
if (Modifier.isPublic(field.getModifiers())) {
    // Field is public
}

// Practical example: Generic mapper
public class ObjectMapper {
    public <T> T map(Map<String, Object> data, Class<T> clazz) 
            throws Exception {
        T instance = clazz.getDeclaredConstructor().newInstance();
        
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            Object value = data.get(field.getName());
            if (value != null) {
                field.set(instance, value);
            }
        }
        
        return instance;
    }
}
```

### Custom Annotations

```java
// Define annotation
@Retention(RetentionPolicy.RUNTIME) // Available at runtime
@Target(ElementType.FIELD) // Can be applied to fields
public @interface ValidateEmail {
    String message() default "Invalid email";
}

// Use annotation
public class User {
    @ValidateEmail(message = "Email format is invalid")
    private String email;
}

// Process annotation
public class Validator {
    public void validate(Object obj) throws Exception {
        Class<?> clazz = obj.getClass();
        
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(ValidateEmail.class)) {
                ValidateEmail annotation = 
                    field.getAnnotation(ValidateEmail.class);
                field.setAccessible(true);
                String email = (String) field.get(obj);
                
                if (!isValidEmail(email)) {
                    throw new ValidationException(annotation.message());
                }
            }
        }
    }
    
    private boolean isValidEmail(String email) {
        return email != null && email.contains("@");
    }
}

// Multiple targets
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Secured {
    String[] roles() default {};
}

// Method annotation
@Secured(roles = {"ADMIN", "USER"})
public void deleteUser(Long id) {
    // Implementation
}
```

### Annotation Processing

```java
// Runtime processing (like above with reflection)

// Compile-time processing
@SupportedAnnotationTypes("com.example.Entity")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class EntityProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                          RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(Entity.class)) {
            // Generate code, validate, etc.
            generateRepositoryClass(element);
        }
        return true;
    }
    
    private void generateRepositoryClass(Element element) {
        // Code generation logic
    }
}
```

---

## Summary

This comprehensive guide covers all essential Core Java Knowledge requirements for a medior Java developer, including:

- Modern Java 8+ features (lambdas, streams, Optional)
- Collections framework with performance characteristics
- Exception handling best practices
- Multithreading and concurrency utilities
- JVM internals and memory management
- String handling and optimization
- File I/O operations
- Reflection and annotations

Each topic includes practical examples relevant to Spring Boot development and real-world applications.

---

**Created for Technical Interview Preparation**  
*Last Updated: January 2026*

