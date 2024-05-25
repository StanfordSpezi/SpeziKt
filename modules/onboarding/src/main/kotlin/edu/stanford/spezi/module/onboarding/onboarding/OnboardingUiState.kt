package edu.stanford.spezi.module.onboarding.onboarding

/**
 * A sealed class representing the actions that can be performed on the onboarding screen.
 */
sealed class Action {
    data class UpdateArea(val areas: List<Area>) : Action()

    data object OnLearnMoreClicked : Action()

}

/**
 * The UI state for the onboarding screen.
 */
data class OnboardingUiState(
    val areas: List<Area> = emptyList(),
    val title: String = "Title",
    val subtitle: String = "Subtitle",
    val error: String? = null,
)