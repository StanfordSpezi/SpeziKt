# Module navigation

This module provides a structured approach to handling navigation events in the SPEZI Framework. The
module defines navigation events and a navigator interface to manage navigation within the app.

## Components

### NavigationEvent

`NavigationEvent` is an interface that serves as a base for all navigation events. Any class that
represents a navigation event should implement this interface. The framework user can define
their own navigation events to custom routes by implementing this interface.

### DefaultNavigationEvent

`DefaultNavigationEvent` is a sealed class that extends NavigationEvent. It defines several data
objects representing common navigation destinations in the application provided by the SPEZI
Framework.

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