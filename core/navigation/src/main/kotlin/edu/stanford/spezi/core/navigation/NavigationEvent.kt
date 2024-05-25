package edu.stanford.spezi.core.navigation

sealed class NavigationEvent {
    data object RegisterScreen : NavigationEvent()
    data object LoginScreen : NavigationEvent()
    data object BluetoothScreen : NavigationEvent()
    data object InvitationCodeScreen : NavigationEvent()
    data object OnboardingScreen : NavigationEvent()
    data object SequentialOnboardingScreen : NavigationEvent()
}