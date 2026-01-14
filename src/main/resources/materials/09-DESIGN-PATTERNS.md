# Design Patterns - Complete Guide

## Overview
Concise summaries and practical Java examples for essential design patterns across creational, structural, and behavioral categories. Includes guidance, common pitfalls, and answers to typical interview questions.

---

## 1) Creational Patterns

### Singleton
- Ensures a class has a single instance and provides a global access point.
- Use cases: configuration, logging, caches.
- Pitfalls: global state, testing difficulty; prefer dependency injection.
```java
public class Singleton {
    private static volatile Singleton INSTANCE;
    private Singleton() {}
    public static Singleton getInstance() {
        if (INSTANCE == null) {
            synchronized (Singleton.class) {
                if (INSTANCE == null) INSTANCE = new Singleton();
            }
        }
        return INSTANCE;
    }
}
```

### Factory Method
- Defines an interface for creating objects; subclasses decide which class to instantiate.
- Use cases: decouple creation from usage; switching implementations.
```java
interface Transport { void deliver(); }
class Truck implements Transport { public void deliver() { /* ... */ } }
class Ship implements Transport { public void deliver() { /* ... */ } }
abstract class Logistics { abstract Transport createTransport(); }
class RoadLogistics extends Logistics { Transport createTransport() { return new Truck(); } }
```

### Abstract Factory
- Provides an interface to create families of related objects without specifying concrete classes.
- Use cases: UI themes, database drivers.
```java
interface Button { void render(); }
interface Checkbox { void toggle(); }
interface UIFactory { Button createButton(); Checkbox createCheckbox(); }
class MacFactory implements UIFactory { /* return MacButton/MacCheckbox */ }
class WinFactory implements UIFactory { /* return WinButton/WinCheckbox */ }
```

### Builder
- Separates construction of a complex object from its representation; fluent API.
- Use cases: constructing immutable objects with many optional parameters.
```java
class User {
    private final String name; private final String email; private final int age;
    private User(Builder b){ this.name=b.name; this.email=b.email; this.age=b.age; }
    static class Builder { String name; String email; int age; Builder name(String n){this.name=n; return this;} Builder email(String e){this.email=e; return this;} Builder age(int a){this.age=a; return this;} User build(){ return new User(this);} }
}
User u = new User.Builder().name("John").email("john@example.com").age(30).build();
```

### Prototype
- Creates new objects by cloning an existing instance.
- Use cases: avoid costly construction; copy configured templates.
```java
class Shape implements Cloneable { int x,y; @Override protected Shape clone() { try { return (Shape) super.clone(); } catch (CloneNotSupportedException e) { throw new AssertionError(e); } } }
```

---

## 2) Structural Patterns

### Adapter
- Converts interface of a class into another interface clients expect.
- Use cases: integrate legacy or third-party code.
```java
interface JsonParser { Map<String,Object> parse(String json); }
class LegacyXmlParser { Map<String,Object> parseXml(String xml){ /*...*/ return Map.of(); } }
class XmlToJsonAdapter implements JsonParser {
    private final LegacyXmlParser xml;
    XmlToJsonAdapter(LegacyXmlParser xml){ this.xml = xml; }
    public Map<String,Object> parse(String json){ String xmlStr = convertJsonToXml(json); return xml.parseXml(xmlStr); }
}
```

### Decorator
- Adds responsibilities to objects dynamically without modifying the class.
- Use cases: I/O streams, logging, caching wrappers.
```java
interface Notifier { void send(String msg); }
class EmailNotifier implements Notifier { public void send(String msg){ /* email */ } }
class SmsDecorator implements Notifier {
    private final Notifier wrap;
    SmsDecorator(Notifier wrap){ this.wrap = wrap; }
    public void send(String msg){ wrap.send(msg); /* send SMS */ }
}
```

### Facade
- Provides a simplified interface to a complex subsystem.
- Use cases: simplify library usage, onboarding.
```java
class PaymentFacade {
    void pay(Order order){ /* validate, charge card, notify, persist */ }
}
```

### Proxy
- Controls access to an object; can add lazy-loading, caching, or security.
- Use cases: virtual proxies, remote proxies, protection proxies.
```java
interface Image { void display(); }
class RealImage implements Image { public RealImage(String path){ /* load heavy */ } public void display(){ /* render */ } }
class ImageProxy implements Image { private RealImage real; private final String path; public ImageProxy(String path){ this.path=path; } public void display(){ if(real==null) real=new RealImage(path); real.display(); } }
```

### Composite
- Treat individual objects and compositions uniformly.
- Use cases: tree structures (filesystems, UI components).
```java
interface Component { void render(); }
class Leaf implements Component { public void render(){ /* ... */ } }
class Composite implements Component { private final List<Component> children = new ArrayList<>(); public void add(Component c){ children.add(c);} public void render(){ children.forEach(Component::render); } }
```

### Bridge
- Decouple abstraction from implementation so both can vary independently.
- Use cases: cross-platform rendering, devices vs controls.
```java
interface Renderer { void drawCircle(int x,int y,int r); }
class VectorRenderer implements Renderer { public void drawCircle(int x,int y,int r){ /* vector */ } }
class RasterRenderer implements Renderer { public void drawCircle(int x,int y,int r){ /* raster */ } }
class Circle { private final Renderer r; Circle(Renderer r){ this.r=r; } void draw(){ r.drawCircle(0,0,10); } }
```

---

## 3) Behavioral Patterns

### Strategy
- Define a family of algorithms, encapsulate each, and make them interchangeable.
- Use cases: sorting strategies, payment methods, compression.
```java
interface CompressionStrategy { byte[] compress(byte[] data); }
class ZipCompression implements CompressionStrategy { public byte[] compress(byte[] d){ /*...*/ return d; } }
class GzipCompression implements CompressionStrategy { public byte[] compress(byte[] d){ /*...*/ return d; } }
class Compressor { private CompressionStrategy s; Compressor(CompressionStrategy s){ this.s=s; } void setStrategy(CompressionStrategy s){ this.s=s; } byte[] run(byte[] d){ return s.compress(d); } }
```

### Observer
- Defines one-to-many dependency so that when one object changes state, all dependents are notified.
- Use cases: event handling, UI updates.
```java
interface Listener { void onData(String data); }
class Publisher {
    private final List<Listener> listeners = new CopyOnWriteArrayList<>();
    void subscribe(Listener l){ listeners.add(l);} void unsubscribe(Listener l){ listeners.remove(l);} void publish(String d){ listeners.forEach(l -> l.onData(d)); }
}
```

### Command
- Encapsulates a request as an object, allowing undo/redo and queuing.
- Use cases: GUI actions, task queues.
```java
interface Command { void execute(); }
class SaveCommand implements Command { public void execute(){ /* save */ } }
class Invoker { void run(Command c){ c.execute(); } }
```

### Template Method
- Defines the skeleton of an algorithm, deferring steps to subclasses.
- Use cases: workflow steps with invariant structure.
```java
abstract class DataImporter {
    final void importData(){ read(); transform(); save(); }
    abstract void read(); abstract void transform(); abstract void save();
}
class CsvImporter extends DataImporter { void read(){ /* ... */ } void transform(){ /* ... */ } void save(){ /* ... */ } }
```

### State
- Allows an object to change behavior when its internal state changes.
- Use cases: order lifecycle, TCP connection states.
```java
interface State { void handle(Context ctx); }
class Context { private State s; void set(State s){ this.s=s; } void request(){ s.handle(this); } }
class NewState implements State { public void handle(Context c){ c.set(new ProcessingState()); } }
```

### Chain of Responsibility
- Passes a request along a chain of handlers until one handles it.
- Use cases: logging pipelines, validation.
```java
abstract class Handler {
    private Handler next;
    Handler linkWith(Handler n){ this.next=n; return n; }
    final void handle(String r){ if (!process(r) && next != null) next.handle(r); }
    abstract boolean process(String r);
}
```

### Mediator
- Defines an object that encapsulates how a set of objects interact.
- Use cases: chat rooms, UI dialogs.
```java
class Mediator { void notify(Component c, String event){ /* ... */ } }
```

### Iterator
- Provides a way to access elements sequentially without exposing underlying representation.
- Use cases: custom collections.
```java
class Range implements Iterable<Integer> { public Iterator<Integer> iterator(){ return new Iterator<>() { int cur=0; public boolean hasNext(){ return cur<10; } public Integer next(){ return cur++; } }; } }
```

### Memento
- Captures and externalizes an object's internal state so the object can be restored later.
- Use cases: undo functionality.
```java
class Editor { private String text; String getText(){return text;} void setText(String t){text=t;} }
class Snapshot { private final String text; Snapshot(String t){ this.text=t; } String get(){ return text; } }
```

---

## 4) Patterns in Spring

- Singleton scope is default for Spring beans (container-managed, thread-safe when stateless).
- Strategy widely used via interfaces and injection.
- Template Method seen in Spring templates (JdbcTemplate, RestTemplate) wrapping invariant workflow.
- Proxy pervasive in AOP and transactional method interception.
- Factory/Builder: BeanFactory, builders for WebClient/Security.

---

## 5) Anti-Patterns & Pitfalls

- God Object: too many responsibilities; refactor with SRP.
- Overusing Singletons: hidden dependencies; prefer DI.
- Anemic Domain Model: getters/setters only; push business behavior into domain.
- Premature optimization/generalization: YAGNI; implement only what’s needed.

---

## Common Interview Questions & Answers

### Q1: When would you choose Factory Method vs Abstract Factory?
- Factory Method: vary creation in subclasses for a single product type.
- Abstract Factory: create families of related products; ensure compatibility.

### Q2: Builder vs Factory?
- Builder: step-by-step construction of complex objects; good for immutable objects with optional fields.
- Factory: choose implementation based on input; returns fully constructed object immediately.

### Q3: Strategy vs State?
- Strategy: interchangeable algorithms; context selects strategy.
- State: behavior changes based on internal state transitions within the context.

### Q4: Where do you see Decorator used in Java?
- java.io streams (e.g., BufferedInputStream wraps InputStream).
- Logging, caching wrappers around services.

### Q5: How does Proxy differ from Decorator?
- Proxy controls access (lazy load, security, remote); may not add behavior for original operations.
- Decorator adds responsibilities while preserving interface; typically stacks behaviors.

### Q6: What is the benefit of Facade?
- Simplifies usage, reduces coupling to complex subsystems; improves onboarding and maintenance.

### Q7: How to avoid Singleton pitfalls?
- Use DI containers (Spring), keep beans stateless; avoid global mutable state; provide configuration via properties.

### Q8: What are common anti-patterns you avoid?
- God object, anemic domain, excessive singletons, shotgun surgery, tight coupling; apply SOLID and cohesion principles.

### Q9: Example of Chain of Responsibility in a web app?
- Servlet filter chain / Spring Security filter chain processes requests step-by-step.

### Q10: Why prefer composition over inheritance?
- Composition enables flexible reuse and avoids brittle hierarchies; reduces coupling and increases testability.

---

## Quick Reference
- Creational: Singleton, Factory Method, Abstract Factory, Builder, Prototype.
- Structural: Adapter, Decorator, Facade, Proxy, Composite, Bridge.
- Behavioral: Strategy, Observer, Command, Template Method, State, Chain of Responsibility, Mediator, Iterator, Memento.
- Prefer composition, DI, and interface-based design.

---

*Last Updated: January 2026*

