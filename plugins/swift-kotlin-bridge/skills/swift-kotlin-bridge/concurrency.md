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

When the work outlives the screen — long-running uploads, sync, anything tied to app lifecycle — inject the framework's `Concurrency` module and call its named scope factory:

```kotlin
class SyncManager(
    private val concurrency: Concurrency,
) : Module {
    fun startSync() {
        concurrency.ioCoroutineScope().launch { /* … */ }
    }
}
```

Or with a lazy delegate:

```kotlin
class SyncManager : Module {
    private val concurrency by dependency<Concurrency>()

    fun startSync() {
        concurrency.ioCoroutineScope().launch { /* … */ }
    }
}
```

**Why this matters at port time:** if you translate `Task { … }` literally, the natural Kotlin shape is either `GlobalScope.launch { … }` (a memory leak waiting to happen) or `CoroutineScope(Dispatchers.IO).launch { … }` (an unowned scope that nothing cancels). Both compile. Both produce hard-to-debug lifecycle bugs. Plan scope ownership *before* you write the `launch`.

## Owned scopes via the `Concurrency` module

The framework's `Concurrency` module is registered automatically in the Spezi runtime DI graph and exposes named scope factory methods:

- `concurrency.mainCoroutineScope()` — `Dispatchers.Main` + `SupervisorJob`.
- `concurrency.mainImmediateCoroutineScope()` — `Dispatchers.Main.immediate` + `SupervisorJob`.
- `concurrency.ioCoroutineScope()` — `Dispatchers.IO` + `SupervisorJob`.
- `concurrency.defaultCoroutineScope()` — `Dispatchers.Default` + `SupervisorJob`.

Each call returns an **application-lifetime** `CoroutineScope` — installed as a Spezi singleton, never cancelled. Use them only for app-scoped work. ViewModels should use `viewModelScope` instead; that scope cancels when the ViewModel clears.

**This is the canonical scope source for new code in Spezi-Kotlin.** Don't conjure scopes via `CoroutineScope(Dispatchers.X)` and don't reach for `GlobalScope`.

## Lifecycle-aware scopes via `AppLifecycle`

For work that should follow process foreground/background — e.g., periodic data collection, long-running listeners, sync polling — observe `AppLifecycle.state: StateFlow<AppLifecycle.State>` from the framework's `:core-lifecycle` module and start/stop work accordingly:

```kotlin
class HealthCollector(
    private val appLifecycle: AppLifecycle,
    private val concurrency: Concurrency,
) : Module {
    private val scope = concurrency.ioCoroutineScope()

    override fun configure() {
        scope.launch {
            appLifecycle.state.collect { state ->
                when (state) {
                    AppLifecycle.State.Foreground -> startCollection()
                    AppLifecycle.State.Background -> stopCollection()
                }
            }
        }
    }
}
```

The `:health` module uses this exact shape: it pauses Health Connect data collection when the process backgrounds and resumes on foreground. The `AppLifecycle` module is itself in the Spezi runtime graph; consume it via `dependency<AppLifecycle>()` (or constructor injection from a registered factory).

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
Kotlin: dispatchers (`Dispatchers.Main`, `Dispatchers.IO`, etc.). Use `withContext(Dispatchers.Main) { … }` to switch, or pull a scope from the `Concurrency` module (`concurrency.mainCoroutineScope()` / `concurrency.mainImmediateCoroutineScope()`) and run on it explicitly.

When porting `@MainActor`-annotated UI code, the Kotlin equivalent is usually "this lives in a Composable" — UI in Compose runs on the main thread by default, and you don't decorate the function for it.

## Migration from Hilt: `@Dispatching.X` qualifiers and `CoroutinesModule`

Older Spezi-Kotlin code uses Hilt qualifiers to inject dispatchers and scopes:

```kotlin
class SyncManager @Inject constructor(
    @Dispatching.IO private val ioScope: CoroutineScope,
    @Dispatching.Main private val mainDispatcher: CoroutineDispatcher,
)
```

These are wired by `core-coroutines/CoroutinesModule.kt` (a Hilt `@Module`). The qualifiers are: `@Dispatching.Main`, `@Dispatching.Default`, `@Dispatching.IO`, `@Dispatching.Unconfined` — applied to either `CoroutineDispatcher` or `CoroutineScope` injection sites.

**This is legacy.** New code should consume the `Concurrency` module via Spezi-native DI (`dependency<Concurrency>()` or constructor injection from a registered factory), not via Hilt qualifiers. The Hilt qualifiers persist only because some existing components — most notably `LocalStorageImpl` — still use them; converting them is part of the broader Hilt → Spezi-native DI migration.

**Important behavior to know either way:** every scope produced by both `Concurrency` and `CoroutinesModule` is **application-lifetime** (built with `SupervisorJob()` and never cancelled). Use them for app-scoped work only. Don't expect them to cancel when a screen leaves; use `viewModelScope` for that.

## Why these rules matter

Coroutines and structured concurrency look like the same shape on paper. The two cliffs that ports fall off:

1. Forgetting that Kotlin scopes must be owned (no free `Task { … }`). Inject the scope, don't conjure it.
2. Translating `actor` as `class` and losing exclusivity. Pick `Mutex`, single-thread dispatcher, or `ReentrantLock` deliberately.

Both produce code that compiles, runs, and breaks slowly under load.
