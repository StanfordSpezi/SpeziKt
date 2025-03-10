package edu.stanford.spezi.modules.onboarding.onboarding

data class OnboardingData(
    val areas: List<Area> = emptyList(),
    val title: String = "Title",
    val subTitle: String = "SubTitle",
    val continueButtonText: String = "Learn more",
    val continueButtonAction: () -> Unit = {},
)
