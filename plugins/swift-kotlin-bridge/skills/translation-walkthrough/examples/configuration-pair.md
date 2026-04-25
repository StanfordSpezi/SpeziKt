# Walkthrough: app configuration & module wiring

A representative Swift Spezi `SpeziAppDelegate` and its Kotlin counterpart that bridges Spezi's runtime module graph into Hilt. Use this as a teaching example for the dependency-injection rule and the order-of-translation process rule.

The Swift snippet below mirrors the conventional shape of a Spezi-Swift app delegate. The Kotlin snippet mirrors the conventional shape of a Spezi-Kotlin app module that bridges into Hilt. Neither is a copy of any specific file — both are representative of the patterns each ecosystem uses today.

## Swift side — `SampleAppDelegate.swift`

```swift
import Spezi
import SpeziAccount
import SpeziHealth
import SpeziLocalStorage

final class SampleAppDelegate: SpeziAppDelegate {
    override var configuration: Configuration {
        Configuration {
            Health()
            Account()
            LocalStorage()
        }
    }
}

final class HomeViewModel {
    @Dependency private var health: Health
    @Dependency private var account: Account

    func currentHeartRate() async throws -> Int {
        guard let user = account.signedInUser else {
            throw HomeError.notSignedIn
        }
        return try await health.heartRate(for: user.id)
    }
}
```

### What's happening

The app delegate registers three modules in a Spezi container at startup. `HomeViewModel` consumes two of them via the `@Dependency` property wrapper. Resolution happens at runtime when the container builds; missing modules surface as a startup error.

## Kotlin side — `SampleAppModule.kt` and consumer

```kotlin
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.stanford.spezi.core.requireDependency
import edu.stanford.spezi.health.Health
import edu.stanford.spezi.account.Account
import javax.inject.Inject

@Module
@InstallIn(SingletonComponent::class)
class SampleAppModule {
    @Provides
    fun provideHealth(): Health = requireDependency()

    @Provides
    fun provideAccount(): Account = requireDependency()
}

class HomeViewModel @Inject constructor(
    private val health: Health,
    private val account: Account,
) {
    suspend fun currentHeartRate(): Result<Int> = runCatching {
        val user = account.signedInUser ?: error("Not signed in")
        health.heartRate(user.id)
    }
}
```

The Spezi runtime module graph is set up elsewhere (in the app's `Application` subclass or a Spezi configuration block) before Hilt's first injection request. The `SampleAppModule` bridges Spezi-registered modules into Hilt by calling `requireDependency<T>()` inside `@Provides` functions. Hilt then wires the result wherever a consumer requests `Health` or `Account` via `@Inject constructor`.

## Walkthrough

### `SpeziAppDelegate.configuration` → Hilt `@Module` + Spezi runtime registration

- **Idiom**: Swift `SpeziAppDelegate` exposes a `configuration: Configuration` block listing modules to register at startup.
- **Rule**: dependency-injection — `SpeziAppDelegate` + `@Dependency` (runtime resolution) becomes Hilt `@Module` + `@Provides` / `@Binds` (compile-time validation), with a Spezi-runtime-graph bridge for late-bound modules.
- **Kotlin shape**: A Hilt `@Module` annotated with `@InstallIn(SingletonComponent::class)` exposes each module via a `@Provides` function. Modules registered in Spezi's runtime graph are pulled into Hilt with `requireDependency<T>()`.
- **Why**: a literal port of the Swift configuration block to Hilt would put every module into a single `@Provides` function, dropping Hilt's compile-time validation. Splitting registration (Spezi runtime graph at app init) from consumption-time wiring (`@Provides` in a Hilt module) preserves both: app code declares modules once, and Hilt enforces that every consumer's dependencies resolve at compile time.

### `@Dependency` consumption → constructor injection

- **Idiom**: Swift `@Dependency private var health: Health` is field injection on any class.
- **Rule**: dependency-injection — Kotlin cannot inject into arbitrary property-wrapper-like sites freely. Plan injection points up front; default to constructor injection.
- **Kotlin shape**: `class HomeViewModel @Inject constructor(private val health: Health, private val account: Account)`.
- **Why**: field injection in Hilt requires `@AndroidEntryPoint` on the host class, which is meaningful only for Android lifecycle types (Activity, Fragment, View). A plain class like `HomeViewModel` should use constructor injection — both because it's idiomatic and because it avoids `lateinit var` initialization-order pitfalls.

### Optional dependencies

- The Swift code uses `account.signedInUser` and reads `nil` when no user is signed in. That's fine on both sides: it's an optional return *from a method*, not an optional *dependency*.
- If the Swift had been `@Dependency var optionalThing: SomeType?` (the dependency itself optional), the Kotlin port would use `Provider<SomeType>` or wrap the Spezi bridge with `requireOptionalDependency<SomeType>()` instead of `requireDependency`. **In this example there's nothing to translate on that axis.**

### Async / error model

- **Idiom**: Swift `async throws` returning `Int`.
- **Rule**: concurrency — `async/await` becomes `suspend` + `CoroutineScope`; errors become `Result` or thrown exceptions at the boundary.
- **Kotlin shape**: `suspend fun currentHeartRate(): Result<Int> = runCatching { ... }`.
- **Why**: Kotlin doesn't declare exceptions in signatures. Returning `Result<T>` at the boundary makes the failure mode explicit at the call site, which mirrors what Swift's `try` does at every call site of a `throws` function. The caller in Kotlin gets the same "you must handle the failure" affordance, just at a different syntactic point.

### Scope ownership (implicit but important)

- **Idiom**: Swift's `async throws` callers spawn the work in a `Task` from the view layer.
- **Rule**: concurrency — Kotlin requires an injected `CoroutineScope`; don't conjure one with `GlobalScope` or `CoroutineScope(Dispatchers.IO)`.
- **Kotlin shape**: not visible in this snippet, but the caller of `HomeViewModel.currentHeartRate()` would launch in `viewModelScope` (if the consumer is a Compose ViewModel) or in an injected application-scoped scope (if the consumer is long-lived).
- **Why**: this is the rule that ports most often get wrong. Naming it explicitly here keeps the reader from missing it.

## Customization surface

The Swift side's `Configuration { ... }` block is itself the customization point — apps add or remove modules to compose the framework. On the Kotlin side, the equivalent customization happens at two layers:

1. The Spezi runtime graph (where modules are registered) — fully controlled by app code.
2. The Hilt `@Module` (where the bridge is declared) — extended by adding more `@Provides` functions.

No Environment-style values are surfaced through this configuration code, so there is no SwiftUI customization-surface loss to flag.

## Wins on the Kotlin side

- **Compile-time validation.** Forgetting to register `Account` in the Hilt module fails compilation with a clear "missing binding" error, where the Swift side would only catch it on first launch.
- **Constructor-injected consumers.** `HomeViewModel`'s dependencies are visible at the call-site of its constructor; a reader doesn't need to scan every property declaration to find what the class depends on.

## What this walkthrough is *not* showing

- The setup of Spezi's runtime module graph (in the app's `Application` subclass) — that's app-level boilerplate, not a translation choice per se.
- Hilt's component hierarchy (`SingletonComponent` vs `ActivityComponent` etc.). For framework-level Spezi modules, `SingletonComponent` is the right install scope; per-screen wiring lives elsewhere.

## Why this example is a good teaching case

Three rules from the bridge skill collide in the same translation: dependency-injection (the bridge pattern), concurrency (`async throws` → `suspend` + `Result`), and the implicit scope-ownership rule. A reader who works through this example sees how the rules compose, and why translating any one of them in isolation would produce code that compiles but loses something.
