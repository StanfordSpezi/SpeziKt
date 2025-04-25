package edu.stanford.spezi.modules.onboarding

import edu.stanford.spezi.modules.navigation.NavigationEvent

sealed class OnboardingNavigationEvent : NavigationEvent {

    data object InvitationCodeScreen : OnboardingNavigationEvent()
    data class OnboardingScreen(val clearBackStack: Boolean) : OnboardingNavigationEvent()
    data object SequentialOnboardingScreen : OnboardingNavigationEvent()
    data object ConsentScreen : OnboardingNavigationEvent()
}
