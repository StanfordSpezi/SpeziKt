package edu.stanford.spezi.module.onboarding.spezi.flow

import androidx.compose.runtime.Composable

class OnboardingStackBuilder internal constructor() {
    internal val steps = mutableListOf<OnboardingNavigationStep>()

    fun step(id: String, content: @Composable () -> Unit) {
        steps.add(OnboardingNavigationStep(id, content))
    }
}
