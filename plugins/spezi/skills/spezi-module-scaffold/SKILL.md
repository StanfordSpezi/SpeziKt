---
name: spezi-module-scaffold
description: Scaffolds a new Spezi-Kotlin module that matches current SpeziKt conventions. Use when the user asks to create a Spezi module, scaffold a SpeziKt module, set up a new SpeziKt subproject, or add a new module to the SpeziKt build.
---

# Scaffold a Spezi-Kotlin module

Generate a new module that fits SpeziKt's current conventions — convention plugins, namespacing, dependency declarations, and (optionally) a Hilt module shape that mirrors the framework's existing modules.

## Inputs you need

Ask the user (or infer if obvious):

1. **Module name** — short, lowercase, hyphen-separated. Examples: `health`, `account-firebase`, `ui-validation`. The name becomes the Gradle project (`:<name>`), the directory at the SpeziKt repo root, and part of the package path.
2. **Capabilities** — which convention plugins should apply, beyond the default `spezi.library`?
   - `spezi.compose` — Jetpack Compose UI module.
   - `spezi.hilt` — module participates in Hilt dependency injection (most do).
   - `spezi.serialization` — uses kotlinx.serialization (only when needed).
3. **Module dependencies** — which other Spezi modules does this depend on? Examples: `:core`, `:foundation`, `:ui`, `:account`. Treat `api` vs `implementation` intentionally — `api` re-exports the dependency to consumers.
4. **Optional first class to scaffold** — name of an initial public class. The skill can stop at the build script if the user prefers an empty src tree.

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
                            └── (optional first class .kt file)
```

The `<package-segment>` follows the module name with hyphens converted to dots: `account-firebase` → `edu.stanford.spezi.account.firebase`.

The skill also adds the module to `settings.gradle.kts` by inserting `include(":<module-name>")` in alphabetical order in the existing sorted list.

## Templates

### `build.gradle.kts` — minimal `spezi.library` module

```kotlin
plugins {
    alias(libs.plugins.spezi.library)
}

android {
    namespace = "edu.stanford.spezi.<package-segment>"
}

dependencies {
    api(project(":<dependency-1>"))
    implementation(project(":<dependency-2>"))
}
```

### `build.gradle.kts` — Compose UI module with Hilt

```kotlin
plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.compose)
    alias(libs.plugins.spezi.hilt)
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

### `build.gradle.kts` — module with serialization

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

### Hilt module shape (when `spezi.hilt` is applied)

If the module declares a Hilt module, follow the canonical shape used across the framework — a top-level `@Module class` with optional nested `@Binds`-bearing `abstract class Bindings`:

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

The nested `Bindings` class is a Hilt convention that lets a single `@Module` mix `@Binds` (for `interface → impl` wiring) and `@Provides` (for things that need construction logic). Drop the `Bindings` block entirely if the module only has `@Provides`. Drop the outer `@Provides` block entirely if the module only has `@Binds`.

### Module-graph bridging (when the module exposes a Spezi runtime-graph dependency to Hilt)

When a Spezi-registered module needs to be available to Hilt-using consumers, expose it via a `@Provides` function calling `requireDependency<T>()`:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
class <ModuleName>HiltBridge {
    @Provides
    fun provide<Thing>(): <Thing> = requireDependency()
}
```

This is the recommended bridge pattern — not a circularity workaround. Use it when the consumer is Hilt-managed and the provider is Spezi-runtime-graph-managed.

### First class — minimal public type

```kotlin
package edu.stanford.spezi.<package-segment>

class <ClassName>
```

No SPDX/BDHG license header — current SpeziKt files don't carry one. Don't add one. (If the repo later standardizes on a header, update this skill.)

## Conventions you must preserve

- **Namespace = package**: `android.namespace = "edu.stanford.spezi.<package-segment>"` and the source path mirror it: `src/main/kotlin/edu/stanford/spezi/<package-segment>/`.
- **Project string form**: `implementation(project(":x"))`, not `implementation(projects.x)`.
- **`api` vs `implementation`**: re-export with `api` only when downstream consumers should see the dependency transitively. Default to `implementation`.
- **Sort `settings.gradle.kts` `include(...)` block alphabetically.** This is enforced by team convention; Android Studio's `Edit → Sort Lines` is the canonical approach.
- **Sort the `[versions]`, `[libraries]`, `[plugins]`, and `[bundles]` blocks in `gradle/libs.versions.toml`** if you add anything there.

## What you do not scaffold

- **No SPDX/BDHG license headers** — current files don't have them.
- **No SpeziAppDelegate-equivalent boilerplate** — Spezi-Kotlin uses Hilt + a Spezi runtime graph that is bootstrapped at the application layer, not per-module.
- **No `ConfigureFirebase` registration** — that initialization component does not exist in current Spezi-Kotlin code. Firebase-using modules wire up their dependencies directly.
- **No onboarding wiring** — no active onboarding module exists in current Spezi-Kotlin to integrate with.
- **No tests folder** — leave it to the user to add `src/test/kotlin/` and `src/androidTest/kotlin/` when they have tests to write. The convention plugin disables the `androidTest` variant for libraries until that folder exists, so adding it prematurely changes build behavior.

## Verification

After scaffolding:

1. `./gradlew :<module-name>:assemble` — module compiles standalone.
2. `./gradlew :<module-name>:detekt` — detekt passes (warnings-as-errors mode).
3. The module appears in alphabetical order in `settings.gradle.kts`.
4. `android.namespace` matches the package path of any source files.
5. No SPDX header at the top of any scaffolded `.kt` file.

## Step-by-step

1. Confirm module name, capabilities, and dependencies with the user.
2. Create the module directory at the SpeziKt repo root: `<module-name>/`.
3. Write `<module-name>/build.gradle.kts` from the appropriate template above.
4. Create `src/main/kotlin/edu/stanford/spezi/<package-segment>/`.
5. (Optional) write a first class file.
6. (Optional) write a Hilt module file if the user requested DI scaffolding.
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
