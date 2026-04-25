# Concurrency

Swift's structured concurrency and Kotlin coroutines look superficially similar — both have suspendable functions, both have structured cancellation. The differences that matter when porting are in **scope ownership** and in the absence of a Kotlin `actor` equivalent.

## `async` / `await`

Swift:

```swift
func loadProfile(id: String) async throws -> Profile {
    let response = try await api.fetchProfile(id: id)
    return response.profile
}

// caller
Task {
    let profile = try await loadProfile(id: id)
    // …
}
```

Kotlin:

```kotlin
suspend fun loadProfile(id: String): Profile {
    val response = api.fetchProfile(id)
    return response.profile
}

// caller — every coroutine runs in *some* CoroutineScope
viewModelScope.launch {
    val profile = loadProfile(id)
    // …
}
```

Two structural differences:

1. **Errors.** Swift uses `throws` plus `try`; Kotlin throws exceptions but does not declare them in signatures. `runCatching { … }` or `try / catch` at the boundary is the equivalent of `try? await` / `try await`.
2. **Scope.** This is the one that bites translations.

## Scope: the rule that always trips ports

Swift can spawn a free `Task { … }` from any context:

```swift
@Observable
final class FeedViewModel {
    func refresh() {
        Task {
            try await loadFeed()  // unowned task
        }
    }
}
```

Kotlin **cannot do this idiomatically**. Coroutines must run in some `CoroutineScope`, and that scope is owned — by a `ViewModel`, by a singleton-scoped Hilt component, by a Compose `rememberCoroutineScope`, or by a manually-built scope you remember to cancel.

```kotlin
@HiltViewModel
class FeedViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
) : ViewModel() {
    fun refresh() {
        viewModelScope.launch {
            runCatching { feedRepository.loadFeed() }
                .onFailure { /* surface error */ }
        }
    }
}
```

When the work outlives the screen — long-running uploads, sync, anything tied to app lifecycle — inject an application-scoped `CoroutineScope` rather than reaching for `GlobalScope`:

```kotlin
class SyncManager @Inject constructor(
    @Dispatching.IO private val ioScope: CoroutineScope,
) {
    fun startSync() {
        ioScope.launch { /* … */ }
    }
}
```

**Why this matters at port time:** if you translate `Task { … }` literally, the natural Kotlin shape is either `GlobalScope.launch { … }` (a memory leak waiting to happen) or `CoroutineScope(Dispatchers.IO).launch { … }` (an unowned scope that nothing cancels). Both compile. Both produce hard-to-debug lifecycle bugs. Plan scope ownership *before* you write the `launch`.

## Cancellation

Both ecosystems support cooperative cancellation:

- Swift: `Task.checkCancellation()`, `Task.isCancelled`.
- Kotlin: `ensureActive()`, `isActive`, or simply suspending — every suspension point checks for cancellation.

Kotlin's structured concurrency cancels children when the parent scope cancels. That mirrors Swift's structured tasks, but the unit is the **scope**, not the function. If you build a `CoroutineScope` manually, you are responsible for cancelling it.

## `actor`

Swift's `actor` provides exclusive access to mutable state without explicit locks:

```swift
actor RateLimiter {
    private var calls: Int = 0
    func record() { calls += 1 }
}
```

Kotlin has no direct equivalent. Three options, in order of preference:

1. **`Mutex`** (suspend-friendly):

   ```kotlin
   class RateLimiter {
       private val lock = Mutex()
       private var calls: Int = 0
       suspend fun record() = lock.withLock { calls += 1 }
   }
   ```

2. **Single-threaded dispatcher** (use when you need to keep all access on one thread, e.g. interop with a non-thread-safe library):

   ```kotlin
   class RateLimiter(parent: CoroutineScope) {
       private val scope = parent + newSingleThreadContext("rate-limiter")
       private var calls: Int = 0
       suspend fun record() = withContext(scope.coroutineContext) { calls += 1 }
   }
   ```

3. **`ReentrantLock`** (only for non-suspending code):

   ```kotlin
   class RateLimiter {
       private val lock = ReentrantLock()
       private var calls: Int = 0
       fun record() = lock.withLock { calls += 1 }
   }
   ```

**Don't translate `actor` as a plain `class`.** That removes the exclusivity guarantee silently. The Swift type's *purpose* was thread-safety; pick a Kotlin construct that preserves it.

## `MainActor` and dispatchers

Swift: `@MainActor` confines work to the main thread.
Kotlin: dispatchers (`Dispatchers.Main`, `Dispatchers.IO`, etc.). Use `withContext(Dispatchers.Main) { … }` to switch, or inject a main `CoroutineDispatcher` via Hilt and run on it explicitly.

When porting `@MainActor`-annotated UI code, the Kotlin equivalent is usually "this lives in a Composable" — UI in Compose runs on the main thread by default, and you don't decorate the function for it.

## Why these rules matter

Coroutines and structured concurrency look like the same shape on paper. The two cliffs that ports fall off:

1. Forgetting that Kotlin scopes must be owned (no free `Task { … }`). Inject the scope, don't conjure it.
2. Translating `actor` as `class` and losing exclusivity. Pick `Mutex`, single-thread dispatcher, or `ReentrantLock` deliberately.

Both produce code that compiles, runs, and breaks slowly under load.
