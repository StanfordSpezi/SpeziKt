package edu.stanford.spezi.module.onboarding.sequential

class SequentialOnboardingData(
    val steps: List<Step>,
    val actionText: String,
    val onAction: () -> Unit
)