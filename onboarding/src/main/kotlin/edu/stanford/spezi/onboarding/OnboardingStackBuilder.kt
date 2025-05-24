package edu.stanford.spezi.onboarding

import androidx.compose.runtime.Composable

internal data class OnboardingStep(
    val id: String,
    val content: @Composable () -> Unit,
)

class OnboardingStackBuilder internal constructor() {
    internal val steps = mutableListOf<OnboardingStep>()

    fun step(id: String, content: @Composable () -> Unit) {
        steps.add(OnboardingStep(id, content))
    }
}
