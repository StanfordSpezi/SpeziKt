package edu.stanford.spezi.modules.onboarding.sequential

data class SequentialOnboardingData(
    val steps: List<Step>,
    val actionText: String,
    val onAction: () -> Unit,
)
