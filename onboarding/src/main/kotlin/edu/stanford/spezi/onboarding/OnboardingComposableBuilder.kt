package edu.stanford.spezi.onboarding

import androidx.compose.runtime.Composable

data class OnboardingStep(
    val identifier: String,
    val composable: @Composable () -> Unit,
)

data class OnboardingComposableBuilder(
    var list: MutableList<OnboardingStep>,
) {
    fun step(id: String, composable: @Composable () -> Unit) {
        list.add(OnboardingStep(id, composable))
    }
}

fun buildOnboardingSteps(
    build: OnboardingComposableBuilder.() -> Unit,
): List<OnboardingStep> {
    val builder = OnboardingComposableBuilder(mutableListOf())
    build(builder)
    return builder.list
}
