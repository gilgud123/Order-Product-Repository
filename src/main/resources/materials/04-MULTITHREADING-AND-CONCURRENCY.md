# Multithreading & Concurrency - Complete Guide

## Overview
A practical guide to Java multithreading and concurrency, including core concepts, APIs, patterns, common pitfalls, and answers to typical interview questions.

---

## 1) Thread Basics

### Summary
- A Thread represents a unit of execution.
- Create threads via Thread class or ExecutorService.
- Avoid manual thread management when possible.

### Examples
```java
// Creating and starting a thread
Thread t = new Thread(() -> System.out.println("Running on " + Thread.currentThread().getName()));
t.start();

// Subclassing Thread (less preferred)
class Worker extends Thread {
    @Override public void run() { System.out.println("Work"); }
}
new Worker().start();
```

### Key Points
- start() creates a new thread and invokes run() asynchronously.
- run() directly executes in current thread—does NOT start a new thread.
- Prefer ExecutorService for pooling and lifecycle control.

---

## 2) Synchronization & Locks

### Summary
- Synchronization ensures mutual exclusion and visibility of shared state.
- Tools: synchronized, ReentrantLock, ReadWriteLock.
- Minimize critical sections to reduce contention.

### Examples
```java
// synchronized method
public synchronized void increment() { count++; }

// synchronized block with explicit lock object
private final Object lock = new Object();
void add(int x) {
    synchronized (lock) {
        total += x;
    }
}

// ReentrantLock
private final ReentrantLock rl = new ReentrantLock();
void safe() {
    rl.lock();
    try {
        // critical section
    } finally {
        rl.unlock();
    }
}

// ReadWriteLock: many readers, single writer
ReadWriteLock rw = new ReentrantReadWriteLock();
rw.readLock().lock();
try { /* read only */ } finally { rw.readLock().unlock(); }
rw.writeLock().lock();
try { /* mutate */ } finally { rw.writeLock().unlock(); }
```

### Visibility & Happens-Before
- synchronized blocks and lock operations establish happens-before relationships.
- Volatile guarantees visibility and ordering for single writes/reads.

---

## 3) The volatile Keyword

### Summary
- Ensures visibility of changes to variables across threads.
- Prevents instruction reordering for that variable.
- Does NOT provide atomicity for compound operations.

### Examples
```java
volatile boolean running = true;

// Thread A
while (running) { /* do work */ }

// Thread B
running = false; // Visible to Thread A promptly
```

### When to Use
- Flags, configuration switches.
- Avoid for counters or state that needs atomic updates—use Atomic* classes.

---

## 4) Atomic Variables & Adders

### Summary
- Atomic classes provide lock-free thread-safe operations using CAS.
- Common: AtomicInteger, AtomicLong, AtomicReference, LongAdder/DoubleAdder.

### Examples
```java
AtomicInteger counter = new AtomicInteger(0);
int next = counter.incrementAndGet(); // atomic

AtomicReference<String> ref = new AtomicReference<>("init");
ref.compareAndSet("init", "updated");

// High-contention counters
LongAdder adder = new LongAdder();
adder.increment();
long sum = adder.sum();
```

### Guidance
- Use LongAdder for hot counters under heavy contention.
- AtomicReference for swapping references atomically.

---

## 5) Executor Framework

### Summary
- Abstracts thread management via thread pools.
- Key types: Executor, ExecutorService, ScheduledExecutorService.
- Factory: Executors.newFixedThreadPool, newCachedThreadPool, newSingleThreadExecutor, newScheduledThreadPool.

### Examples
```java
ExecutorService pool = Executors.newFixedThreadPool(4);
Future<Integer> f = pool.submit(() -> 42);
Integer val = f.get(); // blocks until done
pool.shutdown(); // graceful shutdown

// Scheduled tasks
ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
scheduler.scheduleAtFixedRate(() -> doWork(), 0, 1, TimeUnit.MINUTES);
```

### Best Practices
- Always shutdown executors.
- Use bounded queues/thread pools to prevent resource exhaustion.
- Prefer ThreadPoolExecutor for fine-grained control.

---

## 6) Futures & CompletableFuture

### Summary
- Future: handle async results, get() blocks.
- CompletableFuture: composition, callbacks, error handling, non-blocking continuations.

### Examples
```java
CompletableFuture<Integer> cf = CompletableFuture.supplyAsync(() -> compute());
CompletableFuture<String> res = cf
    .thenApply(Object::toString)
    .exceptionally(ex -> "error:" + ex.getMessage());

// Compose dependent async tasks
CompletableFuture<User> user = CompletableFuture.supplyAsync(() -> loadUser(id));
CompletableFuture<List<Order>> orders = user.thenCompose(u -> CompletableFuture.supplyAsync(() -> loadOrders(u)));

// Combine independent tasks
CompletableFuture<Integer> a = CompletableFuture.supplyAsync(() -> 10);
CompletableFuture<Integer> b = CompletableFuture.supplyAsync(() -> 20);
CompletableFuture<Integer> sum = a.thenCombine(b, Integer::sum);
```

### Error Handling
- exceptionally: provide fallback
- handle: process result or exception
- whenComplete: side-effects after completion

---

## 7) Synchronizers

### Summary
- Utilities for coordinating threads: CountDownLatch, CyclicBarrier, Semaphore, Phaser, Exchanger.

### Examples
```java
// CountDownLatch: wait for N events
CountDownLatch latch = new CountDownLatch(3);
new Thread(() -> { doTask(); latch.countDown(); }).start();
latch.await(); // waits until count reaches zero

// CyclicBarrier: wait for all parties, then proceed
CyclicBarrier barrier = new CyclicBarrier(3, () -> System.out.println("All ready"));
for (int i = 0; i < 3; i++) {
    new Thread(() -> { prepare(); await(barrier); proceed(); }).start();
}

// Semaphore: limit concurrent access
Semaphore sem = new Semaphore(5); // max 5 permits
sem.acquire();
try { accessResource(); } finally { sem.release(); }
```

---

## 8) Concurrent Collections

### Summary
- Thread-safe collections designed for high concurrency.
- Common: ConcurrentHashMap, ConcurrentLinkedQueue/Deque, CopyOnWriteArrayList/Set, BlockingQueue implementations.

### Examples
```java
ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
map.merge("k", 1, Integer::sum);
map.compute("k", (k, v) -> v == null ? 1 : v + 1);

BlockingQueue<Task> queue = new ArrayBlockingQueue<>(100);
queue.put(new Task()); // blocks if full
Task t = queue.take(); // blocks if empty

CopyOnWriteArrayList<String> cow = new CopyOnWriteArrayList<>();
cow.add("A"); // copy-on-write; good for read-heavy scenarios
```

### Guidance
- Prefer concurrent collections over synchronized wrappers for scalability.
- Avoid CopyOnWrite for write-heavy workloads.

---

## 9) Thread Coordination (wait/notify & Conditions)

### Summary
- Low-level communication via Object.wait/notify/notifyAll.
- Prefer higher-level abstractions (BlockingQueue, Latch) but know basics.

### Examples
```java
class BoundedBuffer<T> {
    private final Queue<T> q = new ArrayDeque<>();
    private final int cap;
    BoundedBuffer(int cap) { this.cap = cap; }

    public synchronized void put(T item) throws InterruptedException {
        while (q.size() == cap) { wait(); }
        q.add(item);
        notifyAll();
    }

    public synchronized T take() throws InterruptedException {
        while (q.isEmpty()) { wait(); }
        T item = q.remove();
        notifyAll();
        return item;
    }
}
```

### Conditions (java.util.concurrent.locks.Condition)
```java
Lock lock = new ReentrantLock();
Condition notEmpty = lock.newCondition();
Condition notFull = lock.newCondition();
```

---

## 10) Immutability & Thread-Safety

### Summary
- Immutable objects are inherently thread-safe.
- Favor final fields, defensive copies, and builders.

### Examples
```java
final class Point {
    private final int x, y;
    Point(int x, int y) { this.x = x; this.y = y; }
    int x() { return x; }
    int y() { return y; }
}
```

### Guidance
- Reduce shared mutable state.
- Prefer message passing and immutability for simpler concurrency models.

---

## 11) Deadlocks, Livelocks, Starvation

### Summary
- Deadlock: threads wait forever due to circular lock dependencies.
- Livelock: threads actively change state but make no progress.
- Starvation: thread never gets CPU or resources.

### Prevention
- Consistent lock ordering.
- Timeout-based locking (tryLock).
- Minimize lock scope and granularity.

---

## 12) Thread States & Lifecycle

### Summary
- NEW, RUNNABLE, BLOCKED, WAITING, TIMED_WAITING, TERMINATED.
- State transitions occur via start, synchronized blocks, wait/notify, sleep, join.

### Example
```java
Thread t = new Thread(() -> { /* work */ });
System.out.println(t.getState()); // NEW
t.start();
System.out.println(t.getState()); // RUNNABLE or other
```

---

## 13) Performance & Tuning

### Summary
- Use profiling, thread dumps, and metrics.
- Avoid false sharing; consider padding for AtomicLong (LongAdder is better).
- Use ForkJoinPool for divide-and-conquer (parallel streams use common pool).

### Tips
- Measure before optimizing.
- Avoid contention hot spots.
- Right-size thread pools to CPUs and workload.

---

## Common Interview Questions & Answers

### Q1: Difference between start() and run()
- start(): creates a new thread and invokes run() asynchronously.
- run(): executes method in the current thread; no new thread is started.

### Q2: What is a race condition? How to prevent it?
- Race condition occurs when multiple threads access and modify shared data concurrently, leading to inconsistent results.
- Prevention: synchronization (locks), atomic variables, avoiding shared mutable state, using thread-safe collections.

### Q3: What is deadlock and how do you avoid it?
- Deadlock: two or more threads block each other, each waiting for a resource the other holds.
- Avoid: consistent lock ordering, tryLock with timeouts, reduce lock scope, detect with thread dumps and tools.

### Q4: synchronized vs ReentrantLock
- synchronized: intrinsic lock, simpler, automatic release, monitors wait/notify.
- ReentrantLock: explicit lock with features: tryLock, lockInterruptibly, fairness, multiple Conditions.
- Choose ReentrantLock for advanced control or when timeouts/fairness needed.

### Q5: volatile keyword usage
- Ensures visibility and ordering for single variable reads/writes.
- Not atomic for compound operations (x = x + 1). Use Atomic* classes for counters.

### Q6: Thread states
- NEW, RUNNABLE, BLOCKED, WAITING, TIMED_WAITING, TERMINATED; know transitions using start, join, sleep, wait/notify.

### Q7: ExecutorService vs Thread
- ExecutorService manages thread pools, task submission via Runnable/Callable, lifecycle control (shutdown). Prefer over manual threads for scalability.

### Q8: What is a Future vs CompletableFuture?
- Future: represents async result; limited composition; blocking get().
- CompletableFuture: rich async composition, callbacks, error handling, non-blocking chaining.

### Q9: Producer-Consumer pattern
- Use BlockingQueue; producers put(), consumers take(); avoids busy-waiting and manual synchronization.

### Q10: What is the Fork/Join framework?
- Work-stealing pool for recursive divide-and-conquer tasks; backing for parallel streams via common ForkJoinPool.

### Q11: What is false sharing?
- Performance degradation when threads modify variables that reside on the same CPU cache line; mitigate by padding/isolating state.

### Q12: When to use parallel streams?
- Large CPU-bound data sets, minimal synchronization, stateless operations. Benchmark; avoid I/O-bound tasks and shared mutable state.

---

## Best Practices Checklist
- Prefer ExecutorService over raw threads.
- Limit shared mutable state; use immutable data.
- Use concurrent collections and atomic variables.
- Keep critical sections small; avoid nested locks.
- Use timeouts (tryLock), and detect deadlocks with dumps/tools.
- Always shutdown executors; handle interruptions properly.
- Avoid blocking in CompletableFuture chains; use asynchronous stages.
- Benchmark parallelism; measure and tune pool sizes.

---

## Quick Reference
- synchronized: mutual exclusion + visibility.
- volatile: visibility only, no atomicity.
- AtomicInteger/LongAdder: lock-free counters.
- ExecutorService: task submission + pooling.
- CompletableFuture: async composition.
- BlockingQueue: producer-consumer.
- CountDownLatch/CyclicBarrier/Semaphore: coordination.
- ConcurrentHashMap/CopyOnWriteArrayList: thread-safe collections.

---

*Last Updated: January 2026*

