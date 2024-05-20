# Module coroutines

Module that provides access to different `CoroutineDispatcher`s and `CoroutineScope`s in coroutine-based components 
to manage concurrency and threading.

Access is enabled via `DispatchersProvider` component which is available in Hilt DI graph:

```kotlin
interface DispatchersProvider {
    fun main(): CoroutineDispatcher
    fun default(): CoroutineDispatcher
    fun io(): CoroutineDispatcher
    fun unconfined(): CoroutineDispatcher
}
```

It additionally provides four qualifiers to request the injection of a specific `CoroutineDispatcher` or `CoroutineScope` 
as shown in [Example usage](#example-usage).

### Dependency
```gradle
dependencies {
    implementation(project(":core:coroutines"))
}
```

### Example usage

```kotlin
class MyClass @Inject constructor(
    private val dispatchersProvider: DispatchersProvider,
    @Dispatching.IO private val ioDispatcher: CoroutineDispatcher,
    @Dispatching.Main private val mainScope: CoroutineScope,
) {
    
    suspend fun executeWithDispatcher() = withContext(ioDispatcher) {
        // Perform IO-bound operation here
    }
    
    suspend fun executeWithProvider() = withContext(dispatchersProvider.default()) {
        // Perform operation using a dispatcher from the provider
    }
    
    fun launchScope() {
        mainScope.launch {
            // Perform UI-related task here
        }
    }
}
```