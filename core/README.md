# Module core

The `core` module is the heart of the Spezi Android framework: a lightweight runtime and dependency-injection container that wires together the modules of a Spezi application. An application declares a `Configuration` of `Module`s (and other dependencies), Spezi builds a `DependenciesGraph` from it at startup, instantiates everything, and invokes `Module.configure()` on each module. A single `Standard` module orchestrates the application's data flow. This README also serves as the index for the Core & Foundation module group (see [Related Modules](#related-modules)).

## Components

- **`SpeziApplication`** — base interface an `Application` implements to expose its `configuration: Configuration`. The companion `SpeziApplication.configure(...)` builds (or rebuilds) the dependency graph; `clear()` tears it down. Spezi invokes this automatically at app startup.
- **`Module`** — base interface every Spezi module implements. The optional `configure()` hook runs after the whole graph is built.
- **`Standard` / `DefaultStandard`** — the key module that orchestrates data flow by meeting requirements defined by other modules. `DefaultStandard` is the no-op default used when none is supplied.
- **`Configuration`** — sealed interface holding the registered `standard` and dependency registry. Built via `Configuration(standard = ...) { ... }`; two configurations can be merged with the `+` operator (`Configuration.plus`).
- **`ConfigurationBuilder`** — `@SpeziDsl` builder exposing `module { }` (registers a `Module`, optionally keyed by `identifier`), `singleton { }` (lazily created, cached for the graph lifetime), `factory { }` (transient, re-created on every resolution), and `include(configuration)` to fold in another `Configuration`. `getCached` / `BuilderCacheKey` let extensions stash intermediate build state.
- **`DependenciesGraph`** — the resolved graph. `dependency<T>(identifier)` resolves a required dependency (throwing `SpeziError` if missing), `optionalDependency<T>(identifier)` returns `null` instead. When nothing is registered for a required type it auto-instantiates via a no-arg constructor, a `Context`-arg constructor, or a `DefaultInitializer`. Detects circular dependencies during resolution.
- **Top-level resolution helpers** — `dependency<T>()` returns a `Lazy<T>` delegate and `optionalDependency<T>()` a `Lazy<T?>`; `requireDependency<T>()` / `requireOptionalDependency<T>()` resolve eagerly against the global graph. Used throughout modules to inject collaborators.
- **`DefaultInitializer<T>`** — implemented by a type's companion object to provide a default `create(context)` instance when the type is requested but not explicitly registered.
- **`ApplicationModule`** — automatically registered module exposing the `application`, its `configuration` and `standard`, and `requireContext()` for the Android application `Context`.
- **`SpeziDsl`** — `@DslMarker` annotation shared by all Spezi DSL builders to prevent scope leakage.
- **`SpeziError` / `speziError(message, cause)`** — the framework's `Throwable` type and a `Nothing`-returning throw helper.

## Usage

Define a `Standard`, one or more `Module`s, and declare them in a `Configuration` on your `Application`:

```kotlin
class MyStandard : Standard {
    override fun configure() {
        // orchestrate cross-module data flow once the graph is built
    }
}

class AudioModule : Module {
    private val analytics: AnalyticsService by dependency()

    override fun configure() {
        analytics.track("audio_module_ready")
    }
}

class MyApplication : Application(), SpeziApplication {
    override val configuration: Configuration = Configuration(standard = MyStandard()) {
        module { AudioModule() }
        module<Onboarding> { OnboardingImpl() }
        module(identifier = "alternative-onboarding") { AlternativeOnboarding() }

        singleton { AnalyticsService(dependency()) }
        factory { RequestContext(dependency()) }

        include(configuration = externalConfiguration)
    }
}
```

Resolve dependencies anywhere via the graph helpers:

```kotlin
val audio: AudioModule = requireDependency()
val onboarding: Onboarding? = requireOptionalDependency()
val lazyStandard: MyStandard by dependency()
```

## Related Modules

- **`foundation`** (`edu.stanford.spezi.foundation`) — pure-Kotlin primitives with no framework dependencies: `ValueRepository<Anchor>`, a thread-safe, strongly-typed store keyed by `KnowledgeSource` types (`DefaultProvidingKnowledgeSource`, `ComputedKnowledgeSource`, `OptionalComputedKnowledgeSource`, with an `AlwaysCompute`/`Store` storage policy) anchored to a `RepositoryAnchor`; `TypeReference<T>` / `typeReference()` for capturing generic types at runtime; `ObjectIdentifier` for reference-identity keys; and `UUID()` factory helpers.
- **`core-coroutines`** (`edu.stanford.spezi.core.coroutines`) — `CoroutinesLauncher`, a wrapper around a `CoroutineScope` that exposes `launch`/`async` while withholding cancellation (plus `Flow.stateIn`/`launchIn` overloads and a `CoroutineScope.coroutinesLauncher` extension), and `Concurrency`, a `Module` providing the standard `main`/`default`/`io`/`unconfined` dispatchers and matching `SupervisorJob`-backed scopes.
- **`core-lifecycle`** (`edu.stanford.spezi.core.lifecycle`) — `AppLifecycle`, a `Module` that observes `ProcessLifecycleOwner` and exposes the foreground/background `State` as a `StateFlow`, plus `isInForeground`/`isInBackground` flags and `awaitForeground()`/`awaitBackground()` suspend helpers.
- **`core-logging`** (`edu.stanford.spezi.core.logging`) — `SpeziLogger` with inline `i`/`w`/`e` methods, obtained via the `speziLogger { }` / `groupLogger(tag) { }` property delegates that auto-derive the tag from the owning class. Configurable through `LoggerConfig` and a `LoggingStrategy` (`PRINT`, `TIMBER`, `LOG`); globally toggled via `SpeziLogger.setLoggingEnabled(...)`.
- **`core-time`** (`edu.stanford.spezi.core.time`) — `TimeProvider`, a `Module` abstracting `Instant`/`LocalTime`/`ZonedDateTime`/offset access for testability, and `DateFormatter`, a `Module` formatting `TemporalAccessor`s/`Date`s with `DateFormat` patterns (`MM_DD_YYYY`, `HH_MM`, or `Custom`) in UTC or the system zone.
- **`core-viewmodel`** (`edu.stanford.spezi.core.viewmodel`) — bridges AndroidX `ViewModel`s to the Spezi graph. Register them with the `ConfigurationBuilder.viewModels { viewModel { ... } }` DSL (`ViewModelsBuilderScope`); factory lambdas run in a `ViewModelFactoryScope` exposing `dependency()`, `optionalDependency()`, and `savedStateHandle()`. Retrieve them via `speziViewModel<VM>()` in composables or `speziViewModels`/`speziActivityViewModels` in Activities/Fragments.
