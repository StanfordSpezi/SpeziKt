package edu.stanford.spezi.core.navigation

interface NavigationEvent
sealed class DefaultNavigationEvent : NavigationEvent {
    data object RegisterScreen : DefaultNavigationEvent()
    data object LoginScreen : DefaultNavigationEvent()
    data object BluetoothScreen : DefaultNavigationEvent()
    data object InvitationCodeScreen : DefaultNavigationEvent()
    data object OnboardingScreen : DefaultNavigationEvent()
    data object SequentialOnboardingScreen : DefaultNavigationEvent()
    data object ConsentScreen : DefaultNavigationEvent()
}