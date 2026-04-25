# Dependency injection

Swift Spezi uses a runtime DI container backed by `SpeziAppDelegate` and the `@Dependency` property wrapper. Kotlin uses Dagger Hilt — a code-generated, compile-time-validated container. Translation is not a one-to-one mechanical mapping; the two systems make different trade-offs about *when* errors surface and *how* injection sites are expressed.

## Swift Spezi

```swift
final class HealthService: Module {
    @Dependency private var localStorage: LocalStorage
    @Dependency private var firebase: ConfigureFirebase

    func recordHeartRate(_ bpm: Int) async throws {
        // …
    }
}

// AppDelegate
class MyAppDelegate: SpeziAppDelegate {
    override var configuration: Configuration {
        Configuration {
            HealthService()
            LocalStorage()
            ConfigureFirebase()
        }
    }
}
```

The `@Dependency` wrapper resolves at runtime when the module's container builds. Missing dependencies surface at startup, not at compile time.

## Kotlin Hilt

The straightforward translation: declare a Hilt `@Module`, install it in the right component, and use `@Provides` or `@Binds` to expose dependencies. Constructor injection is the default consumption pattern.

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
) : Module {
    suspend fun recordHeartRate(bpm: Int) {
        // …
    }
}
```

**`@Provides` vs `@Binds`:**
- `@Provides` is a function returning the bound type. Use when you need to construct the value (third-party types, parameterized constructors).
- `@Binds` is an `abstract` function declaring "interface I is implemented by class C". The compiler wires it; no runtime work. Prefer when you have an `@Inject`-able implementation class.

**Trade-off compared to Swift:** Hilt catches missing or duplicate bindings at compile time. The cost is more upfront wiring — annotation processors, `@HiltAndroidApp`, `@AndroidEntryPoint` on Activities/Fragments. Plan up front; you can't paper over circular wiring at runtime the way Swift's container can.

## Bridging a Spezi-style runtime graph into Hilt

The SpeziKt ecosystem keeps a Spezi-style runtime module graph alongside Hilt for modules that need late binding or runtime registration. Bridge with `@Provides`:

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

`requireDependency<T>()` walks the Spezi runtime graph and pulls the registered module. Hilt then wires the result wherever `Health` or `Navigator` is requested by `@Inject constructor`. **This is the recommended bridge pattern when you have something registered Spezi-style and a Hilt-using consumer.** It's not a circularity workaround — it's a deliberate seam.

## Injection sites

Swift's `@Dependency` is a property wrapper that can sit on any class field. Kotlin cannot inject into arbitrary property-wrapper-like sites freely. Plan injection points up front:

- **Constructor injection** is the default. Mark the class `@Inject constructor(...)`.
- **Field injection** is available with `@Inject lateinit var`, but only on classes Hilt knows about (Activities, Fragments, ViewModels via `@HiltViewModel`). Don't add `@AndroidEntryPoint` to arbitrary classes just to use field injection.
- **Function-scope injection** (Compose): use `hiltViewModel()` inside a composable to get a Hilt-managed ViewModel.

## What does *not* port one-to-one

- **`@EnvironmentObject`-style cross-cutting state**. SwiftUI views read environment objects implicitly. The Kotlin equivalent is *not* a Hilt-injected ViewModel — Hilt doesn't fan out to descendants the way Environment does. Use `CompositionLocal` for genuinely cross-cutting state and pass parameters otherwise. See [state-management.md](state-management.md).
- **Lazy module registration mid-flight**. Spezi can register modules at runtime; Hilt's component graph is fixed at compile time. If your Swift code registers modules dynamically, the Kotlin port either pre-declares all of them with feature-flag-style branching, or uses the Spezi-runtime-graph pattern above with `requireDependency`.
- **Optional dependencies**. Swift's `@Dependency var x: SomeType?` resolves to `nil` if absent. In Hilt, request `Provider<T>` or `Optional<T>` and check; or use the runtime graph pattern with an `optionalDependency<T>()` helper.

## Why these rules matter

The two trade-offs to make explicit when translating:

1. **Error timing.** Swift defers wiring errors to startup; Kotlin moves them to compile. A literal port of a Swift `@Dependency`-heavy module to Hilt sometimes produces a wiring graph that won't compile until you decide what's `@Binds` vs `@Provides` vs constructor-injected. Don't paper over it with `@Provides` everywhere — that throws away Hilt's biggest advantage.
2. **Injection sites.** Don't try to recreate Swift's "decorator anywhere" feel. Pick the injection style up front (constructor, field, function-scope) and stick with it per consumer.

When in doubt, default to **constructor injection** plus `@Binds` for interface→impl wiring. Reach for `@Provides` only when constructing the value requires logic.
