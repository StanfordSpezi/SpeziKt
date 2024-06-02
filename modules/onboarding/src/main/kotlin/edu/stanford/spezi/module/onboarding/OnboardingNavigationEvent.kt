package edu.stanford.spezi.module.onboarding

import edu.stanford.spezi.core.navigation.NavigationEvent

sealed class OnboardingNavigationEvent : NavigationEvent {

    data object InvitationCodeScreen : OnboardingNavigationEvent()
    data object OnboardingScreen : OnboardingNavigationEvent()
    data object SequentialOnboardingScreen : OnboardingNavigationEvent()
    data object ConsentScreen : OnboardingNavigationEvent()
}