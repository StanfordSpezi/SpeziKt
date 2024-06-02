package edu.stanford.spezi.core.navigation

interface NavigationEvent
sealed class DefaultNavigationEvent : NavigationEvent {
    data object BluetoothScreen : DefaultNavigationEvent()
}