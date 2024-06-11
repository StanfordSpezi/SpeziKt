package edu.stanford.spezi.module.onboarding.sequential

data class SequentialOnboardingData(
    val steps: List<Step>,
    val actionText: String,
    val onAction: () -> Unit,
)
