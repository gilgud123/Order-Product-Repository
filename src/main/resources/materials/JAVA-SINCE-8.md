# What’s New in Java (Since 8)

Covers Java 9 through Java 23 (LTS: 11, 17, 21). High-signal highlights only.

## Java 9 (2017-09)
- Platform: Java Platform Module System (JPMS, “Jigsaw”); multi-release JARs; compact strings; G1 becomes default GC.
- Tools: JShell (REPL).
- Language/API: Collection factory methods (List.of/Set.of/Map.of); Stream API (takeWhile/dropWhile/iterate with predicate); Optional.ifPresentOrElse/stream; Process API; Stack-Walking API.
- Networking: HTTP/2 Client (incubator).

## Java 10 (2018-03)
- Language: Local variable type inference (var).
- APIs/Libs: Collectors.toUnmodifiable{List|Set|Map}; Optional.orElseThrow() no-arg.
- Runtime: Application Class-Data Sharing (AppCDS); container-awareness (CPU/memory limits); parallel full GC for G1; Root CAs in OpenJDK.

## Java 11 (2018-09) — LTS
- Language/Tooling: var in lambda parameters; single-file source execution (java MyApp.java).
- APIs: Standard HTTP Client (HTTP/2, WebSocket); String (isBlank, lines, repeat, strip*); Files.readString/writeString; Path.of.
- Runtime: Java Flight Recorder (JFR) open-sourced; ZGC (experimental); Epsilon (no-op GC); TLS 1.3.
- Cleaning: Removal of Java EE/CORBA modules from JDK.

## Java 12 (2019-03)
- Language: Switch expressions (preview).
- Runtime: Shenandoah GC (experimental in OpenJDK builds); G1 returns unused memory promptly; Default CDS archives.
- APIs/Dev: JVM Constants API; Microbenchmark suite (JMH harness integrated for JDK devs).

## Java 13 (2019-09)
- Language: Text blocks (preview); switch expressions (2nd preview).
- Runtime: ZGC uncommits unused memory; Dynamic CDS archives; legacy socket API reimplemented.

## Java 14 (2020-03)
- Language: Switch expressions (final); Records (preview); Pattern matching for instanceof (preview).
- Diagnostics/Tools: Helpful NullPointerExceptions; JFR event streaming; jpackage (incubator).
- Foreign memory access API (incubator).

## Java 15 (2020-09)
- Language: Text blocks (final); Sealed classes (preview); Records (2nd preview); Pattern matching for instanceof (2nd preview); Hidden classes.
- Runtime: ZGC becomes a product feature; removed Nashorn JS engine.

## Java 16 (2021-03)
- Language: Records (final); Pattern matching for instanceof (final); Sealed classes (2nd preview).
- APIs: Vector API (incubator); Foreign Linker API + Foreign-Memory Access (incubators); Unix-domain socket channels.
- Platform/Tools: jpackage (final); Alpine Linux, Windows/AArch64 ports; JDK sources move to GitHub.

## Java 17 (2021-09) — LTS
- Language: Sealed classes (final); Pattern matching for switch (preview).
- APIs/Runtime: Enhanced PRNGs; Foreign Function & Memory (incubator); macOS Metal rendering pipeline; context-specific deserialization filters.
- Platform: Strongly encapsulate JDK internals by default; Security Manager deprecated for removal.

## Java 18 (2022-03)
- Language: Pattern matching for switch (2nd preview).
- APIs/Tools: Simple web server; UTF-8 by default; JavaDoc code snippets; Internet-address resolution SPI.
- Incubators/Previews: FFM (2nd incubator); Vector API (3rd incubator).
- Deprecations: Finalization deprecated for removal.

## Java 19 (2022-09)
- Loom (concurrency): Virtual threads (preview); Structured concurrency (incubator).
- Language: Record patterns (preview); Pattern matching for switch (3rd preview).
- APIs: FFM (preview); Vector API (4th incubator); Linux/RISC-V port.

## Java 20 (2023-03)
- Loom: Virtual threads (2nd preview); Structured concurrency (2nd incubator); Scoped values (incubator).
- Language: Record patterns (2nd preview); Pattern matching for switch (4th preview).
- APIs: FFM (2nd preview); Vector API (5th incubator).

## Java 21 (2023-09) — LTS
- Loom (finalized): Virtual threads (final) — lightweight threads for massive concurrency.
- Language: Record patterns (final); Pattern matching for switch (final); String templates (preview); Unnamed classes & instance main methods (preview).
- Collections: Sequenced collections (List/Set/Map with defined encounter order API).
- Runtime/APIs: FFM (3rd preview); Scoped values (preview); Generational ZGC; Key Encapsulation Mechanism API (incubator).
- Platform: Prepare to disallow dynamic loading of agents.

## Java 22 (2024-03)
- Language/APIs (previews): String templates (2nd preview); Unnamed patterns & variables (2nd preview); Stream gatherers (preview); Class-File API (preview).
- Concurrency: Scoped values (2nd preview); Structured concurrency (2nd preview); Vector API (7th incubator).
- FFM: Foreign Function & Memory API (finalized).

## Java 23 (2024-09)
- Language/APIs (continued previews/incubators): String templates (3rd preview); Stream gatherers (2nd preview); Class-File API (2nd preview); Vector API (next incubator); KEM API (next incubator).
- Tooling/Docs: Markdown in JavaDoc comments (preview).
- GC/Performance: Ongoing improvements across GCs and JIT (incremental).

---
Tips
- Prefer an LTS (11, 17, 21) in production unless you have a reason to track six?monthly releases.
- For new concurrency work: target Java 21+ to use virtual threads; they dramatically simplify high-throughput I/O services.
- For native interop: adopt the finalized FFM API (Java 22+) instead of JNI for many cases.

---
## Appendix: Sequenced Collections (Java 21)

What they are
- New interfaces that unify “first/last” and “reverse order” operations across ordered collections and maps:
  - SequencedCollection<E>
  - SequencedSet<E> extends Set<E>
  - SequencedMap<K,V> extends Map<K,V>
- Apply only to types with a defined encounter order (insertion, access, or sorted order). Unordered types (e.g., HashSet/HashMap) do not implement them.

Core operations
- Collections/Sets (via SequencedCollection/SequencedSet):
  - addFirst(E), addLast(E)
  - getFirst(), getLast()
  - removeFirst(), removeLast()
  - reversed() — returns a live, reversed view (backed by the original)
- Maps (via SequencedMap):
  - putFirst(K,V), putLast(K,V)
  - firstEntry(), lastEntry()
  - pollFirstEntry(), pollLastEntry()
  - reversed() — live reversed view
  - sequencedKeySet(), sequencedValues(), sequencedEntrySet() — ordered views

Which JDK types implement them
- Lists: ArrayList, LinkedList, List.of(...) — implement SequencedCollection
- Sets with order: LinkedHashSet (insertion/access order), TreeSet (sorted order) — implement SequencedSet
- Maps with order: LinkedHashMap (insertion/access order), TreeMap (sorted order) — implement SequencedMap
- Unordered: HashSet/HashMap do NOT implement these interfaces

Examples (Java 21+)
- Reversing a list view
  List<Integer> nums = new ArrayList<>(List.of(1, 2, 3));
  List<Integer> rev = nums.reversed(); // live view
  rev.addFirst(0);           // nums becomes [0, 1, 2, 3]
  rev.removeLast();          // nums becomes [0, 1, 2]

- Using first/last on a LinkedHashSet
  Set<String> s = new LinkedHashSet<>(List.of("a", "b", "c"));
  SequencedSet<String> seq = (SequencedSet<String>) s;
  seq.addFirst("z");        // order: z, a, b, c
  String first = seq.getFirst(); // "z"
  String last = seq.getLast();   // "c"

- Working with a LinkedHashMap
  Map<String,Integer> m = new LinkedHashMap<>();
  SequencedMap<String,Integer> sm = (SequencedMap<String,Integer>) m;
  sm.putLast("a", 1);
  sm.putFirst("b", 2);     // order: b=2, a=1
  Map.Entry<String,Integer> e = sm.lastEntry(); // a=1
  SequencedMap<String,Integer> rev = sm.reversed(); // live reversed view

Notes and tips
- reversed() returns a view; changes reflect both directions. Copy if you need an independent list/map.
- Unmodifiable collections still throw UnsupportedOperationException on mutating operations.
- Prefer sequenced APIs over ad-hoc index/iterator hacks for clearer intent and fewer edge cases.

---
## Appendix: Java 11 Standard HTTP Client (HTTP/2, WebSocket)

Overview
- Replaces HttpURLConnection with a modern, async-friendly API.
- Supports HTTP/1.1 and HTTP/2 (with ALPN negotiation); includes a WebSocket client.
- Key types: java.net.http.HttpClient, HttpRequest, HttpResponse, BodyHandlers/BodyPublishers, WebSocket.

Create a client
- Default client:
  HttpClient client = HttpClient.newHttpClient();
- Custom builder:
  HttpClient client2 = HttpClient.newBuilder()
      .version(HttpClient.Version.HTTP_2)
      .followRedirects(HttpClient.Redirect.NORMAL)
      .connectTimeout(Duration.ofSeconds(5))
      // .proxy(ProxySelector.of(new InetSocketAddress("proxy", 8080)))
      // .authenticator(Authenticator.getDefault())
      // .sslContext(sslContext)
      .build();

Synchronous GET
HttpRequest req = HttpRequest.newBuilder(URI.create("https://example.com/api"))
    .GET()
    .timeout(Duration.ofSeconds(10))
    .build();
HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
int status = res.statusCode();
String body = res.body();

Asynchronous GET
HttpRequest req2 = HttpRequest.newBuilder(URI.create("https://example.com/items"))
    .build();
CompletableFuture<HttpResponse<String>> fut = client.sendAsync(req2, HttpResponse.BodyHandlers.ofString());
fut.thenApply(HttpResponse::body)
   .thenAccept(System.out::println)
   .join(); // wait for completion

POST JSON
String json = "{\"name\":\"Widget\",\"qty\":3}";
HttpRequest post = HttpRequest.newBuilder(URI.create("https://example.com/items"))
    .header("Content-Type", "application/json")
    .POST(HttpRequest.BodyPublishers.ofString(json))
    .build();
HttpResponse<String> postRes = client.send(post, HttpResponse.BodyHandlers.ofString());

Download/upload streaming
// Download to file
HttpRequest fileReq = HttpRequest.newBuilder(URI.create("https://example.com/file.bin")).build();
HttpResponse<Path> saved = client.send(fileReq, HttpResponse.BodyHandlers.ofFile(Path.of("file.bin")));

// Upload from file
HttpRequest upload = HttpRequest.newBuilder(URI.create("https://example.com/upload"))
    .POST(HttpRequest.BodyPublishers.ofFile(Path.of("file.bin")))
    .build();
HttpResponse<String> uploadRes = client.send(upload, HttpResponse.BodyHandlers.ofString());

HTTP/2 notes
- client.version(HTTP_2) requests HTTP/2; the actual protocol is negotiated with the server (falls back to HTTP/1.1 if not supported).
- Multiplexing shines with multiple concurrent sendAsync calls.

Timeouts, redirects, headers
- Connect timeout: set on client builder; per-request timeout: request.timeout(Duration).
- Redirect policy: Redirect.NEVER | NORMAL | ALWAYS.
- Headers: requestBuilder.header(k,v); response headers via res.headers().firstValue("...").

Proxy and authentication
- Configure proxy with .proxy(ProxySelector). Use .authenticator(...) for challenge-based auth.
- For simple Basic auth, add an Authorization header or provide an Authenticator.

Body handlers/publishers quick refs
- BodyHandlers: ofString(), ofByteArray(), ofFile(Path), ofInputStream().
- BodyPublishers: ofString(s), ofByteArray(b), ofFile(path), noBody().

WebSocket client (HTTP/1.1 upgrade)
HttpClient wsClient = HttpClient.newHttpClient();
CompletableFuture<WebSocket> wsFut = wsClient.newWebSocketBuilder()
    .connectTimeout(Duration.ofSeconds(5))
    .buildAsync(URI.create("wss://example.com/socket"), new WebSocket.Listener() {
      @Override public void onOpen(WebSocket ws) { ws.request(1); }
      @Override public CompletionStage<?> onText(WebSocket ws, CharSequence data, boolean last) {
        System.out.println("msg: " + data);
        ws.request(1); // demand next message
        return CompletableFuture.completedFuture(null);
      }
      @Override public CompletionStage<?> onClose(WebSocket ws, int status, String reason) {
        System.out.println("closed: " + status + ", " + reason);
        return CompletableFuture.completedFuture(null);
      }
      @Override public void onError(WebSocket ws, Throwable error) { error.printStackTrace(); }
    });
WebSocket ws = wsFut.join();
ws.sendText("hello", true);
ws.sendClose(WebSocket.NORMAL_CLOSURE, "bye");

Tips
- Blocking send() cooperates with virtual threads; for high fan-out use sendAsync().
- Always set timeouts for reliability; tune redirects and version as needed.
- For large bodies, prefer streaming handlers (ofFile/ofInputStream) to avoid loading into memory.
- HTTP/2 requires TLS ALPN on most servers; expect graceful fallback to HTTP/1.1.

---
## Appendix: Stream Gatherers (Java 22–23, Preview)

Overview
- Adds Stream.gather(Gatherer<In, ?, Out>) to perform stateful, intermediate transformations that can emit zero, one, or many outputs per input, while remaining lazy and pipeline-friendly.
- Ships as a preview feature in Java 22 and 23. The helper class java.util.stream.Gatherers provides common gatherers.

Why not just map/flatMap/collect?
- map: one output per input; no cross-element state.
- flatMap: many outputs per input; still stateless across elements.
- collect: terminal operation; computes a single result (or container) at the end.
- gather: intermediate, can maintain state across elements, emit results incrementally, and continue the pipeline.

Common use cases
- Running aggregates (running sum/min/max), moving averages.
- Sliding windows (fixed-size) or other windowed computations.
- Stateful filters or de-duplication by adjacency.
- Chunking/segmenting streams while keeping laziness.

Examples (Java 22+/preview)
- Running sum with scan
  var sums = Stream.of(1, 2, 3, 4)
      .gather(java.util.stream.Gatherers.scan(0, Integer::sum))
      .toList();
  // sums => [1, 3, 6, 10]

- Sliding window of size 3 (fixed-size windows)
  var windows = Stream.of(1, 2, 3, 4, 5)
      .gather(java.util.stream.Gatherers.windowFixed(3))
      .map(window -> window.stream().mapToInt(Integer::intValue).sum())
      .toList();
  // windows => [6 (1+2+3), 9 (2+3+4), 12 (3+4+5)]

Notes
- Preview feature: compile and run with --enable-preview and the matching --release or source/target. Example:
  // javac --release 22 --enable-preview MyApp.java
  // java  --enable-preview MyApp
- Gatherers produce live, flowing results; they are not terminal like collect.
- As with other stateful operations, consider parallel streams carefully; prefer sequential unless you know your gatherer is safe and beneficial in parallel.
- If you need a final summary value, gather can still be followed by collect or reduce.

When to use
- Prefer gatherers when you need cross-element state and incremental emission but still want to compose with the rest of the pipeline.
- Stick to map/flatMap/filter for simple stateless transforms; collect for terminal aggregation.
