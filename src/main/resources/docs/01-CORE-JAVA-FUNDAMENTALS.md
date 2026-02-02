# Core Java Fundamentals - Complete Guide

## Table of Contents
1. [Object-Oriented Programming (OOP)](#object-oriented-programming)
2. [Java Memory Management](#java-memory-management)
3. [Java Class Loading](#java-class-loading)
4. [Equals, HashCode, and Comparisons](#equals-hashcode-and-comparisons)
5. [Interview Questions & Answers](#interview-questions--answers)

---

## Object-Oriented Programming (OOP)

### 1. Encapsulation

**Definition:** Encapsulation is the bundling of data (variables) and methods that operate on that data into a single unit (class), while hiding the internal state and requiring all interaction to occur through well-defined interfaces.

**Key Concepts:**
- **Data Hiding:** Private fields prevent direct access from outside the class
- **Public Interface:** Public methods (getters/setters) provide controlled access
- **Validation:** Setters can validate data before modifying internal state
- **Flexibility:** Internal implementation can change without affecting external code

**Example:**
```java
public class BankAccount {
    // Private fields - encapsulated data
    private String accountNumber;
    private double balance;
    private String owner;
    
    // Constructor
    public BankAccount(String accountNumber, String owner) {
        this.accountNumber = accountNumber;
        this.owner = owner;
        this.balance = 0.0;
    }
    
    // Public interface with validation
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        this.balance += amount;
    }
    
    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        if (amount > balance) {
            throw new IllegalStateException("Insufficient funds");
        }
        this.balance -= amount;
    }
    
    // Getter only - balance can't be set directly
    public double getBalance() {
        return balance;
    }
    
    public String getAccountNumber() {
        return accountNumber;
    }
}
```

**Benefits:**
- **Security:** Sensitive data is protected
- **Maintainability:** Changes to internal implementation don't break external code
- **Validation:** Control over how data is modified
- **Flexibility:** Can add logging, caching, etc., in accessor methods

---

### 2. Inheritance

**Definition:** Inheritance is a mechanism where a new class (subclass/child) derives properties and behaviors from an existing class (superclass/parent), promoting code reuse and establishing a hierarchical relationship.

**Key Concepts:**
- **"IS-A" Relationship:** Subclass IS-A type of superclass
- **Code Reuse:** Inherit fields and methods from parent
- **Method Overriding:** Subclass can provide specific implementation
- **Super Keyword:** Access parent class members

**Example:**
```java
// Parent class
public abstract class Animal {
    protected String name;
    protected int age;
    
    public Animal(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    // Method to be inherited
    public void sleep() {
        System.out.println(name + " is sleeping");
    }
    
    // Abstract method - must be implemented by subclasses
    public abstract void makeSound();
    
    // Getters
    public String getName() { return name; }
    public int getAge() { return age; }
}

// Child class
public class Dog extends Animal {
    private String breed;
    
    public Dog(String name, int age, String breed) {
        super(name, age);  // Call parent constructor
        this.breed = breed;
    }
    
    // Override abstract method
    @Override
    public void makeSound() {
        System.out.println(name + " says: Woof! Woof!");
    }
    
    // Dog-specific method
    public void fetch() {
        System.out.println(name + " is fetching the ball");
    }
    
    // Override toString from Object class
    @Override
    public String toString() {
        return "Dog{name='" + name + "', age=" + age + ", breed='" + breed + "'}";
    }
}

// Another child class
public class Cat extends Animal {
    private boolean indoor;
    
    public Cat(String name, int age, boolean indoor) {
        super(name, age);
        this.indoor = indoor;
    }
    
    @Override
    public void makeSound() {
        System.out.println(name + " says: Meow!");
    }
    
    public void scratch() {
        System.out.println(name + " is scratching");
    }
}

// Usage
Dog dog = new Dog("Buddy", 3, "Golden Retriever");
dog.sleep();        // Inherited method
dog.makeSound();    // Overridden method
dog.fetch();        // Dog-specific method
```

**Types of Inheritance in Java:**
- **Single Inheritance:** One parent, one child (Java supports this)
- **Multilevel Inheritance:** Chain of inheritance (A ? B ? C)
- **Hierarchical Inheritance:** Multiple children from one parent
- **Multiple Inheritance:** NOT supported with classes (use interfaces)

**Important Points:**
- Java doesn't support multiple inheritance with classes to avoid the "Diamond Problem"
- Use `super()` to call parent constructor (must be first line in child constructor)
- Use `super.method()` to call parent method
- Private members are not inherited (not accessible in child)
- Protected members are inherited and accessible in child

---

### 3. Polymorphism

**Definition:** Polymorphism means "many forms" - the ability of an object to take many forms. It allows one interface to be used for a general class of actions.

#### 3.1 Compile-Time Polymorphism (Method Overloading)

**Definition:** Multiple methods with the same name but different parameters in the same class.

**Example:**
```java
public class Calculator {
    
    // Overloaded methods - same name, different parameters
    public int add(int a, int b) {
        return a + b;
    }
    
    public double add(double a, double b) {
        return a + b;
    }
    
    public int add(int a, int b, int c) {
        return a + b + c;
    }
    
    public String add(String a, String b) {
        return a + b;
    }
}

// Usage
Calculator calc = new Calculator();
System.out.println(calc.add(5, 10));           // Calls int version
System.out.println(calc.add(5.5, 10.2));       // Calls double version
System.out.println(calc.add(1, 2, 3));         // Calls three-parameter version
System.out.println(calc.add("Hello", "World")); // Calls String version
```

**Rules for Method Overloading:**
- Method name must be the same
- Parameter list must be different (type, number, or order)
- Return type can be different
- Access modifier can be different
- Can throw different exceptions

#### 3.2 Runtime Polymorphism (Method Overriding)

**Definition:** Subclass provides a specific implementation of a method already defined in its parent class.

**Example:**
```java
public class PaymentProcessor {
    public void processPayment(double amount) {
        System.out.println("Processing payment: $" + amount);
    }
    
    public double calculateFee(double amount) {
        return amount * 0.02; // 2% fee
    }
}

public class CreditCardProcessor extends PaymentProcessor {
    @Override
    public void processPayment(double amount) {
        System.out.println("Processing credit card payment: $" + amount);
        System.out.println("Fee: $" + calculateFee(amount));
    }
    
    @Override
    public double calculateFee(double amount) {
        return amount * 0.03; // 3% fee for credit cards
    }
}

public class PayPalProcessor extends PaymentProcessor {
    @Override
    public void processPayment(double amount) {
        System.out.println("Processing PayPal payment: $" + amount);
        System.out.println("Fee: $" + calculateFee(amount));
    }
    
    @Override
    public double calculateFee(double amount) {
        return amount * 0.025; // 2.5% fee for PayPal
    }
}

// Usage - Runtime Polymorphism in action
public class PaymentService {
    public void executePayment(PaymentProcessor processor, double amount) {
        // The actual method called is determined at runtime
        processor.processPayment(amount);
    }
}

// Client code
PaymentService service = new PaymentService();
service.executePayment(new CreditCardProcessor(), 100.0);
service.executePayment(new PayPalProcessor(), 100.0);
```

**Rules for Method Overriding:**
- Method signature must be exactly the same (name and parameters)
- Return type must be the same or covariant (subtype)
- Access modifier must be the same or more accessible (not more restrictive)
- Cannot override `final`, `static`, or `private` methods
- Use `@Override` annotation (not required but recommended)
- Can throw same, fewer, or narrower exceptions (not broader)

---

### 4. Abstraction

**Definition:** Abstraction is the process of hiding implementation details and showing only essential features to the user. It focuses on "what" an object does rather than "how" it does it.

#### 4.1 Abstract Classes

**Example:**
```java
public abstract class Shape {
    protected String color;
    
    public Shape(String color) {
        this.color = color;
    }
    
    // Abstract methods - no implementation
    public abstract double calculateArea();
    public abstract double calculatePerimeter();
    
    // Concrete method - has implementation
    public void display() {
        System.out.println("Shape color: " + color);
        System.out.println("Area: " + calculateArea());
        System.out.println("Perimeter: " + calculatePerimeter());
    }
    
    // Getter
    public String getColor() {
        return color;
    }
}

public class Circle extends Shape {
    private double radius;
    
    public Circle(String color, double radius) {
        super(color);
        this.radius = radius;
    }
    
    @Override
    public double calculateArea() {
        return Math.PI * radius * radius;
    }
    
    @Override
    public double calculatePerimeter() {
        return 2 * Math.PI * radius;
    }
}

public class Rectangle extends Shape {
    private double width;
    private double height;
    
    public Rectangle(String color, double width, double height) {
        super(color);
        this.width = width;
        this.height = height;
    }
    
    @Override
    public double calculateArea() {
        return width * height;
    }
    
    @Override
    public double calculatePerimeter() {
        return 2 * (width + height);
    }
}
```

**Abstract Class Characteristics:**
- Cannot be instantiated directly
- Can have abstract methods (without body)
- Can have concrete methods (with body)
- Can have constructors
- Can have instance variables
- Can have any access modifier
- A class can extend only one abstract class

#### 4.2 Interfaces

**Example:**
```java
public interface Drawable {
    // Abstract method (implicitly public abstract)
    void draw();
    
    // Default method (Java 8+)
    default void resize(double scale) {
        System.out.println("Resizing by factor: " + scale);
    }
    
    // Static method (Java 8+)
    static void info() {
        System.out.println("Drawable interface for graphical objects");
    }
    
    // Constant (implicitly public static final)
    String VERSION = "1.0";
}

public interface Moveable {
    void move(int x, int y);
    void rotate(double angle);
}

// Class implementing multiple interfaces
public class GraphicalObject implements Drawable, Moveable {
    private int x, y;
    private String name;
    
    public GraphicalObject(String name) {
        this.name = name;
        this.x = 0;
        this.y = 0;
    }
    
    @Override
    public void draw() {
        System.out.println("Drawing " + name + " at (" + x + ", " + y + ")");
    }
    
    @Override
    public void move(int x, int y) {
        this.x += x;
        this.y += y;
        System.out.println(name + " moved to (" + this.x + ", " + this.y + ")");
    }
    
    @Override
    public void rotate(double angle) {
        System.out.println(name + " rotated by " + angle + " degrees");
    }
}
```

**Interface Characteristics (Java 8+):**
- All methods are `public abstract` by default (before Java 8)
- Can have `default` methods with implementation (Java 8+)
- Can have `static` methods (Java 8+)
- Can have `private` methods (Java 9+)
- All fields are `public static final` (constants)
- Cannot have instance variables
- Cannot have constructors
- A class can implement multiple interfaces
- An interface can extend multiple interfaces

**Abstract Class vs Interface:**

| Feature | Abstract Class | Interface |
|---------|---------------|-----------|
| Multiple Inheritance | No (single inheritance) | Yes (multiple interfaces) |
| Methods | Abstract + Concrete | Abstract + Default + Static |
| Variables | Any type | Only constants (public static final) |
| Constructor | Yes | No |
| Access Modifiers | Any | Public (implicitly) |
| When to Use | Common base with shared state | Contract for unrelated classes |

---

### 5. SOLID Principles

#### 5.1 Single Responsibility Principle (SRP)

**Definition:** A class should have only one reason to change, meaning it should have only one job or responsibility.

**Bad Example:**
```java
// Violates SRP - handles multiple responsibilities
public class User {
    private String name;
    private String email;
    
    public void save() {
        // Database logic
        Connection conn = DriverManager.getConnection("...");
        // Save user to database
    }
    
    public void sendEmail(String message) {
        // Email logic
        // Send email to user
    }
    
    public String generateReport() {
        // Report generation logic
        return "User Report: " + name;
    }
}
```

**Good Example:**
```java
// Each class has a single responsibility
public class User {
    private String name;
    private String email;
    
    // Getters and setters only
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}

public class UserRepository {
    public void save(User user) {
        // Database logic only
        Connection conn = DriverManager.getConnection("...");
        // Save user
    }
    
    public User findById(Long id) {
        // Find user logic
        return user;
    }
}

public class EmailService {
    public void sendEmail(User user, String message) {
        // Email logic only
        System.out.println("Sending email to: " + user.getEmail());
    }
}

public class UserReportGenerator {
    public String generateReport(User user) {
        // Report generation logic only
        return "User Report: " + user.getName();
    }
}
```

#### 5.2 Open/Closed Principle (OCP)

**Definition:** Classes should be open for extension but closed for modification.

**Bad Example:**
```java
public class DiscountCalculator {
    public double calculateDiscount(String customerType, double amount) {
        if (customerType.equals("REGULAR")) {
            return amount * 0.05;
        } else if (customerType.equals("PREMIUM")) {
            return amount * 0.10;
        } else if (customerType.equals("VIP")) {
            return amount * 0.20;
        }
        return 0;
    }
    // Adding new customer type requires modifying this method
}
```

**Good Example:**
```java
public interface DiscountStrategy {
    double calculateDiscount(double amount);
}

public class RegularCustomerDiscount implements DiscountStrategy {
    @Override
    public double calculateDiscount(double amount) {
        return amount * 0.05;
    }
}

public class PremiumCustomerDiscount implements DiscountStrategy {
    @Override
    public double calculateDiscount(double amount) {
        return amount * 0.10;
    }
}

public class VIPCustomerDiscount implements DiscountStrategy {
    @Override
    public double calculateDiscount(double amount) {
        return amount * 0.20;
    }
}

public class DiscountCalculator {
    private DiscountStrategy strategy;
    
    public DiscountCalculator(DiscountStrategy strategy) {
        this.strategy = strategy;
    }
    
    public double calculate(double amount) {
        return strategy.calculateDiscount(amount);
    }
}

// Adding new customer type doesn't require modifying existing code
public class GoldCustomerDiscount implements DiscountStrategy {
    @Override
    public double calculateDiscount(double amount) {
        return amount * 0.15;
    }
}
```

#### 5.3 Liskov Substitution Principle (LSP)

**Definition:** Objects of a superclass should be replaceable with objects of its subclasses without breaking the application.

**Bad Example:**
```java
public class Rectangle {
    protected int width;
    protected int height;
    
    public void setWidth(int width) {
        this.width = width;
    }
    
    public void setHeight(int height) {
        this.height = height;
    }
    
    public int getArea() {
        return width * height;
    }
}

public class Square extends Rectangle {
    @Override
    public void setWidth(int width) {
        this.width = width;
        this.height = width; // Square has equal sides
    }
    
    @Override
    public void setHeight(int height) {
        this.height = height;
        this.width = height; // Square has equal sides
    }
}

// Problem:
Rectangle rect = new Square();
rect.setWidth(5);
rect.setHeight(10);
System.out.println(rect.getArea()); // Expected 50, but gets 100!
// Square violates LSP - doesn't behave like a Rectangle
```

**Good Example:**
```java
public interface Shape {
    int getArea();
}

public class Rectangle implements Shape {
    private int width;
    private int height;
    
    public Rectangle(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
    @Override
    public int getArea() {
        return width * height;
    }
}

public class Square implements Shape {
    private int side;
    
    public Square(int side) {
        this.side = side;
    }
    
    @Override
    public int getArea() {
        return side * side;
    }
}
```

#### 5.4 Interface Segregation Principle (ISP)

**Definition:** Clients should not be forced to depend on interfaces they don't use.

**Bad Example:**
```java
public interface Worker {
    void work();
    void eat();
    void sleep();
}

// Robot has to implement eat() and sleep() even though it doesn't need them
public class Robot implements Worker {
    @Override
    public void work() {
        System.out.println("Robot working");
    }
    
    @Override
    public void eat() {
        // Robots don't eat - forced to implement
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void sleep() {
        // Robots don't sleep - forced to implement
        throw new UnsupportedOperationException();
    }
}
```

**Good Example:**
```java
public interface Workable {
    void work();
}

public interface Eatable {
    void eat();
}

public interface Sleepable {
    void sleep();
}

public class Human implements Workable, Eatable, Sleepable {
    @Override
    public void work() {
        System.out.println("Human working");
    }
    
    @Override
    public void eat() {
        System.out.println("Human eating");
    }
    
    @Override
    public void sleep() {
        System.out.println("Human sleeping");
    }
}

public class Robot implements Workable {
    @Override
    public void work() {
        System.out.println("Robot working");
    }
    // No need to implement eat() or sleep()
}
```

#### 5.5 Dependency Inversion Principle (DIP)

**Definition:** High-level modules should not depend on low-level modules. Both should depend on abstractions.

**Bad Example:**
```java
// Low-level module
public class MySQLDatabase {
    public void save(String data) {
        System.out.println("Saving to MySQL: " + data);
    }
}

// High-level module depends on low-level module directly
public class UserService {
    private MySQLDatabase database = new MySQLDatabase();
    
    public void saveUser(String userData) {
        database.save(userData);
    }
    // If we want to switch to PostgreSQL, we must modify this class
}
```

**Good Example:**
```java
// Abstraction
public interface Database {
    void save(String data);
}

// Low-level modules
public class MySQLDatabase implements Database {
    @Override
    public void save(String data) {
        System.out.println("Saving to MySQL: " + data);
    }
}

public class PostgreSQLDatabase implements Database {
    @Override
    public void save(String data) {
        System.out.println("Saving to PostgreSQL: " + data);
    }
}

// High-level module depends on abstraction
public class UserService {
    private Database database;
    
    // Dependency injection
    public UserService(Database database) {
        this.database = database;
    }
    
    public void saveUser(String userData) {
        database.save(userData);
    }
}

// Usage - easy to switch implementations
UserService service1 = new UserService(new MySQLDatabase());
UserService service2 = new UserService(new PostgreSQLDatabase());
```

---

## Java Memory Management

### 1. Memory Areas

#### 1.1 Heap Memory

**Purpose:** Stores objects and instance variables.

**Characteristics:**
- Shared among all threads
- Garbage collected
- Larger than stack
- Slower access than stack
- Created when JVM starts
- Divided into:
  - **Young Generation** (Eden + Survivor spaces)
  - **Old Generation** (Tenured)
  - **Metaspace** (Java 8+, replaces PermGen)

**Example:**
```java
public class Person {
    private String name;  // Stored in heap
    private int age;      // Stored in heap
    
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
}

public class Demo {
    public static void main(String[] args) {
        Person p1 = new Person("John", 30);  // Object created in heap
        Person p2 = new Person("Jane", 25);  // Another object in heap
        // p1 and p2 references are on stack, objects are on heap
    }
}
```

#### 1.2 Stack Memory

**Purpose:** Stores method frames, local variables, and references.

**Characteristics:**
- Each thread has its own stack
- LIFO (Last In, First Out)
- Automatically deallocated when method returns
- Smaller than heap
- Faster access than heap
- Stores:
  - Primitive local variables
  - Object references (not the objects themselves)
  - Method call frames

**Example:**
```java
public class StackDemo {
    public static void main(String[] args) {
        int x = 10;              // Primitive on stack
        String str = "Hello";    // Reference on stack, "Hello" in String pool (heap)
        Person p = new Person(); // Reference on stack, object on heap
        
        calculate(x);
    }
    
    public static void calculate(int num) {  // New stack frame created
        int result = num * 2;                // Local variable on stack
        System.out.println(result);
    }  // Stack frame destroyed when method returns
}
```

**Stack vs Heap:**

| Aspect | Stack | Heap |
|--------|-------|------|
| Storage | Method frames, primitives, references | Objects, instance variables |
| Size | Smaller (configurable with -Xss) | Larger (configurable with -Xms, -Xmx) |
| Access Speed | Fast | Slower |
| Scope | Method/block scope | Until garbage collected |
| Thread Safety | Thread-local | Shared across threads |
| Allocation | Automatic | Managed by GC |
| Errors | StackOverflowError | OutOfMemoryError |

#### 1.3 Metaspace (Java 8+)

**Purpose:** Stores class metadata, static variables, constant pool.

**Characteristics:**
- Replaces PermGen from Java 7
- Uses native memory (not heap)
- Auto-sized by default
- Can be limited with `-XX:MaxMetaspaceSize`

**What's stored:**
- Class definitions
- Method definitions
- Static variables
- Runtime constant pool

---

### 2. Garbage Collection (GC)

**Definition:** Automatic memory management that reclaims memory occupied by objects no longer referenced.

#### 2.1 How GC Works

**Reachability:**
- **Reachable:** Object can be accessed via reference chain from GC roots
- **Unreachable:** No reference chain exists, eligible for GC

**GC Roots:**
- Local variables in active methods
- Static variables
- Active threads
- JNI references

**Example:**
```java
public class GCDemo {
    public static void main(String[] args) {
        Person p1 = new Person("John");   // p1 is GC root
        Person p2 = new Person("Jane");   // p2 is GC root
        
        p1 = null;  // "John" object now unreachable, eligible for GC
        
        // p2 still references "Jane", so it won't be collected
        
        System.gc();  // Suggests GC to run (not guaranteed)
    }
}
```

#### 2.2 Generational Garbage Collection

**Young Generation:**
- New objects created here
- **Eden Space:** Objects initially allocated here
- **Survivor Spaces (S0, S1):** Objects that survive minor GC
- **Minor GC:** Frequent, fast, collects young generation

**Old Generation (Tenured):**
- Long-lived objects promoted here
- **Major GC/Full GC:** Less frequent, slower, collects entire heap

**Process:**
1. New objects allocated in Eden
2. When Eden fills, Minor GC occurs
3. Surviving objects moved to Survivor space
4. Objects surviving multiple GCs promoted to Old Generation
5. When Old Generation fills, Major GC occurs

#### 2.3 Types of Garbage Collectors

**Serial GC** (`-XX:+UseSerialGC`)
- Single thread for GC
- Pauses application during GC
- Suitable for small applications, single-core machines

**Parallel GC** (`-XX:+UseParallelGC`)
- Multiple threads for GC
- Throughput-oriented
- Default in Java 8
- Pauses application but faster than Serial

**G1 GC** (`-XX:+UseG1GC`)
- Divides heap into regions
- Predictable pause times
- Default in Java 9+
- Balances throughput and latency
- Suitable for large heaps (> 4GB)

**ZGC** (`-XX:+UseZGC`)
- Ultra-low pause times (< 10ms)
- Scalable (handles multi-terabyte heaps)
- Concurrent (minimal pausing)
- Java 11+

**Example Configuration:**
```bash
# Set heap size
java -Xms512m -Xmx2g MyApp

# Use G1 GC with max pause time
java -XX:+UseG1GC -XX:MaxGCPauseMillis=200 MyApp

# GC logging (Java 9+)
java -Xlog:gc*:file=gc.log MyApp
```

#### 2.4 Memory Leaks

**Common Causes:**

**1. Static Collections:**
```java
public class Cache {
    // Never cleared - objects never GC'd
    private static Map<String, Object> cache = new HashMap<>();
    
    public static void put(String key, Object value) {
        cache.put(key, value);
    }
}
```

**2. Unclosed Resources:**
```java
public void readFile(String path) {
    BufferedReader reader = new BufferedReader(new FileReader(path));
    // If exception occurs, reader never closed - resource leak
    String line = reader.readLine();
}

// Fix: Use try-with-resources
public void readFile(String path) throws IOException {
    try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
        String line = reader.readLine();
    } // Automatically closed
}
```

**3. Inner Class References:**
```java
public class Outer {
    private byte[] data = new byte[1000000];
    
    public class Inner {
        // Implicitly holds reference to Outer
        // If Inner lives long, Outer can't be GC'd
    }
    
    // Fix: Use static inner class if no access to outer needed
    public static class StaticInner {
        // No implicit reference to Outer
    }
}
```

**4. Listeners and Callbacks:**
```java
public class EventSource {
    private List<Listener> listeners = new ArrayList<>();
    
    public void addListener(Listener listener) {
        listeners.add(listener);
        // If listener never removed, can't be GC'd
    }
    
    public void removeListener(Listener listener) {
        listeners.remove(listener);  // Must call this!
    }
}
```

---

### 3. String Pool

**Definition:** Special memory region in heap where String literals are stored to optimize memory usage.

**String Immutability:**
- Strings are immutable (cannot be changed after creation)
- Every modification creates a new String object
- Allows safe sharing via String pool

**Example:**
```java
// String pool
String s1 = "hello";           // Created in string pool
String s2 = "hello";           // Reuses same object from pool
System.out.println(s1 == s2);  // true - same reference

// Heap (outside pool)
String s3 = new String("hello");  // New object in heap
System.out.println(s1 == s3);     // false - different objects
System.out.println(s1.equals(s3)); // true - same content

// intern() - add to pool
String s4 = new String("hello").intern();
System.out.println(s1 == s4);  // true - s4 now references pool object
```

**String vs StringBuilder vs StringBuffer:**

```java
// String - immutable, creates new objects
String str = "Hello";
str = str + " World";  // Creates new String object
str = str + "!";       // Creates another new String object
// Inefficient for multiple concatenations

// StringBuilder - mutable, not thread-safe (fast)
StringBuilder sb = new StringBuilder("Hello");
sb.append(" World");   // Modifies same object
sb.append("!");        // Modifies same object
String result = sb.toString();
// Efficient for multiple modifications

// StringBuffer - mutable, thread-safe (slower than StringBuilder)
StringBuffer sbf = new StringBuffer("Hello");
sbf.append(" World");  // Synchronized method
sbf.append("!");
String result2 = sbf.toString();
```

**Performance Comparison:**
```java
// Bad - inefficient
String result = "";
for (int i = 0; i < 10000; i++) {
    result += i;  // Creates 10000 String objects!
}

// Good - efficient
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 10000; i++) {
    sb.append(i);  // Modifies same object
}
String result = sb.toString();
```

---

## Java Class Loading

### Class Loader Hierarchy

**Bootstrap ClassLoader:**
- Loads core Java classes (java.lang.*, java.util.*)
- Written in native code (C/C++)
- Parent of all class loaders

**Extension ClassLoader:**
- Loads classes from extension directories (jre/lib/ext)
- Java implementation

**Application ClassLoader:**
- Loads classes from classpath
- Java implementation
- Loads your application classes

**Example:**
```java
public class ClassLoaderDemo {
    public static void main(String[] args) {
        // Application class loader
        System.out.println(ClassLoaderDemo.class.getClassLoader());
        // sun.misc.Launcher$AppClassLoader
        
        // Extension class loader
        System.out.println(ClassLoaderDemo.class.getClassLoader().getParent());
        // sun.misc.Launcher$ExtClassLoader
        
        // Bootstrap class loader (null - native code)
        System.out.println(String.class.getClassLoader());
        // null
    }
}
```

**Class Loading Process:**
1. **Loading:** Find and load class file
2. **Linking:**
   - **Verification:** Verify bytecode correctness
   - **Preparation:** Allocate memory for static variables
   - **Resolution:** Replace symbolic references with direct references
3. **Initialization:** Execute static initializers and static blocks

---

## Equals, HashCode, and Comparisons

### 1. equals() and hashCode()

**Contract:**
- If `a.equals(b)` is true, then `a.hashCode() == b.hashCode()` must be true
- If `a.hashCode() == b.hashCode()`, `a.equals(b)` may or may not be true
- If `a.equals(b)` is false, hash codes can be same or different

**Why both are needed:**
- Collections like `HashMap`, `HashSet` use hash code for bucketing
- equals() used for exact comparison within the same bucket

**Example:**
```java
public class Person {
    private String name;
    private int age;
    private String email;
    
    public Person(String name, int age, String email) {
        this.name = name;
        this.age = age;
        this.email = email;
    }
    
    @Override
    public boolean equals(Object o) {
        // Check if same reference
        if (this == o) return true;
        
        // Check if null or different class
        if (o == null || getClass() != o.getClass()) return false;
        
        // Cast and compare fields
        Person person = (Person) o;
        return age == person.age &&
               Objects.equals(name, person.name) &&
               Objects.equals(email, person.email);
    }
    
    @Override
    public int hashCode() {
        // Use Objects.hash for multiple fields
        return Objects.hash(name, age, email);
    }
}

// Usage
Person p1 = new Person("John", 30, "john@email.com");
Person p2 = new Person("John", 30, "john@email.com");

System.out.println(p1.equals(p2));     // true
System.out.println(p1.hashCode() == p2.hashCode()); // true

// Works correctly in collections
Set<Person> people = new HashSet<>();
people.add(p1);
System.out.println(people.contains(p2)); // true - found by equals()
```

**Common Mistakes:**
```java
// BAD: Override equals but not hashCode
public class BadPerson {
    private String name;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BadPerson that = (BadPerson) o;
        return Objects.equals(name, that.name);
    }
    // Missing hashCode() - breaks HashMap, HashSet
}

// Problem:
BadPerson p1 = new BadPerson("John");
BadPerson p2 = new BadPerson("John");
Set<BadPerson> set = new HashSet<>();
set.add(p1);
System.out.println(set.contains(p2)); // false! Should be true
// Different hash codes, so they go to different buckets
```

### 2. == vs equals()

**== Operator:**
- Compares references (memory addresses)
- For primitives, compares values
- Cannot be overridden

**equals() Method:**
- Compares object content
- Can be overridden to define custom equality
- Default implementation in Object class uses ==

**Example:**
```java
// Primitives - == compares values
int a = 5;
int b = 5;
System.out.println(a == b);  // true

// Objects - == compares references
String s1 = new String("hello");
String s2 = new String("hello");
System.out.println(s1 == s2);      // false - different objects
System.out.println(s1.equals(s2)); // true - same content

// String pool
String s3 = "hello";
String s4 = "hello";
System.out.println(s3 == s4);      // true - same pool object
System.out.println(s3.equals(s4)); // true - same content

// Integer caching (-128 to 127)
Integer i1 = 127;
Integer i2 = 127;
System.out.println(i1 == i2);      // true - cached

Integer i3 = 128;
Integer i4 = 128;
System.out.println(i3 == i4);      // false - not cached
System.out.println(i3.equals(i4)); // true - same value
```

### 3. Comparable and Comparator

#### Comparable Interface

**Purpose:** Define natural ordering for a class.

**Example:**
```java
public class Employee implements Comparable<Employee> {
    private String name;
    private int salary;
    private int age;
    
    public Employee(String name, int salary, int age) {
        this.name = name;
        this.salary = salary;
        this.age = age;
    }
    
    @Override
    public int compareTo(Employee other) {
        // Natural ordering by salary
        return Integer.compare(this.salary, other.salary);
        
        // Alternative implementations:
        // return this.salary - other.salary;  // Be careful with overflow!
        // return this.name.compareTo(other.name);  // By name
    }
    
    // Getters, toString, etc.
}

// Usage
List<Employee> employees = new ArrayList<>();
employees.add(new Employee("John", 50000, 30));
employees.add(new Employee("Jane", 60000, 28));
employees.add(new Employee("Bob", 45000, 35));

Collections.sort(employees);  // Sorts by salary (natural ordering)
```

#### Comparator Interface

**Purpose:** Define custom orderings separate from the class.

**Example:**
```java
// Comparator for sorting by name
public class NameComparator implements Comparator<Employee> {
    @Override
    public int compare(Employee e1, Employee e2) {
        return e1.getName().compareTo(e2.getName());
    }
}

// Comparator for sorting by age
public class AgeComparator implements Comparator<Employee> {
    @Override
    public int compare(Employee e1, Employee e2) {
        return Integer.compare(e1.getAge(), e2.getAge());
    }
}

// Usage
List<Employee> employees = new ArrayList<>();
employees.add(new Employee("John", 50000, 30));
employees.add(new Employee("Jane", 60000, 28));
employees.add(new Employee("Bob", 45000, 35));

// Sort by name
Collections.sort(employees, new NameComparator());

// Sort by age
Collections.sort(employees, new AgeComparator());

// Java 8+ Lambda comparators
Collections.sort(employees, (e1, e2) -> e1.getName().compareTo(e2.getName()));

// Java 8+ Method references
employees.sort(Comparator.comparing(Employee::getName));
employees.sort(Comparator.comparing(Employee::getAge));
employees.sort(Comparator.comparing(Employee::getSalary).reversed());

// Chained comparisons
employees.sort(Comparator.comparing(Employee::getSalary)
                        .thenComparing(Employee::getAge)
                        .thenComparing(Employee::getName));
```

**Comparable vs Comparator:**

| Aspect | Comparable | Comparator |
|--------|-----------|------------|
| Package | java.lang | java.util |
| Method | compareTo(T o) | compare(T o1, T o2) |
| Purpose | Natural ordering | Custom orderings |
| Implementation | Modify the class | External class/lambda |
| Number | One per class | Multiple per class |
| Usage | Collections.sort(list) | Collections.sort(list, comparator) |

---

## Interview Questions & Answers

### Q1: Explain the difference between abstract classes and interfaces. When would you use each?

**Answer:**

**Abstract Class:**
- Can have both abstract and concrete methods
- Can have instance variables (state)
- Can have constructors
- Supports single inheritance only
- Can have any access modifier

**Interface:**
- All methods are public and abstract by default (before Java 8)
- Can have default and static methods (Java 8+)
- Can only have constants (public static final)
- No constructors
- Supports multiple inheritance
- All methods are public

**When to use:**
- **Abstract Class:** When classes share common state and behavior. Use for "IS-A" relationship with shared implementation.
  - Example: `Animal` ? `Dog`, `Cat` (share state like name, age)
  
- **Interface:** When defining a contract that unrelated classes can implement. Use for "CAN-DO" relationship.
  - Example: `Flyable` ? `Bird`, `Airplane` (unrelated but can fly)

**Example:**
```java
// Abstract class - shared behavior and state
abstract class Vehicle {
    protected String brand;
    protected int speed;
    
    public Vehicle(String brand) {
        this.brand = brand;
    }
    
    public abstract void start();
    
    public void displayInfo() {
        System.out.println("Brand: " + brand);
    }
}

// Interface - contract
interface Chargeable {
    void charge();
    int getBatteryLevel();
}

// Class can extend one abstract class and implement multiple interfaces
class ElectricCar extends Vehicle implements Chargeable {
    private int batteryLevel;
    
    public ElectricCar(String brand) {
        super(brand);
        this.batteryLevel = 100;
    }
    
    @Override
    public void start() {
        System.out.println("Electric car starting silently");
    }
    
    @Override
    public void charge() {
        batteryLevel = 100;
    }
    
    @Override
    public int getBatteryLevel() {
        return batteryLevel;
    }
}
```

---

### Q2: What is method overloading vs method overriding?

**Answer:**

**Method Overloading (Compile-time Polymorphism):**
- Same method name, different parameters
- Within the same class
- Return type can be different
- Resolved at compile time

**Method Overriding (Runtime Polymorphism):**
- Same method signature (name and parameters)
- In subclass
- Return type must be same or covariant
- Resolved at runtime

**Example:**
```java
// Overloading
public class Calculator {
    public int add(int a, int b) {
        return a + b;
    }
    
    public double add(double a, double b) {  // Different parameter types
        return a + b;
    }
    
    public int add(int a, int b, int c) {    // Different number of parameters
        return a + b + c;
    }
}

// Overriding
class Animal {
    public void makeSound() {
        System.out.println("Some sound");
    }
}

class Dog extends Animal {
    @Override
    public void makeSound() {  // Same signature as parent
        System.out.println("Woof!");
    }
}

// Runtime polymorphism
Animal animal = new Dog();
animal.makeSound();  // Calls Dog's version - "Woof!"
```

---

### Q3: Explain the SOLID principles with examples.

**Answer:**

**S - Single Responsibility Principle:**
A class should have only one reason to change.
```java
// Bad: Multiple responsibilities
class User {
    void save() { /* database logic */ }
    void sendEmail() { /* email logic */ }
}

// Good: Separate concerns
class User { /* just data */ }
class UserRepository { void save(User u) { } }
class EmailService { void send(User u) { } }
```

**O - Open/Closed Principle:**
Open for extension, closed for modification.
```java
// Use interfaces/abstract classes so new functionality can be added without modifying existing code
interface PaymentProcessor {
    void process(double amount);
}

class CreditCardProcessor implements PaymentProcessor { }
class PayPalProcessor implements PaymentProcessor { }
// Add new processor without modifying existing code
```

**L - Liskov Substitution Principle:**
Subclasses should be substitutable for their base classes.
```java
// Bad: Square changes behavior of Rectangle
class Rectangle {
    void setWidth(int w) { }
    void setHeight(int h) { }
}
class Square extends Rectangle { } // Violates LSP

// Good: Common interface
interface Shape { int getArea(); }
class Rectangle implements Shape { }
class Square implements Shape { }
```

**I - Interface Segregation Principle:**
Don't force clients to depend on methods they don't use.
```java
// Bad: Fat interface
interface Worker {
    void work();
    void eat();
}

// Good: Segregated interfaces
interface Workable { void work(); }
interface Eatable { void eat(); }
class Human implements Workable, Eatable { }
class Robot implements Workable { } // Doesn't need Eatable
```

**D - Dependency Inversion Principle:**
Depend on abstractions, not concretions.
```java
// Bad: Direct dependency
class UserService {
    private MySQLDatabase db = new MySQLDatabase();
}

// Good: Depend on abstraction
interface Database { void save(String data); }
class UserService {
    private Database db;
    public UserService(Database db) { this.db = db; }
}
```

---

### Q4: What are the benefits of composition over inheritance?

**Answer:**

**Composition Benefits:**
1. **Flexibility:** Change behavior at runtime
2. **No fragile base class problem:** Changes to base don't break derived classes
3. **Multiple "inheritance":** Compose multiple behaviors
4. **Better encapsulation:** Internal details hidden
5. **Testability:** Easy to mock dependencies

**Example:**
```java
// Inheritance - rigid
class Animal {
    void eat() { }
    void sleep() { }
}
class Dog extends Animal {
    void bark() { }
}
// Dog inherits everything from Animal, can't change

// Composition - flexible
interface Eatable { void eat(); }
interface Sleepable { void sleep(); }
interface Barkable { void bark(); }

class Dog {
    private Eatable eatable;
    private Sleepable sleepable;
    private Barkable barkable;
    
    public Dog(Eatable e, Sleepable s, Barkable b) {
        this.eatable = e;
        this.sleepable = s;
        this.barkable = b;
    }
    
    void eat() { eatable.eat(); }
    void sleep() { sleepable.sleep(); }
    void bark() { barkable.bark(); }
}
// Can inject different implementations, change at runtime
```

**When to use inheritance:**
- Clear "IS-A" relationship
- Shared behavior and state
- Won't need to change at runtime

**When to use composition:**
- "HAS-A" or "USES-A" relationship
- Need flexibility
- Want to avoid coupling
- Multiple behaviors from different sources

---

### Q5: How does Java garbage collection work?

**Answer:**

**Garbage Collection Process:**

1. **Marking:** Identifies which objects are reachable
2. **Deletion:** Removes unreachable objects
3. **Compaction:** (Optional) Moves surviving objects together

**Generational GC:**

**Young Generation:**
- New objects allocated in **Eden** space
- When Eden fills ? **Minor GC** runs
- Surviving objects ? **Survivor** space (S0 or S1)
- Objects surviving multiple Minor GCs ? promoted to Old Generation

**Old Generation:**
- Long-lived objects
- When Old fills ? **Major GC/Full GC** runs
- Slower than Minor GC

**Example:**
```java
public void processData() {
    for (int i = 0; i < 1000000; i++) {
        String temp = "Processing " + i;  // Created in Eden
        // temp becomes unreachable at end of iteration
        // Eligible for GC
    }
}
```

**Making objects eligible for GC:**
```java
Person p = new Person("John");
p = null;  // Original object now unreachable

Person p1 = new Person("Jane");
Person p2 = p1;
p1 = null;  // Object still reachable via p2
p2 = null;  // Now unreachable, eligible for GC
```

**GC Types:**
- **Serial GC:** Single thread, small apps
- **Parallel GC:** Multiple threads, throughput focus
- **G1 GC:** Low pause times, default Java 9+
- **ZGC:** Ultra-low pause times, large heaps

---

### Q6: What's the difference between `==` and `.equals()`?

**Answer:**

**== Operator:**
- Compares **references** (memory addresses) for objects
- Compares **values** for primitives
- Cannot be overridden

**equals() Method:**
- Compares **content/values** of objects
- Can be overridden to define custom equality
- Default implementation (in Object) uses `==`

**Example:**
```java
// Primitives - == compares values
int a = 5, b = 5;
System.out.println(a == b);  // true

// Objects - == compares references
String s1 = new String("hello");
String s2 = new String("hello");
System.out.println(s1 == s2);       // false - different objects
System.out.println(s1.equals(s2));  // true - same content

// String pool - literal strings reuse same object
String s3 = "hello";
String s4 = "hello";
System.out.println(s3 == s4);       // true - same reference
System.out.println(s3.equals(s4));  // true - same content

// Custom class
class Person {
    String name;
    Person(String name) { this.name = name; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(name, person.name);
    }
}

Person p1 = new Person("John");
Person p2 = new Person("John");
System.out.println(p1 == p2);       // false - different objects
System.out.println(p1.equals(p2));  // true - same name (custom equals)
```

**Best Practice:**
- Use `==` for primitives and checking null
- Use `equals()` for object content comparison
- Always override `hashCode()` when overriding `equals()`

---

### Q7: Explain String immutability and its benefits.

**Answer:**

**String Immutability:**
Once a String is created, it cannot be changed. Any "modification" creates a new String object.

**Why Strings are Immutable:**

1. **String Pool Optimization:**
```java
String s1 = "hello";  // Created in string pool
String s2 = "hello";  // Reuses same object
// If mutable, changing s1 would affect s2!
```

2. **Thread Safety:**
```java
// Multiple threads can safely share same String
public class SharedData {
    private String message = "Important Message";
    // No synchronization needed - String is immutable
}
```

3. **Security:**
```java
public void openConnection(String url) {
    // url can't be changed by caller after validation
    if (isValid(url)) {
        connect(url);  // Safe - url unchanged
    }
}
```

4. **Hashcode Caching:**
```java
// hashCode calculated once and cached
Map<String, Object> map = new HashMap<>();
String key = "myKey";
map.put(key, value);  // hashCode calculated and cached
// Future lookups use cached hashCode - faster
```

**Performance Implications:**
```java
// Bad - creates many String objects
String result = "";
for (int i = 0; i < 1000; i++) {
    result += i;  // Creates new String each time!
}

// Good - use StringBuilder for modifications
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 1000; i++) {
    sb.append(i);  // Modifies same object
}
String result = sb.toString();
```

**String vs StringBuilder vs StringBuffer:**
- **String:** Immutable, thread-safe, use for fixed text
- **StringBuilder:** Mutable, NOT thread-safe, fastest for single thread
- **StringBuffer:** Mutable, thread-safe (synchronized), use for multi-threading

---

### Q8: What causes OutOfMemoryError and how do you fix it?

**Answer:**

**Common Causes:**

1. **Heap Space Exhausted:**
```java
// Creating too many objects
List<byte[]> list = new ArrayList<>();
while (true) {
    list.add(new byte[1024 * 1024]);  // 1MB each
}
// Fix: Increase heap size or fix memory leak
// java -Xmx2g MyApp
```

2. **Memory Leaks:**
```java
// Static collection never cleared
public class Cache {
    private static Map<String, Object> cache = new HashMap<>();
    public static void add(String key, Object value) {
        cache.put(key, value);  // Never removed!
    }
}
// Fix: Clear cache periodically or use WeakHashMap
```

3. **Too Many Threads:**
```java
// Each thread has its own stack
while (true) {
    new Thread(() -> {
        try { Thread.sleep(Long.MAX_VALUE); }
        catch (Exception e) {}
    }).start();
}
// Fix: Use thread pools
ExecutorService executor = Executors.newFixedThreadPool(10);
```

4. **Large Object Creation:**
```java
byte[] huge = new byte[Integer.MAX_VALUE];
// Fix: Process in chunks
```

**Solutions:**

1. **Increase Heap Size:**
```bash
java -Xms1g -Xmx4g MyApp
```

2. **Find Memory Leaks:**
- Use profiler (VisualVM, JProfiler)
- Analyze heap dump
- Check for unclosed resources

3. **Fix Common Leaks:**
```java
// Always close resources
try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
    // Use reader
}  // Automatically closed

// Remove listeners
eventSource.removeListener(myListener);

// Use weak references for caches
Map<String, WeakReference<Object>> cache = new HashMap<>();
```

4. **Optimize Code:**
```java
// Use primitive collections to reduce object overhead
// Use object pools for frequently created objects
// Process data in streams instead of loading all at once
```

---

### Q9: Explain the difference between Comparable and Comparator.

**Answer:**

**Comparable:**
- Part of `java.lang` package
- Defines **natural ordering** for a class
- Modifies the class itself
- Has `compareTo(T o)` method
- Used with `Collections.sort(list)`
- One per class

**Comparator:**
- Part of `java.util` package
- Defines **custom orderings**
- External to the class
- Has `compare(T o1, T o2)` method
- Used with `Collections.sort(list, comparator)`
- Multiple per class

**Example:**
```java
// Comparable - natural ordering
public class Employee implements Comparable<Employee> {
    private String name;
    private int salary;
    
    @Override
    public int compareTo(Employee other) {
        return Integer.compare(this.salary, other.salary);
    }
}

// Usage
List<Employee> employees = Arrays.asList(
    new Employee("John", 50000),
    new Employee("Jane", 60000)
);
Collections.sort(employees);  // Sorts by salary

// Comparator - custom ordering
Comparator<Employee> nameComparator = new Comparator<Employee>() {
    @Override
    public int compare(Employee e1, Employee e2) {
        return e1.getName().compareTo(e2.getName());
    }
};

Collections.sort(employees, nameComparator);  // Sorts by name

// Java 8+ Lambda
Collections.sort(employees, (e1, e2) -> e1.getName().compareTo(e2.getName()));

// Java 8+ Method Reference
employees.sort(Comparator.comparing(Employee::getName));
employees.sort(Comparator.comparing(Employee::getSalary).reversed());

// Chained comparisons
employees.sort(Comparator.comparing(Employee::getSalary)
                        .thenComparing(Employee::getName));
```

**When to use:**
- **Comparable:** When there's an obvious natural ordering (e.g., numbers, dates)
- **Comparator:** When you need multiple orderings or can't modify the class

---

### Q10: What is the difference between final, finally, and finalize?

**Answer:**

**final:**
- Keyword
- Can be applied to variables, methods, and classes

```java
// Final variable - can't be reassigned
final int MAX = 100;
// MAX = 200;  // Compilation error

// Final method - can't be overridden
class Parent {
    public final void display() {
        System.out.println("Final method");
    }
}
class Child extends Parent {
    // public void display() { }  // Compilation error
}

// Final class - can't be extended
final class ImmutableClass {
    // ...
}
// class SubClass extends ImmutableClass { }  // Compilation error

// Final reference - reference can't change, but object can
final List<String> list = new ArrayList<>();
list.add("Item");  // OK - modifying object
// list = new ArrayList<>();  // Error - can't reassign reference
```

**finally:**
- Block in try-catch-finally
- Always executes (except System.exit())
- Used for cleanup (close resources)

```java
public void readFile(String path) {
    BufferedReader reader = null;
    try {
        reader = new BufferedReader(new FileReader(path));
        String line = reader.readLine();
        return;  // finally still executes
    } catch (IOException e) {
        System.err.println("Error reading file");
        throw e;  // finally still executes
    } finally {
        // Always executes (cleanup code)
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                // Handle close exception
            }
        }
    }
}

// Modern approach - try-with-resources (no finally needed)
try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
    String line = reader.readLine();
}  // Automatically closed
```

**finalize():**
- Method in Object class
- Called by garbage collector before object destruction
- **Deprecated in Java 9** - don't use!
- Unpredictable timing
- Better alternatives: try-with-resources, Cleaner API

```java
// Old way (deprecated - don't use)
@Override
protected void finalize() throws Throwable {
    try {
        // Cleanup code
    } finally {
        super.finalize();
    }
}

// Modern way - implement AutoCloseable
public class Resource implements AutoCloseable {
    @Override
    public void close() {
        // Cleanup code
    }
}

// Usage
try (Resource resource = new Resource()) {
    // Use resource
}  // close() called automatically
```

**Summary:**
- **final:** Prevents change (variable), override (method), inheritance (class)
- **finally:** Ensures code execution in try-catch
- **finalize():** Deprecated GC callback - use AutoCloseable instead

---

## Summary

This guide covered:

? **OOP Principles:** Encapsulation, Inheritance, Polymorphism, Abstraction  
? **SOLID Principles:** Design best practices  
? **Memory Management:** Heap, Stack, GC, String Pool  
? **Class Loading:** How Java loads and initializes classes  
? **Comparisons:** equals(), hashCode(), Comparable, Comparator  
? **Interview Q&A:** Comprehensive answers to common questions  

**Next Steps:**
- Practice implementing these concepts
- Build sample projects applying SOLID principles
- Study Collections Framework (next guide)
- Review regularly before interviews

---

**Good luck with your interview preparation! ?**

*Last Updated: January 2026*

