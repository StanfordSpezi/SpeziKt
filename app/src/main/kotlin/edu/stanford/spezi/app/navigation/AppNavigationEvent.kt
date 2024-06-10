package edu.stanford.spezi.app.navigation

import edu.stanford.spezi.core.navigation.NavigationEvent

sealed interface AppNavigationEvent : NavigationEvent {
    data object BluetoothScreen : AppNavigationEvent
}
