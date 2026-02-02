# Collections Framework - Complete Guide

## Overview
The Java Collections Framework provides a unified architecture for storing and manipulating groups of objects. It includes interfaces, implementations, and algorithms to work with collections efficiently.

---

## Collection Hierarchy

```
Collection (Interface)
??? List (Interface) - Ordered, allows duplicates
?   ??? ArrayList - Resizable array implementation
?   ??? LinkedList - Doubly-linked list implementation
?   ??? Vector - Synchronized ArrayList (legacy)
??? Set (Interface) - No duplicates
?   ??? HashSet - Hash table implementation
?   ??? LinkedHashSet - Hash table + linked list
?   ??? TreeSet - Red-black tree (sorted)
??? Queue (Interface) - FIFO operations
    ??? PriorityQueue - Heap-based priority queue
    ??? Deque - Double-ended queue

Map (Interface) - Key-value pairs
??? HashMap - Hash table implementation
??? LinkedHashMap - Hash table + linked list (insertion order)
??? TreeMap - Red-black tree (sorted by keys)
??? Hashtable - Synchronized HashMap (legacy)
??? ConcurrentHashMap - Thread-safe HashMap
```

---

## List Interface

### ArrayList
**Summary:**
- Backed by a resizable array
- Fast random access: O(1)
- Slow insertions/deletions in middle: O(n)
- Not synchronized (not thread-safe)
- Allows null elements
- Default initial capacity: 10
- Grows by 50% when full

**When to Use:**
- Frequent access by index
- More reads than writes
- Adding elements at the end

**Example:**
```java
List<String> arrayList = new ArrayList<>();
arrayList.add("Apple");           // O(1) amortized
arrayList.get(0);                 // O(1)
arrayList.add(1, "Banana");       // O(n) - shifts elements
arrayList.remove(0);              // O(n) - shifts elements
```

**Time Complexity:**
- `get(index)`: O(1)
- `add(element)`: O(1) amortized
- `add(index, element)`: O(n)
- `remove(index)`: O(n)
- `contains(element)`: O(n)
- `size()`: O(1)

---

### LinkedList
**Summary:**
- Implements both List and Deque interfaces
- Doubly-linked list structure
- Fast insertions/deletions: O(1) at known positions
- Slow random access: O(n)
- Not synchronized
- Higher memory overhead (stores node references)

**When to Use:**
- Frequent insertions/deletions in middle
- Queue/Stack operations (Deque)
- No need for random access

**Example:**
```java
LinkedList<String> linkedList = new LinkedList<>();
linkedList.add("First");
linkedList.addFirst("New First");  // O(1)
linkedList.addLast("Last");        // O(1)
linkedList.get(5);                 // O(n) - must traverse
linkedList.remove();               // O(1) - removes first
```

**Time Complexity:**
- `get(index)`: O(n)
- `add(element)`: O(1)
- `add(index, element)`: O(n) - need to find position
- `addFirst(element)`: O(1)
- `addLast(element)`: O(1)
- `removeFirst()`: O(1)
- `removeLast()`: O(1)

---

## Set Interface

### HashSet
**Summary:**
- Backed by HashMap internally
- No duplicates allowed
- No guaranteed order
- Allows one null element
- Not synchronized
- Best performance for basic operations

**When to Use:**
- Need to eliminate duplicates
- Order doesn't matter
- Fast lookups required

**Example:**
```java
Set<String> hashSet = new HashSet<>();
hashSet.add("Apple");              // O(1)
hashSet.add("Apple");              // Returns false, no duplicate
hashSet.contains("Apple");         // O(1)
hashSet.remove("Apple");           // O(1)
```

**Time Complexity:**
- `add(element)`: O(1) average
- `remove(element)`: O(1) average
- `contains(element)`: O(1) average
- `size()`: O(1)

**Internal Working:**
- Uses hashCode() to determine bucket
- Uses equals() to check for duplicates
- Load factor: 0.75 (default)
- Initial capacity: 16 (default)

---

### TreeSet
**Summary:**
- Backed by TreeMap (Red-Black tree)
- Elements stored in sorted order (natural or custom)
- No duplicates
- Does NOT allow null (since Java 7)
- Not synchronized
- Slower than HashSet due to sorting

**When to Use:**
- Need sorted elements
- Range operations required
- Navigable operations (floor, ceiling, higher, lower)

**Example:**
```java
Set<Integer> treeSet = new TreeSet<>();
treeSet.add(5);
treeSet.add(1);
treeSet.add(3);
// Iteration order: 1, 3, 5 (sorted)

// With custom comparator
Set<String> customSet = new TreeSet<>(Comparator.reverseOrder());

// Navigable operations
NavigableSet<Integer> navSet = new TreeSet<>(Arrays.asList(1, 3, 5, 7, 9));
navSet.lower(5);    // Returns 3
navSet.floor(5);    // Returns 5
navSet.ceiling(6);  // Returns 7
navSet.higher(5);   // Returns 7
```

**Time Complexity:**
- `add(element)`: O(log n)
- `remove(element)`: O(log n)
- `contains(element)`: O(log n)
- `size()`: O(1)

---

### LinkedHashSet
**Summary:**
- Extends HashSet
- Maintains insertion order
- Hash table + doubly-linked list
- Slightly slower than HashSet
- Predictable iteration order

**When to Use:**
- Need unique elements
- Insertion order matters
- Compromise between HashSet and TreeSet

**Example:**
```java
Set<String> linkedHashSet = new LinkedHashSet<>();
linkedHashSet.add("Banana");
linkedHashSet.add("Apple");
linkedHashSet.add("Cherry");
// Iteration order: Banana, Apple, Cherry (insertion order)
```

---

## Map Interface

### HashMap
**Summary:**
- Stores key-value pairs
- Uses hashing for keys
- Allows one null key and multiple null values
- No guaranteed order
- Not synchronized
- Best performance for general use

**When to Use:**
- Fast key-value lookups
- Order doesn't matter
- Single-threaded environment

**Example:**
```java
Map<String, Integer> map = new HashMap<>();
map.put("Apple", 10);              // O(1)
map.get("Apple");                  // O(1) - Returns 10
map.containsKey("Apple");          // O(1)
map.remove("Apple");               // O(1)
map.putIfAbsent("Banana", 5);     // Only adds if key absent

// Iteration
for (Map.Entry<String, Integer> entry : map.entrySet()) {
    System.out.println(entry.getKey() + ": " + entry.getValue());
}

// Java 8+ methods
map.forEach((key, value) -> System.out.println(key + ": " + value));
map.computeIfAbsent("Cherry", k -> 15);
map.merge("Apple", 5, Integer::sum); // Adds 5 to existing value
```

**Time Complexity:**
- `put(key, value)`: O(1) average
- `get(key)`: O(1) average
- `remove(key)`: O(1) average
- `containsKey(key)`: O(1) average

**Internal Working:**
- Array of buckets (each bucket is a linked list or tree)
- Hash collision handling: Linked list ? Red-Black tree (when bucket size > 8)
- Load factor: 0.75
- Initial capacity: 16
- Rehashing occurs when size exceeds capacity × load factor

**Hash Function:**
```java
int hash = (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
int index = hash & (capacity - 1);
```

---

### TreeMap
**Summary:**
- Implements NavigableMap interface
- Keys stored in sorted order
- Red-Black tree implementation
- Does NOT allow null keys
- Not synchronized
- Slower than HashMap

**When to Use:**
- Need sorted keys
- Range queries
- Navigable operations

**Example:**
```java
Map<Integer, String> treeMap = new TreeMap<>();
treeMap.put(3, "Three");
treeMap.put(1, "One");
treeMap.put(2, "Two");
// Keys in order: 1, 2, 3

// NavigableMap operations
NavigableMap<Integer, String> navMap = new TreeMap<>();
navMap.put(1, "One");
navMap.put(3, "Three");
navMap.put(5, "Five");

navMap.lowerKey(3);        // Returns 1
navMap.floorKey(4);        // Returns 3
navMap.ceilingKey(4);      // Returns 5
navMap.higherKey(3);       // Returns 5
navMap.subMap(1, 5);       // Returns map with keys [1,5)
```

**Time Complexity:**
- `put(key, value)`: O(log n)
- `get(key)`: O(log n)
- `remove(key)`: O(log n)
- `containsKey(key)`: O(log n)

---

### LinkedHashMap
**Summary:**
- Extends HashMap
- Maintains insertion order (or access order)
- Hash table + doubly-linked list
- Can be configured for LRU cache
- Predictable iteration order

**When to Use:**
- Need insertion order
- Building LRU cache
- Predictable iteration needed

**Example:**
```java
// Insertion-order map
Map<String, Integer> linkedHashMap = new LinkedHashMap<>();
linkedHashMap.put("C", 3);
linkedHashMap.put("A", 1);
linkedHashMap.put("B", 2);
// Iteration order: C, A, B

// Access-order map (LRU cache)
Map<String, Integer> lruCache = new LinkedHashMap<>(16, 0.75f, true) {
    @Override
    protected boolean removeEldestEntry(Map.Entry<String, Integer> eldest) {
        return size() > 3; // Max size 3
    }
};
```

---

### ConcurrentHashMap
**Summary:**
- Thread-safe without synchronized
- Segment-based locking (Java 7) ? Node-based locking (Java 8+)
- Better concurrency than Hashtable
- Does NOT allow null keys or values
- Fail-safe iterators

**When to Use:**
- Multi-threaded environment
- High concurrency required
- Better performance than synchronized Map

**Example:**
```java
Map<String, Integer> concurrentMap = new ConcurrentHashMap<>();
concurrentMap.put("Apple", 10);
concurrentMap.putIfAbsent("Apple", 20);  // Returns 10, doesn't update

// Atomic operations (Java 8+)
concurrentMap.compute("Apple", (key, value) -> value == null ? 1 : value + 1);
concurrentMap.merge("Banana", 1, Integer::sum);

// Parallel operations
concurrentMap.forEach(2, (key, value) -> 
    System.out.println(key + ": " + value)
);
```

**Time Complexity:**
- Same as HashMap: O(1) average
- Lock-free reads in most cases

---

## Queue and Deque

### PriorityQueue
**Summary:**
- Min-heap by default
- Elements ordered by natural ordering or comparator
- Not thread-safe
- Does NOT allow null
- Head is the smallest element

**Example:**
```java
Queue<Integer> pq = new PriorityQueue<>();
pq.offer(5);
pq.offer(1);
pq.offer(3);
pq.poll();  // Returns 1 (smallest)

// Max-heap
Queue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
```

### ArrayDeque
**Summary:**
- Resizable array implementation of Deque
- Faster than LinkedList as a queue/stack
- Not thread-safe
- Does NOT allow null
- Can be used as stack or queue

**Example:**
```java
Deque<String> deque = new ArrayDeque<>();
deque.addFirst("First");
deque.addLast("Last");
deque.pollFirst();
deque.pollLast();
```

---

## Common Questions & Answers

### 1. When would you use ArrayList vs LinkedList?

**Answer:**
Choose **ArrayList** when:
- You need fast random access by index (O(1) vs O(n))
- You mostly read/access elements
- You add/remove elements primarily at the end
- Memory is a concern (ArrayList uses less memory)

Choose **LinkedList** when:
- You frequently insert/delete in the middle (O(1) vs O(n) after finding position)
- You need queue/deque operations (addFirst, addLast, removeFirst, removeLast)
- You don't need random access
- You're implementing a queue or stack

**Example:**
```java
// Good use of ArrayList
List<String> names = new ArrayList<>();
names.add("Alice");  // Fast: O(1)
String first = names.get(0);  // Fast: O(1)

// Good use of LinkedList
Queue<Task> taskQueue = new LinkedList<>();
taskQueue.offer(new Task());  // Fast: O(1)
taskQueue.poll();  // Fast: O(1)

// Bad use of ArrayList
List<String> list = new ArrayList<>();
for (int i = 0; i < 1000; i++) {
    list.add(0, "Item");  // Slow: O(n) - shifts all elements
}

// Bad use of LinkedList
List<Integer> numbers = new LinkedList<>();
for (int i = 0; i < 1000; i++) {
    numbers.add(i);
}
// Slow random access
for (int i = 0; i < numbers.size(); i++) {
    System.out.println(numbers.get(i));  // O(n) for each get()
}
```

---

### 2. How does HashMap work internally?

**Answer:**
HashMap stores key-value pairs using an array of buckets and hashing mechanism.

**Internal Structure:**
1. **Array of Nodes**: Initial capacity 16, each index is a bucket
2. **Hashing**: Key's hashCode() determines the bucket index
3. **Collision Handling**: 
   - Linked List (bucket size ? 8)
   - Red-Black Tree (bucket size > 8)

**Put Operation:**
```java
// Simplified internal working
public V put(K key, V value) {
    // 1. Calculate hash
    int hash = hash(key);
    
    // 2. Find bucket index
    int index = hash & (capacity - 1);
    
    // 3. Check if key exists in bucket
    Node<K,V> existing = findNode(index, key);
    if (existing != null) {
        V oldValue = existing.value;
        existing.value = value;  // Update value
        return oldValue;
    }
    
    // 4. Add new node to bucket
    addNode(index, key, value);
    
    // 5. Resize if needed
    if (++size > threshold) {
        resize();  // Doubles capacity and rehashes
    }
    return null;
}
```

**Get Operation:**
```java
public V get(Object key) {
    int hash = hash(key);
    int index = hash & (capacity - 1);
    Node<K,V> node = findNode(index, key);
    return node == null ? null : node.value;
}
```

**Key Points:**
- Hash collisions are handled via chaining (linked list/tree)
- Load factor 0.75 balances space vs time
- Rehashing occurs when size exceeds threshold
- Java 8+ converts linked list to tree when bucket size > 8 (prevents DOS attacks)
- Uses equals() for key comparison, hashCode() for bucket selection

**Why it's O(1):**
- Direct array access to bucket: O(1)
- Good hash function distributes keys evenly
- Worst case O(n) if all keys in one bucket, but O(log n) with tree structure

---

### 3. What is the time complexity of HashMap operations?

**Answer:**

| Operation | Average Case | Worst Case (Java 7) | Worst Case (Java 8+) |
|-----------|--------------|---------------------|----------------------|
| get(key) | O(1) | O(n) | O(log n) |
| put(key, value) | O(1) | O(n) | O(log n) |
| remove(key) | O(1) | O(n) | O(log n) |
| containsKey(key) | O(1) | O(n) | O(log n) |

**Explanation:**
- **Average O(1)**: With good hash function, keys distributed evenly across buckets
- **Worst O(n)**: All keys hash to same bucket (Java 7 uses linked list)
- **Worst O(log n)**: Java 8+ converts large buckets to Red-Black trees

**Factors Affecting Performance:**
```java
// Bad hashCode() - always returns same value
class BadKey {
    @Override
    public int hashCode() {
        return 1;  // All keys go to same bucket - O(n) operations
    }
}

// Good hashCode() - distributes keys evenly
class GoodKey {
    private String id;
    
    @Override
    public int hashCode() {
        return Objects.hash(id);  // Uses proper hashing
    }
}
```

**Space Complexity:** O(n) where n is number of entries

---

### 4. How do you make a collection thread-safe?

**Answer:**
There are multiple approaches to make collections thread-safe:

**1. Collections.synchronizedXxx() - Wrapper Methods:**
```java
// Wrap existing collection
List<String> syncList = Collections.synchronizedList(new ArrayList<>());
Set<String> syncSet = Collections.synchronizedSet(new HashSet<>());
Map<String, Integer> syncMap = Collections.synchronizedMap(new HashMap<>());

// IMPORTANT: Iteration must be manually synchronized
synchronized(syncList) {
    for (String item : syncList) {
        System.out.println(item);
    }
}
```
**Pros:** Simple, works with any collection
**Cons:** Entire collection locked, poor concurrency, iteration not safe

**2. Concurrent Collections (Recommended):**
```java
// High concurrency, lock-free reads
Map<String, Integer> concurrentMap = new ConcurrentHashMap<>();
Queue<String> concurrentQueue = new ConcurrentLinkedQueue<>();
Deque<String> concurrentDeque = new ConcurrentLinkedDeque<>();
List<String> copyOnWriteList = new CopyOnWriteArrayList<>();
Set<String> copyOnWriteSet = new CopyOnWriteArraySet<>();

// Blocking queues for producer-consumer
BlockingQueue<Task> blockingQueue = new LinkedBlockingQueue<>();
BlockingQueue<Task> arrayBlockingQueue = new ArrayBlockingQueue<>(100);
```
**Pros:** Better concurrency, fail-safe iterators, atomic operations
**Cons:** CopyOnWrite* expensive for writes

**3. Vector and Hashtable (Legacy - NOT Recommended):**
```java
List<String> vector = new Vector<>();  // Synchronized ArrayList
Map<String, Integer> hashtable = new Hashtable<>();  // Synchronized HashMap
```
**Cons:** Entire method locked, poor performance, obsolete

**4. Manual Synchronization:**
```java
List<String> list = new ArrayList<>();

public synchronized void addItem(String item) {
    list.add(item);
}

public synchronized String getItem(int index) {
    return list.get(index);
}
```

**When to Use Each:**
- **ConcurrentHashMap**: High-concurrency read/write scenarios
- **CopyOnWriteArrayList**: Many reads, few writes (e.g., listeners, observers)
- **BlockingQueue**: Producer-consumer pattern
- **Collections.synchronizedXxx()**: Low contention, simple synchronization

**Example - Producer-Consumer:**
```java
BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(10);

// Producer
new Thread(() -> {
    try {
        queue.put(1);  // Blocks if queue is full
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
}).start();

// Consumer
new Thread(() -> {
    try {
        Integer item = queue.take();  // Blocks if queue is empty
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
}).start();
```

---

### 5. Explain fail-fast vs fail-safe iterators

**Answer:**

**Fail-Fast Iterators:**
- Throw `ConcurrentModificationException` if collection is modified during iteration
- Used by: ArrayList, HashMap, HashSet, TreeMap, etc.
- Check modification count before each operation
- Fast detection of concurrent modifications
- Better for catching bugs early

```java
// Fail-Fast Example
List<String> list = new ArrayList<>(Arrays.asList("A", "B", "C"));
Iterator<String> iterator = list.iterator();

while (iterator.hasNext()) {
    String item = iterator.next();
    list.add("D");  // ? ConcurrentModificationException
}

// Correct way to modify during iteration
Iterator<String> it = list.iterator();
while (it.hasNext()) {
    String item = it.next();
    if (item.equals("B")) {
        it.remove();  // ? Safe removal
    }
}
```

**How Fail-Fast Works:**
```java
// Simplified ArrayList iterator
private class Itr implements Iterator<E> {
    int expectedModCount = modCount;  // Capture at creation
    
    public E next() {
        if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
        // Return element
    }
}
```

**Fail-Safe Iterators:**
- Work on a copy/snapshot of the collection
- Do NOT throw ConcurrentModificationException
- Used by: CopyOnWriteArrayList, ConcurrentHashMap, etc.
- May not reflect recent modifications
- Safe for concurrent access

```java
// Fail-Safe Example
List<String> list = new CopyOnWriteArrayList<>(Arrays.asList("A", "B", "C"));
for (String item : list) {
    list.add("D");  // ? No exception, but iterator sees old snapshot
}

Map<String, Integer> map = new ConcurrentHashMap<>();
map.put("A", 1);
map.put("B", 2);

for (String key : map.keySet()) {
    map.put("C", 3);  // ? Safe, iterator may or may not see "C"
}
```

**Comparison:**

| Aspect | Fail-Fast | Fail-Safe |
|--------|-----------|-----------|
| Exception | Yes | No |
| Snapshot | No | Yes (some implementations) |
| Memory | Less | More |
| Performance | Better | Slightly worse |
| Use Case | Single-threaded | Multi-threaded |
| Examples | ArrayList, HashMap | CopyOnWriteArrayList, ConcurrentHashMap |

**Best Practices:**
```java
// ? Don't modify collection directly during iteration
for (String item : list) {
    list.remove(item);  // Fail-fast will throw exception
}

// ? Use iterator's remove method
Iterator<String> it = list.iterator();
while (it.hasNext()) {
    if (condition) {
        it.remove();  // Safe
    }
}

// ? Use removeIf (Java 8+)
list.removeIf(item -> item.startsWith("A"));

// ? Or collect to remove list
List<String> toRemove = new ArrayList<>();
for (String item : list) {
    if (condition) {
        toRemove.add(item);
    }
}
list.removeAll(toRemove);
```

**Note:** Fail-fast is NOT guaranteed - it's a best-effort mechanism. Don't rely on it for program correctness.

---

## Performance Comparison Table

| Operation | ArrayList | LinkedList | HashSet | TreeSet | HashMap | TreeMap |
|-----------|-----------|------------|---------|---------|---------|---------|
| Add | O(1)* | O(1) | O(1) | O(log n) | O(1) | O(log n) |
| Remove | O(n) | O(1)** | O(1) | O(log n) | O(1) | O(log n) |
| Get | O(1) | O(n) | O(1) | O(log n) | O(1) | O(log n) |
| Contains | O(n) | O(n) | O(1) | O(log n) | O(1) | O(log n) |
| Iteration | O(n) | O(n) | O(n) | O(n) | O(n) | O(n) |

*Amortized O(1), worst case O(n) when resizing
**O(1) if node reference known, otherwise O(n) to find node

---

## Choosing the Right Collection

### Decision Tree

**Need key-value pairs?**
- Yes ? Map (HashMap, TreeMap, LinkedHashMap)
- No ? Continue

**Allow duplicates?**
- Yes ? List (ArrayList, LinkedList)
- No ? Set (HashSet, TreeSet, LinkedHashSet)

**Need ordering?**
- Natural/Custom order ? TreeSet, TreeMap
- Insertion order ? LinkedHashSet, LinkedHashMap
- No order ? HashSet, HashMap

**Thread-safe required?**
- Yes ? ConcurrentHashMap, CopyOnWriteArrayList, etc.
- No ? Use standard implementations

**Performance priorities?**
- Fast random access ? ArrayList, HashMap
- Fast insertions/deletions ? LinkedList, HashSet
- Sorted operations ? TreeSet, TreeMap

---

## Best Practices

1. **Specify initial capacity** if size is known:
```java
List<String> list = new ArrayList<>(1000);  // Avoid resizing
Map<String, Integer> map = new HashMap<>(1000, 0.75f);
```

2. **Use interface types** for declarations:
```java
List<String> list = new ArrayList<>();  // ? Good
ArrayList<String> list = new ArrayList<>();  // ? Avoid
```

3. **Choose appropriate collection** based on operations
4. **Use diamond operator** (Java 7+):
```java
Map<String, List<Integer>> map = new HashMap<>();  // ?
```

5. **Consider immutable collections** (Java 9+):
```java
List<String> immutable = List.of("A", "B", "C");
Set<Integer> immutableSet = Set.of(1, 2, 3);
Map<String, Integer> immutableMap = Map.of("A", 1, "B", 2);
```

6. **Use streams** for complex operations (Java 8+):
```java
list.stream()
    .filter(s -> s.startsWith("A"))
    .map(String::toUpperCase)
    .collect(Collectors.toList());
```

---

## Common Pitfalls

1. **Modifying collection during iteration:**
```java
// ? Wrong
for (String item : list) {
    list.remove(item);
}

// ? Correct
list.removeIf(item -> condition);
```

2. **Not overriding hashCode() and equals():**
```java
class Person {
    String name;
    
    // Must override both for HashMap/HashSet
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(name, person.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
```

3. **Using null inappropriately:**
```java
TreeSet<String> set = new TreeSet<>();
set.add(null);  // ? NullPointerException

HashMap<String, String> map = new HashMap<>();
map.put(null, "value");  // ? OK for HashMap
```

4. **Assuming iteration order without guarantee:**
```java
Set<String> set = new HashSet<>();
// No guaranteed order - use LinkedHashSet or TreeSet
```

---

## Summary

- **ArrayList**: Fast access, slow modifications ? Use for random access
- **LinkedList**: Fast modifications, slow access ? Use for queues/stacks
- **HashSet**: Fast operations, no order ? Use to eliminate duplicates
- **TreeSet**: Sorted, slower ? Use when sorting required
- **HashMap**: Fast key-value lookups ? Use for general mapping
- **TreeMap**: Sorted keys ? Use for sorted key operations
- **ConcurrentHashMap**: Thread-safe ? Use in multi-threaded scenarios

**Remember:** Choose based on your access patterns, not just features!

---

*Last Updated: January 2026*

