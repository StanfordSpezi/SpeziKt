# Module testing-core

## Overview

The `testing-core` module (`edu.stanford.spezi.testing.core`) provides the foundational test utilities for the Spezi Android libraries. It lets unit tests spin up a real `SpeziApplication` and configure the dependency-injection graph without the boilerplate of a production application. This module also acts as the index for the Spezi testing module group, which adds Compose UI, screenshot, and coroutine helpers on top of this core.

## Components

- **`TestSpeziApplication`** — An abstract `Application` that also implements `SpeziApplication`, exposing a `Configuration` built from a `Standard` and a `ConfigurationBuilder` scope. Intended to be created through the `testSpeziApplication` factory rather than directly.
- **`TestStandard`** — A minimal `Standard` implementation used as the default standard for tests that don't need a custom one.
- **`testSpeziApplication(standard, scope)`** — Factory that clears any prior `SpeziApplication` state, instantiates a configured `TestSpeziApplication`, registers it via `SpeziApplication.configure`, and returns it for cases where you need a reference to the application.
- **`testDependencies(scope)`** — Convenience wrapper that sets up the Spezi dependency graph for a test via a `ConfigurationBuilder` scope without exposing the underlying application. Use it when you only need the graph configured.

## Usage

Configure the Spezi dependency graph for a test:

```kotlin
import edu.stanford.spezi.testing.core.testDependencies
import org.junit.Test

class MyServiceTest {

    @Test
    fun `service resolves its dependencies`() {
        testDependencies {
            singleton { MyService() }
            module { MyRepository(dependency()) }
        }

        // exercise code that resolves from the Spezi graph
    }
}
```

When you need a reference to the application itself, use `testSpeziApplication` instead:

```kotlin
val application = testSpeziApplication {
    singleton { MyService() }
}
```

## Related Modules

- **`testing-ui`** (`edu.stanford.spezi.testing.ui`) — Compose UI testing helpers. Provides `ComposeContentActivity`, an `AppCompatActivity` that hosts swappable Composable content (via `setScreen`) inside `SpeziTheme`, plus `SemanticsNodeInteractionsProvider` extensions (`onNodeWithIdentifier`, `onNodeWithContent`, `onAllNodes`, `waitNode`) for locating nodes by Spezi `TestIdentifier` tags and content semantics.
- **`testing-screenshot`** (`edu.stanford.spezi.testing.screenshot`) — Paparazzi-based screenshot testing. The abstract `ScreenshotTest` base class wires up a `Paparazzi` rule (Pixel 6, light Material theme, 0.01 max diff) together with `MainDispatcherRule`, and offers a `screenshot { }` helper that renders content in `SpeziTheme` with `LocalInspectionMode` enabled.
- **`testing-concurrency`** (`edu.stanford.spezi.testing.concurrency`) — Coroutine test support. `MainDispatcherRule` is a JUnit `TestWatcher` that swaps the main dispatcher for a `TestDispatcher` (defaulting to `UnconfinedTestDispatcher`) before each test and resets it afterward.
