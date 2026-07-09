# Dependency injection

Swift Spezi uses a runtime DI container backed by `SpeziAppDelegate` and the `@Dependency` property wrapper. Spezi-Kotlin uses **its own runtime DI graph** — the `Module` interface, the `Configuration { }` builder, and `dependency<T>()` / `requireDependency<T>()` consumption helpers. Hilt is being phased out and is documented at the bottom of this file under "Migration from Hilt" for readers maintaining legacy code.

The two systems are very close in spirit: both build a graph of named modules at startup and let consumers retrieve them by type. Translation from Swift to Spezi-Kotlin is more direct than the previous Hilt-based approach.

## Swift Spezi (source side)

```swift
final class HealthService: Module {
    @Dependency private var localStorage: LocalStorage

    func recordHeartRate(_ bpm: Int) async throws {
        // …
    }
}

class MyAppDelegate: SpeziAppDelegate {
    override var configuration: Configuration {
        Configuration {
            HealthService()
            LocalStorage()
        }
    }
}
```

The `@Dependency` wrapper resolves at runtime when the container builds. Missing dependencies surface at startup.

## Spezi-Kotlin (canonical destination)

### `Module` interface

Every Spezi module implements the one-method `Module` interface:

```kotlin
interface Module {
    fun configure() {}
}
```

`configure()` is an optional hook the framework calls after the dependency graph is fully resolved. Use it for side-effects that need every other module already registered (registering listeners, kicking off background work, validating configuration). Don't use it to *fetch* dependencies — that's what the module's own constructor or property delegates are for.

### `Configuration { … }` DSL

The application class implements `SpeziApplication` and exposes a `configuration: Configuration`. The DSL is a small `@SpeziDsl`-marked builder:

```kotlin
class MyApplication : Application(), SpeziApplication {
    override val configuration: Configuration = Configuration(standard = this) {
        module { LocalStorage() }
        module { HealthService(localStorage = dependency()) }
    }
}
```

Things to know:

- `Configuration(standard = this) { … }` constructs the configuration. The `standard` parameter is the `Standard` instance (often the application class itself, conforming to a domain-specific Standard interface). When unspecified it defaults to a built-in `DefaultStandard`.
- `module { factory }` registers a module. The factory has a `DependenciesGraph` receiver, so inside the factory body you can call `dependency<T>()` (lazy) or `requireDependency<T>()` (direct) to fetch other modules — they will resolve once the whole graph is constructed.
- `module<Interface> { ImplFactory() }` registers an implementation against an interface (callers can then `dependency<Interface>()`).
- `module(identifier = "name") { … }` lets you register multiple instances of the same type, distinguished by the identifier; consumers retrieve them with `dependency<T>("name")`.
- `include(otherConfiguration)` composes another `Configuration` into this one — useful for module groups that ship their own pre-built configuration.
- Per-module DSL extensions like `health { … }` and `accountConfiguration(service, storageProvider, configuration = { … })` are extension functions on `ConfigurationBuilder` that the respective modules contribute.

A real-world Configuration block looks like this:

```kotlin
class SampleApplication : Application(), SpeziApplication, HealthConstraint {
    override val configuration: Configuration = Configuration(standard = this) {
        module {
            Navigator(concurrency = dependency())
        }

        health {
            requestReadAccess(RecordType.bloodPressure, RecordType.weight)
            requestWriteAccess(RecordType.heartRate, RecordType.steps)
            collectRecord(
                recordType = RecordType.steps,
                start = CollectionMode.Automatic(pollingInterval = 5.seconds),
                continueInBackground = true,
            )
        }

        accountConfiguration(
            service = InMemoryAccountService(),
            storageProvider = InMemoryAccountStorageProvider(),
            configuration = {
                requires(key = AccountKeys.accountId)
                collects(key = AccountKeys.email)
                collects(key = AccountKeys.password)
            },
        )
    }
}
```

The framework calls `Spezi.configure(application = this)` automatically at app start; you do not invoke it manually.

### Consumption helpers

Four helpers cover all consumption sites:

```kotlin
inline fun <reified M : Module> dependency(identifier: String? = null): Lazy<M>
inline fun <reified M : Module> optionalDependency(identifier: String? = null): Lazy<M?>
inline fun <reified M : Module> requireDependency(identifier: String? = null): M
inline fun <reified M : Module> requireOptionalDependency(identifier: String? = null): M?
```

Use them like this:

```kotlin
class HealthService(
    private val localStorage: LocalStorage,
    private val concurrency: Concurrency,
) : Module {
    private val cache by dependency<Cache>()                 // lazy delegate; resolves on first access
    private val analytics by optionalDependency<Analytics>() // null if not registered
}

class MyActivity : ComponentActivity() {
    private val navigator by dependency<Navigator>()  // Activity-level access; no Hilt entry-point needed
}
```

The `identifier: String?` parameter lets you discriminate between multiple instances of the same module type:

```kotlin
Configuration(standard = this) {
    module(identifier = "primary") { Cache(/* ... */) }
    module(identifier = "secondary") { Cache(/* ... */) }
}

// at consumption:
private val primaryCache by dependency<Cache>(identifier = "primary")
```

### Module lifecycle

The order at app startup is:

1. The `Configuration { … }` builder body runs. Module *factories* are registered. **Modules are not yet constructed.**
2. The framework constructs the `DependenciesGraph` and calls each registered factory in dependency order. Factories may call `dependency<T>()` / `requireDependency<T>()` on the receiver — these resolve lazily once construction reaches the requested module.
3. Once every module has been constructed, the framework calls `configure()` on each one.

Two practical consequences:

- **Don't request dependencies inside the `Configuration { … }` builder body itself.** Only request them inside module factories or after construction is complete. A `dependency<X>()` call during the builder body will run before X is registered.
- **`configure()` is the right place to wire cross-module observers.** By the time `configure()` runs, every module's instance exists.

### Translation from `@Dependency`

Direct mapping:

```swift
final class HealthService: Module {
    @Dependency private var localStorage: LocalStorage
}
```

becomes

```kotlin
class HealthService(
    private val localStorage: LocalStorage,
) : Module
```

with the wiring happening in the `Configuration { module { HealthService(localStorage = dependency()) } }` registration. If you prefer property-delegate consumption (closer to the Swift wrapper feel):

```kotlin
class HealthService : Module {
    private val localStorage by dependency<LocalStorage>()
}
```

Both are valid. Constructor parameters are clearer for the dependency graph; property delegates are clearer when the module's external API doesn't expose the dependency.

### Translation from Swift's optional `@Dependency`

```swift
@Dependency var analytics: Analytics?  // Swift: nil if not registered
```

```kotlin
private val analytics by optionalDependency<Analytics>()
```

`optionalDependency` returns `Lazy<M?>`; `requireOptionalDependency` is the eager non-lazy variant.

## What does not port one-to-one

- **`@EnvironmentObject`-style cross-cutting state.** SwiftUI views read environment objects implicitly. The Compose equivalent is **not** a DI-injected ViewModel — neither Hilt nor Spezi DI fans out values to descendant composables the way Environment does. Use `CompositionLocal` for genuinely cross-cutting state and pass parameters otherwise. See [state-management.md](state-management.md).
- **Lazy module registration mid-flight.** Swift Spezi can register modules at runtime mid-session. Spezi-Kotlin currently expects the full graph at startup via `Configuration { … }`. Compose feature gates by registering the candidate modules conditionally inside the builder, not by deferring registration.

## Migration from Hilt

The previous DI stack in Spezi-Kotlin was Dagger Hilt. Hilt usage in current code is now **legacy migration scaffolding** — it persists in a small set of locations that haven't yet been converted, plus a single area where Spezi DI doesn't yet have a story (ViewModels). When working in those areas, you'll see Hilt patterns; this section explains them and how they convert to Spezi-native DI.

### Hilt patterns you'll encounter in legacy code

```kotlin
@Module
@InstallIn(SingletonComponent::class)
class StorageModule {
    @Module
    @InstallIn(SingletonComponent::class)
    abstract class Bindings {
        @Binds
        internal abstract fun bindLocalStorage(impl: LocalStorageImpl): LocalStorage
    }
}

class HealthService @Inject constructor(
    private val localStorage: LocalStorage,
) : Module
```

`@Module` + `@Provides` / `@Binds` declares wiring; `@Inject constructor` consumes it; `@HiltAndroidApp` on the application class and `@AndroidEntryPoint` on Activities/Fragments enable the runtime.

### Bridging Spezi-native modules into Hilt-managed consumers

When a `@HiltViewModel` (the one place where Hilt is currently still required — see below) constructor-injects a Spezi-native module, the seam looks like this:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
class SampleAppModule {
    @Provides
    fun provideHealth(): Health = requireDependency()

    @Provides
    fun provideNavigator(): Navigator = requireDependency()
}
```

`requireDependency<T>()` walks the Spezi runtime graph; the `@Provides` function lets Hilt hand the result to a `@HiltViewModel`'s constructor. **This is migration scaffolding** — it lives only as long as those Hilt-managed consumers exist. Once a consumer migrates off Hilt, the bridge `@Provides` for it can be deleted.

### Conversion recipes

| Hilt construct | Spezi-native equivalent |
|---|---|
| `@Module class XModule { @Provides fun provideX(): X = X(/* deps */) }` | `Configuration { module { X(/* deps via `dependency()` */) } }` |
| `@Module abstract class XModule { @Binds abstract fun bind(impl: XImpl): X }` | `Configuration { module<X> { XImpl(/* deps */) } }` |
| `class Y @Inject constructor(x: X) : Module` | `class Y(private val x: X) : Module`, registered via `module { Y(x = dependency()) }` |
| `@Inject lateinit var z: Z` (field injection on Activities/Fragments) | `private val z by dependency<Z>()` (works in any class — no `@AndroidEntryPoint` needed) |
| `Provider<X>` for optional / lazy access | `dependency<X>()` is already lazy; for optional use `optionalDependency<X>()` |
| Multi-binding (`@IntoSet`, `@IntoMap`) | Multiple `module(identifier = "key1") { … }` registrations + collect via repeated `dependency<X>("key1")` calls, or wrap in a manager module |

### `@HiltViewModel` is the one exception

The 2 ViewModels currently in the codebase use `@HiltViewModel` + `@Inject constructor` because **Spezi-Kotlin does not yet have a ViewModel/Compose-lifecycle integration**. There is no `SpeziViewModel` base class, no factory composable equivalent to `hiltViewModel<T>()`, and no documented pattern for binding a Spezi-graph dependency into a Compose-lifecycle-aware ViewModel.

Until that gap closes:

```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val navigator: Navigator,
) : ViewModel()
```

remains the working shape. Pair it with a Hilt bridge module that bridges the Spezi-native `Navigator` (or whatever the ViewModel needs) into Hilt via the `requireDependency()` pattern shown above. New Hilt usage *outside* ViewModels should be avoided — it just creates more migration debt.

See [state-management.md](state-management.md) for the broader ViewModel discussion.

## Why these rules matter

Two trade-offs to make explicit:

1. **Spezi DI is the strategic direction.** The framework is converging on a single runtime graph (`Module` + `Configuration { }` + `dependency<T>()`). Adding new Hilt `@Module`s, `@Inject constructor`s, or bridges to non-ViewModel code means writing migration debt — work that has to be undone later.
2. **ViewModels are temporarily exempt.** Until Spezi-Kotlin grows a ViewModel integration story, `@HiltViewModel` is the legitimate path for Compose-lifecycle-aware state holders. Don't rewrite working `@HiltViewModel` classes for the sake of "purity"; do migrate them when a Spezi-native pattern lands.

When in doubt: if you're writing a non-ViewModel module or service, register it as a Spezi `Module` and consume via `dependency<T>()`. If you're writing a `@HiltViewModel`, accept the Hilt boundary, and bridge the Spezi-native modules it needs through a small `@Module class` with `@Provides fun = requireDependency()`.
