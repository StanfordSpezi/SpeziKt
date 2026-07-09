---
name: spezi-module-scaffold
description: Scaffolds a new Spezi-Kotlin module that matches current SpeziKt conventions. Use when the user asks to create a Spezi module, scaffold a SpeziKt module, set up a new SpeziKt subproject, or add a new module to the SpeziKt build.
---

# Scaffold a Spezi-Kotlin module

Generate a new module that fits current Spezi-Kotlin conventions: convention plugins, namespacing, dependency declarations, a Spezi-native `Module` class registered in the `Configuration { }` builder, and the module-lifecycle hook. New modules **do not use Hilt** — Spezi has its own runtime DI graph (the `Module` interface + `Configuration { }` + `dependency<T>()` consumption helpers) which is the strategic direction. Hilt remains in current code only as legacy migration scaffolding (and for ViewModels, where Spezi-native integration is still pending). See the "Legacy Hilt module shape" section near the end for the patterns you'll see when editing pre-migration code.

## Inputs you need

Ask the user (or infer if obvious):

1. **Module name** — short, lowercase, hyphen-separated. Examples: `health`, `account-firebase`, `ui-validation`. The name becomes the Gradle project (`:<name>`), the directory at the repo root, and part of the package path.
2. **Capabilities** — which convention plugins should apply, beyond the default `spezi.library`?
   - `spezi.compose` — Jetpack Compose UI module.
   - `spezi.serialization` — uses kotlinx.serialization (only when needed).
   - `spezi.desugaring` — Java time / backported APIs (only when minSdk-driven).
   - `spezi.hilt` — **legacy only.** Do not apply for new modules. Spezi-native DI is the canonical path; the only reason to apply `spezi.hilt` today is when editing a module that already uses Hilt.
3. **Module dependencies** — which other Spezi modules does this depend on? Examples: `:core`, `:foundation`, `:ui`. Treat `api` vs `implementation` intentionally — `api` re-exports the dependency to consumers.
4. **Optional first class** — name of an initial `Module` implementation. The skill can stop at the build script if the user prefers an empty src tree.

## What the scaffold produces

```
<module-name>/
├── build.gradle.kts
└── src/
    └── main/
        └── kotlin/
            └── edu/
                └── stanford/
                    └── spezi/
                        └── <package-segment>/
                            └── (optional Module-implementing class)
```

The `<package-segment>` follows the module name with hyphens converted to dots: `account-firebase` → `edu.stanford.spezi.account.firebase`.

The skill also adds the module to `settings.gradle.kts` by inserting `include(":<module-name>")` in alphabetical order in the existing sorted list.

## Templates

### `build.gradle.kts` — minimal Spezi-native module

```kotlin
plugins {
    alias(libs.plugins.spezi.library)
}

android {
    namespace = "edu.stanford.spezi.<package-segment>"
}

dependencies {
    api(project(":foundation"))
    api(project(":core"))            // for Module interface, Configuration, dependency<T>()
}
```

### `build.gradle.kts` — Compose UI module

```kotlin
plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.compose)
}

android {
    namespace = "edu.stanford.spezi.<package-segment>"
}

dependencies {
    api(project(":ui-theme"))
    implementation(project(":foundation"))
    androidTestImplementation(project(":testing-ui"))
}
```

### `build.gradle.kts` — module with kotlinx.serialization

```kotlin
plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.compose)
    alias(libs.plugins.spezi.serialization)
}

android {
    namespace = "edu.stanford.spezi.<package-segment>"
}

dependencies {
    api(project(":core"))
    api(project(":foundation"))
    api(libs.kotlinx.serialization.json)
    implementation(project(":core-coroutines"))
}
```

### `settings.gradle.kts` insertion

Open `settings.gradle.kts`. Find the existing block of `include(":xxx")` lines (kept sorted). Insert `include(":<module-name>")` in alphabetical order. Do not move other lines. Do not introduce typesafe project accessors — the codebase consistently uses the string form.

### Spezi-native module — canonical class shape

```kotlin
package edu.stanford.spezi.<package-segment>

import edu.stanford.spezi.core.Module

class <ModuleName>(
    private val foo: Foo,
    private val bar: Bar,
) : Module {

    override fun configure() {
        // Optional. Runs after every module in the dependency graph has been constructed.
        // Use it to wire cross-module observers, validate configuration, or kick off background work.
        // Do not request dependencies here — they were supplied via the constructor or property delegates.
    }
}
```

If the module needs to fetch dependencies lazily (e.g., to break a construction-order issue, or because the dependency is genuinely optional), use property delegates:

```kotlin
class <ModuleName> : Module {
    private val foo by dependency<Foo>()                  // lazy; resolves on first access
    private val analytics by optionalDependency<Analytics>() // null if not registered
}
```

### Registration in the application's `Configuration { }` block

```kotlin
class MyApplication : Application(), SpeziApplication {
    override val configuration: Configuration = Configuration(standard = this) {
        module { <ModuleName>(foo = dependency(), bar = dependency()) }
    }
}
```

If the module exposes an *interface* whose implementation should be swappable, register against the interface:

```kotlin
Configuration(standard = this) {
    module<<Interface>> { <Interface>Impl(/* deps via dependency() */) }
}
```

Consumers then write `dependency<<Interface>>()` and receive the implementation.

### First class — minimal public type

If the user wants an empty starter rather than a `Module` implementation:

```kotlin
package edu.stanford.spezi.<package-segment>

class <ClassName>
```

No SPDX/BDHG license header — current Spezi-Kotlin files don't carry one. Don't add one.

## Module contract and lifecycle

Every Spezi module implements the one-method `Module` interface. The lifecycle is:

1. **`Configuration { }` body runs.** Module *factories* are registered. **Modules are not yet constructed.** Do not call `dependency<T>()` here — the module you're requesting hasn't been built.
2. **Graph resolution.** The framework constructs the `DependenciesGraph` and calls each registered factory in dependency order. Inside a factory body, `dependency<T>()` and `requireDependency<T>()` resolve as the requested module is reached.
3. **`configure()` callbacks.** Once every module has been constructed, the framework calls `configure()` on each one. By this point every module's instance exists; this is the right place for cross-module observers, listener registration, or validation.

The framework calls `Spezi.configure(application = this)` automatically at app start. You do not invoke it manually unless rebuilding the graph after a configuration change.

## Configuration DSL

The DSL is a small `@SpeziDsl`-marked builder. The available calls:

- `module { factory }` — register a module. The factory receiver is `DependenciesGraph`, so calls like `dependency()` inside the factory body resolve from the under-construction graph.
- `module<Interface> { ImplFactory() }` — register an implementation against an interface. Consumers can `dependency<Interface>()` and receive the concrete impl.
- `module(identifier = "name") { factory }` — register multiple instances of the same module type, distinguished by identifier. Consumers retrieve via `dependency<T>(identifier = "name")`.
- `include(otherConfiguration)` — compose another `Configuration` into the current one. Useful for module groups that ship a pre-built configuration.
- Per-module DSL extensions (`health { … }`, `accountConfiguration(service, storageProvider, configuration = { … })`) — extension functions on `ConfigurationBuilder` contributed by the respective modules.

The `@SpeziDsl` `@DslMarker` annotation prevents accidental scope leakage between nested builders. When you write a new builder DSL (for a sub-DSL inside your module), annotate the builder class with `@SpeziDsl`.

## Conventions you must preserve

- **Namespace = package**: `android.namespace = "edu.stanford.spezi.<package-segment>"` and the source path mirror it: `src/main/kotlin/edu/stanford/spezi/<package-segment>/`.
- **Project string form**: `implementation(project(":x"))`, not `implementation(projects.x)`.
- **`api` vs `implementation`**: re-export with `api` only when downstream consumers should see the dependency transitively. Default to `implementation`.
- **Sort `settings.gradle.kts` `include(...)` block alphabetically.** Android Studio's `Edit → Sort Lines` is the canonical approach.
- **Sort `[versions]`, `[libraries]`, `[plugins]`, `[bundles]` blocks in `gradle/libs.versions.toml`** if you add anything there.
- **`:core-logging` is auto-injected** as a dependency of every module by the framework's convention plugin. Don't add it as an explicit dependency.
- **Logger tag naming**: when a module ships its own logger factory, prefer `"Spezi<ModuleName>"` (e.g., `"SpeziHealth"`, `"SpeziAccount"`, `"SpeziCoreLogger"`). Never derive tags from class names — minification renames classes in release builds.

## What you do not scaffold

- **No `spezi.hilt` plugin application** for new modules. Spezi-native DI is canonical; Hilt is legacy.
- **No SPDX/BDHG license headers** — current files don't have them.
- **No SpeziAppDelegate-equivalent boilerplate** — Spezi-Kotlin uses `SpeziApplication` + `Configuration { }` at the application layer, not per-module.
- **No `ConfigureFirebase` registration** — that initialization component does not exist in current Spezi-Kotlin code. Firebase-using modules wire up their dependencies directly.
- **No onboarding wiring** — no active onboarding module exists in current Spezi-Kotlin to integrate with.
- **No tests folder** — leave it to the user to add `src/test/kotlin/` and `src/androidTest/kotlin/` when they have tests to write. The convention plugin disables the `androidTest` variant for libraries until that folder exists, so adding it prematurely changes build behavior.

## Legacy Hilt module shape (for editing existing Hilt-bearing modules only)

A handful of modules in current Spezi-Kotlin still apply `spezi.hilt` and ship Hilt `@Module` files: most notably `:core-coroutines` (`CoroutinesModule`), `:storage-local` (`LocalStorageModule`), and `:storage-credential` (`StorageModule`). The `:ui` and `:ui-markdown` modules also still apply `spezi.hilt`, mostly as residual configuration. **All of these are migration targets** — they should eventually be re-expressed as Spezi-native `Module` registrations.

When you need to edit a module that already uses Hilt (because the migration to Spezi-native hasn't happened yet for that module), the canonical Hilt shape is a top-level `@Module class` with optional nested `@Binds`-bearing `abstract class Bindings`:

```kotlin
package edu.stanford.spezi.<package-segment>

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class <ModuleName>Module {

    @Module
    @InstallIn(SingletonComponent::class)
    abstract class Bindings {
        @Binds
        internal abstract fun bind<Interface>(impl: <Interface>Impl): <Interface>
    }

    @Provides
    fun provide<Thing>(): <Thing> = <Thing>Impl()
}
```

The nested `Bindings` class lets a single module mix `@Binds` (for `interface → impl` wiring) and `@Provides` (for things that need construction logic). Drop the `Bindings` block entirely if the module only has `@Provides`. Drop the outer `@Provides` block entirely if the module only has `@Binds`.

**Do not add new Hilt modules to brand-new modules.** Use the Spezi-native `Module` shape above instead.

## Legacy Hilt bridge (for `@HiltViewModel` consumers of Spezi-native modules)

ViewModels are the single area where Hilt is currently still required — Spezi-Kotlin does not yet have a ViewModel/Compose-lifecycle integration. When a `@HiltViewModel` constructor-injects a Spezi-native module, the seam looks like this:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
class <ModuleName>HiltBridge {
    @Provides
    fun provide<Thing>(): <Thing> = requireDependency()

    // Use requireOptionalDependency<T>() instead of requireDependency<T>() for optional dependencies.
}
```

`requireDependency<T>()` walks the Spezi runtime graph and pulls the registered module; the `@Provides` function lets Hilt hand the result to a `@HiltViewModel`'s constructor. **This is migration scaffolding** — once the consuming ViewModel migrates off `@HiltViewModel` (when the Spezi-native ViewModel pattern lands), the bridge `@Provides` can be deleted.

Do not write new Hilt bridges for non-ViewModel consumers. Non-ViewModel code should consume Spezi-native modules directly via `dependency<T>()` — no bridge needed.

## Verification

After scaffolding:

1. `./gradlew :<module-name>:assemble` — module compiles standalone.
2. `./gradlew :<module-name>:detekt` — detekt passes (warnings-as-errors mode).
3. The module appears in alphabetical order in `settings.gradle.kts`.
4. `android.namespace` matches the package path of any source files.
5. No SPDX header at the top of any scaffolded `.kt` file.
6. The scaffold does not apply `spezi.hilt` (unless explicitly editing legacy code).

## Step-by-step

1. Confirm module name, capabilities, and dependencies with the user. Confirm explicitly that no Hilt is required (`spezi.hilt` is legacy).
2. Create the module directory at the repo root: `<module-name>/`.
3. Write `<module-name>/build.gradle.kts` from the appropriate Spezi-native template above.
4. Create `src/main/kotlin/edu/stanford/spezi/<package-segment>/`.
5. (Optional) write a `Module`-implementing class (the canonical class shape above).
6. Show the user the `Configuration { module { … } }` registration snippet to add to their application's `SpeziApplication.configuration`.
7. Insert `include(":<module-name>")` into `settings.gradle.kts` in alphabetical order.
8. Run the verification checks above; report results.

## Proposing knowledge base improvements

During or after a session, if you encounter a pattern, edge case, correction, or worked example that would have made this skill more useful — and that seems likely to generalize beyond the current user's specific situation — write a proposal file to `~/.claude/proposals/spezi/skills/spezi-module-scaffold/`, creating any missing directories.

**Filename:** `YYYY-MM-DD-HHMMSS-<short-slug>.md`.

**Required contents** — YAML frontmatter for the structured fields, prose sections below:

```markdown
---
action: add | edit | deprecate | merge
target: <existing entry being modified — omit for `add`>
confidence: low | medium | high
---

# Proposed content

<the full text of the new or revised entry>

## Rationale

<why this would improve the skill>

## Trigger

<a brief, anonymized snippet of the interaction that prompted the proposal — strip names, identifiers, paths, and any sensitive specifics>
```

**When to propose:**

- Sparingly. Only when the insight seems genuinely reusable across users and projects.
- Never modify this skill's own files directly. Proposals are suggestions for human review, not live edits.
- If you are uncertain whether something is worth proposing, err on the side of not writing a proposal.
