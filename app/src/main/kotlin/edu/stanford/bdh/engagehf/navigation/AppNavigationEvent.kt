package edu.stanford.bdh.engagehf.navigation

import edu.stanford.spezi.core.navigation.NavigationEvent

sealed interface AppNavigationEvent : NavigationEvent {
    data object BluetoothScreen : AppNavigationEvent
}
