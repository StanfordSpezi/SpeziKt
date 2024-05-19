# Testing module

This module provides tools and utilities useful for testing. It includes components or functions that facilitate the testing.
Additionally, it exposes `api` of `libs.bundles.unit.testing` bundle.

### Dependency
```gradle
dependencies {
    testImplementation(project(":core:testing"))
}
```

### Components

- `CoroutineTestRule` - A JUnit Test Rule that sets the main coroutine dispatcher to a [TestDispatcher] for unit testing. 
This rule allows replacing the main dispatcher with a test dispatcher, which can be controlled during tests.
- `TestDispatchersProvider` - Test implementation of `DispatchersProvider` that provides a single test dispatcher 
for all coroutine contexts. This implementation is useful for unit testing, for components that require a [DispatchersProvider] dependency.
- `SpeziTestScope` - A global function that returns a `TestScope` with a specific `CoroutineContext`, defaulting to `UnconfinedTestDispatcher` if no context is provided.
- `verifyNever`, `coVerifyNever` - Global helper functions that verify that a specific interaction with a mock object never occurred
