# Module navigation

This module provides a structured approach to handling navigation events in the Spezi Framework. The
module defines navigation events and a navigator interface to manage navigation within the app.

## Components

### NavigationEvent

`NavigationEvent` is an interface that serves as a base for all navigation events. Any class that
represents a navigation event should implement this interface. The framework user can define
their own navigation events to custom routes by implementing this interface.

### CustomNavigationEvent

For
example [`AccountNavigationEvent`](../../modules/account/src/main/kotlin/edu/stanford/spezi/module/account/AccountNavigationEvent.kt)
is a sealed class that extends NavigationEvent. Each Spezi
Module provides a implementation of `NavigationEvent` so the user has access to all public available
navigation destinations.

### Navigator

`Navigator` is an interface that defines the contract for a navigator responsible for handling
navigation events:

```kotlin 
interface Navigator {
    val events: SharedFlow<NavigationEvent>

    fun navigateTo(event: NavigationEvent)
}
```

`events` are a SharedFlow of NavigationEvent objects. This flow emits navigation events to be
observed and acted upon from the APP implementation of the Navigator.

`navigateTo(event: NavigationEvent)` is a function that takes a NavigationEvent and triggers the
navigation to the corresponding screen.

### Usage

#### 1. NavigationEvent

Implement the NavigationEvent for your module. For example, the Account module provides the
`AccountNavigationEvent`:

```kotlin
sealed class AccountNavigationEvent : NavigationEvent {
   data class RegisterScreen(val isGoogleSignIn: Boolean) : AccountNavigationEvent()
   data class LoginScreen(val isAlreadyRegistered: Boolean) : AccountNavigationEvent()
}
```

You can also use all the public available navigation events from the Spezi Modules.

#### 2. NavGraph

Create the `NavGraph` for your app. For example, the App module provides the `AppNavGraph`:

```kotlin
fun NavGraphBuilder.mainGraph() {
   composable<Routes.RegisterScreen> {
      val args = it.toRoute<Routes.RegisterScreen>()
      RegisterScreen(args.isGoogleSignIn)
   }
}
```

#### 3. Navigator Implementation

Provide a `Navigator` implementation in your app module. You can also use the `DefaultNavigator`
implementation:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NavigationModule {
   @Provides
   @Singleton
   fun provideNavigator(): Navigator = DefaultNavigator()
}
```

#### 4. Routes

Provide `Routes` for your app:

```kotlin
@Serializable
sealed class Routes {

   @Serializable
   data class RegisterScreen(val isGoogleSignIn: Boolean) : Routes()
}
```

#### 5. AppNavigation

Provide a `AppNavigation` composable. Here is an example of how to implement the `AppNavigation` in
the App module. You can use
the `Navigator` interface to navigate to the desired screen, based on `Routes`:

```kotlin
@Composable
fun AppNavigation(navigator: Navigator) {
   val navController = rememberNavController()
   val coroutineScope = rememberCoroutineScope()
   LaunchedEffect(navigator) {
      coroutineScope.launch {
         navigator.events.collect { event ->
            when (event) {
               is OnboardingNavigationEvent.ConsentScreen -> navController.navigate(Routes.ConsentScreen)
            }
         }
      }
   }
   NavHost(
      navController = navController,
      startDestination = Routes.OnboardingScreen,
   ) {
      mainGraph()
   }
}
```

#### 6. Navigator

Use the `Navigator` interface in your app module to navigate to the desired screen. To do this
you can simply inject the `Navigator` in any class that needs to navigate to a different screen:

```kotlin
class DefaultOnboardingRepository @Inject constructor(
   private val navigator: Navigator
) : OnboardingRepository
```

and then call the `navigateTo` function with the desired `NavigationEvent`:

```kotlin
navigator.navigateTo(OnboardingNavigationEvent.SequentialOnboardingScreen)
```