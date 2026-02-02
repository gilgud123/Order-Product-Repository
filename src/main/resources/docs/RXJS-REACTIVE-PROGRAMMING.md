# Reactive Programming with RxJS

A practical guide to Reactive Programming concepts and how to use RxJS effectively, with Angular-focused examples.

## What is Reactive Programming?
Reactive Programming is a programming paradigm centered on asynchronous data streams and the propagation of change. Instead of pulling values (imperative style), you subscribe to streams (observables) and react as data arrives over time.

Key ideas:
- Data as streams (events, HTTP responses, user input)
- Declarative pipelines that transform streams
- Composability via operators
- Backpressure and cancellation via subscriptions

## RxJS Core Building Blocks
- **Observable**: Represents a stream of values over time. Can be finite (HTTP response) or infinite (user input, WebSocket).
- **Observer**: The consumer object with next/error/complete handlers.
- **Subscription**: Represents the execution; can unsubscribe to cancel.
- **Operators**: Functions that transform, filter, combine streams (map, filter, switchMap).
- **Subject**: Both an observable and an observer; multicasts values to multiple subscribers (e.g., `BehaviorSubject`, `ReplaySubject`).
- **Scheduler**: Controls concurrency and execution timing (advanced use).

## Common RxJS Types
- **Observable<T>**: Unicast by default; each subscription runs independently.
- **Subject<T>**: Multicast; manual emission via `.next(value)`.
- **BehaviorSubject<T>**: Like Subject but stores the latest value and emits it on new subscriptions.
- **ReplaySubject<T>**: Replays a buffer of past values to late subscribers.
- **AsyncSubject<T>**: Emits the last value upon completion.

## Essential Operators
- Creation: `of`, `from`, `interval`, `timer`, `fromEvent`, `defer`
- Transformation: `map`, `mergeMap`, `switchMap`, `concatMap`, `exhaustMap`
- Filtering: `filter`, `take`, `takeUntil`, `debounceTime`, `distinctUntilChanged`
- Combination: `combineLatest`, `withLatestFrom`, `merge`, `forkJoin`, `concat`
- Error handling: `catchError`, `retry`, `retryWhen`
- Multicasting/state: `share`, `shareReplay`, `publish`, `refCount`
- Utility: `tap`, `finalize`, `startWith`

## Choosing the Right Flattening Operator
- **switchMap**: Cancel previous inner observable when a new value arrives. Great for typeahead search or route changes.
- **mergeMap**: Run inner observables in parallel; order is not guaranteed. Use for concurrent requests.
- **concatMap**: Queue inner observables, run one after another. Use for ordered operations (e.g., sequential saves).
- **exhaustMap**: Ignore new values while the current inner observable is active. Use for login button to prevent double submits.

## Angular + RxJS Patterns
- **HttpClient** returns `Observable<T>`: chain operators for mapping, error handling, and cancellation.
- **Route data and params**: `ActivatedRoute.params` + `switchMap` to load data on param change.
- **Forms**: `formControl.valueChanges.pipe(debounceTime(300), distinctUntilChanged(), switchMap(...))` for typeahead.
- **Component state**: Use `BehaviorSubject` for component store and expose derived `Observables` with `map`/`combineLatest`.
- **Async pipe (`| async`)**: Subscribe in templates, auto-unsubscribe on destroy.
- **Unsubscription**: Prefer `takeUntil(destroy$)` pattern or `AsyncPipe` to avoid leaks.

## Practical Examples

### 1) Typeahead Product Search
```ts
search$ = new FormControl('');
results$ = this.search$.valueChanges.pipe(
  debounceTime(300),
  distinctUntilChanged(),
  switchMap(query => this.productsService.search(query, 0, 10)),
  catchError(() => of([]))
);
```

### 2) Load Product by Route Param
```ts
product$ = this.route.params.pipe(
  map(params => Number(params['id'])),
  switchMap(id => this.productsService.getById(id)),
  shareReplay(1)
);
```

### 3) Component Store with BehaviorSubject
```ts
private state$ = new BehaviorSubject({ page: 0, size: 10, query: '' });
readonly vm$ = this.state$.pipe(
  switchMap(({ page, size, query }) => this.productsService.list(page, size, query)),
  map(pageData => ({ pageData })),
  shareReplay(1)
);

updateQuery(query: string) {
  const s = this.state$.value;
  this.state$.next({ ...s, query, page: 0 });
}
```

### 4) Unsubscribe on Destroy
```ts
private destroy$ = new Subject<void>();

ngOnInit() {
  fromEvent(window, 'resize')
    .pipe(takeUntil(this.destroy$))
    .subscribe(() => this.onResize());
}

ngOnDestroy() {
  this.destroy$.next();
  this.destroy$.complete();
}
```

## Error Handling Patterns
- Use `catchError` close to the source of failure to map to safe values or rethrow.
- For retryable operations: `retry(3)` or `retryWhen` with a backoff strategy.
- In UI, surface user-friendly messages and avoid breaking the stream.

## Performance Tips
- Use `shareReplay(1)` to memoize hot streams (route data, expensive loads).
- Choose the right flattening operator to avoid unnecessary work.
- Leverage `distinctUntilChanged` to cut redundant updates.
- Prefer `OnPush` change detection and async pipe.

## Testing RxJS Code
- Use marble testing with `rxjs-marbles` or `jest-marbles` to simulate time.
- Test operator chains deterministically (debounce, retry, etc.).
- For Angular services, mock `HttpClient` and assert sequences of emissions.

## Glossary Quick Reference
- **Cold vs Hot**: Cold observables start producing values on subscription; hot observables produce values independently of subscribers.
- **Backpressure**: Strategies to handle fast-producer vs slow-consumer (e.g., throttle, buffer, sample).
- **Multicasting**: Sharing a single execution among multiple subscribers.

## When Not to Use RxJS
- Simple one-off async tasks can use `await/Promise`.
- Heavy class-based state may be simpler with local component state if streams add complexity.

## Summary
RxJS enables declarative, composable handling of async data. In Angular, it powers HTTP, forms, routing, and component state. Mastering observables and operators—especially the flattening operators—leads to robust, scalable UI architectures.

