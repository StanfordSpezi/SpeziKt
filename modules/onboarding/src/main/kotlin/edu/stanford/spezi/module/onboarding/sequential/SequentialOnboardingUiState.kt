package edu.stanford.spezi.module.onboarding.sequential

/**
 * A data class that represents the current ui state of the sequential onboarding screen.
 */
data class SequentialOnboardingUiState(
    val steps: List<Step> = emptyList(),
    val currentPage: Int = 0,
    val pageCount: Int = steps.size,
    val actionText: String = "Start",
)

/**
 * Button event that can be triggered by the user in sequential onboarding screen.
 */
enum class ButtonEvent {
    FORWARD, BACKWARD
}

/**
 * A sealed interface that represents the actions that can be triggered in the sequential onboarding screen.
 */
sealed interface Action {
    data class UpdatePage(val event: ButtonEvent) : Action

    data class SetPage(val page: Int) : Action
}