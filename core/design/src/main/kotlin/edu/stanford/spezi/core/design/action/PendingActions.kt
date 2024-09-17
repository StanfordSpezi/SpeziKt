package edu.stanford.spezi.core.design.action

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember

/**
 * An immutable action holder data class to be used in UiStates and to be consumed in Compose
 * to indicate pending loading async states.
 *
 * Example usage:
 *
 * ```kotlin
 * // Screen actions
 * interface Action {
 *     data object LongOperation : Action
 * }
 *
 * // Screen ui state
 * data class UiState(
 *     val title: String,
 *     val pendingActions: PendingActions<Action> = PendingActions()
 * )
 *
 * val uiState = MutableStateFlow(UiState(title = "PendingActions"))
 *
 * // Adding pending action before executing a blocking operation and removing afterwards
 * fun onAction(action: Action) {
 *     when (action) {
 *         is Action.LongOperation -> {
 *             uiState.update { it.copy(pendingActions = it.pendingActions + action) }
 *             // perform blocking operation
 *             uiState.update { it.copy(pendingActions = it.pendingActions - action) }
 *         }
 *     }
 * }
 *
 * // Using isLoading inside compose
 * @Composable
 * fun PendingActions(uiState: UiState) {
 *     val isLoading = uiState.pendingActions.isLoading(action = Action.LongOperation)
 *     // rest of the screen
 * }
 *
 * ```
 */
@Immutable
data class PendingActions<T : Any>(
    private val actions: List<T> = emptyList(),
) {

    /**
     * Returns a new PendingActions instance by appending `action`
     * @param action action to be appended
     * @return A new PendingActions instance that contains existing actions plus `action`
     */
    operator fun plus(action: T) = PendingActions(actions + action)

    /**
     * Returns a new PendingActions instance by removing all actions of the same type as `action`
     * @param action action type to be removed
     * @return A new PendingActions instance that contains existing actions without `action`
     */
    operator fun minus(action: T) = PendingActions(
        actions = actions.filter { it != action }
    )

    /**
     * @param action action to be checked whether an action of the same type is contained
     * @return true if any other action of the same type is contained, false otherwise
     */
    @Composable
    fun contains(action: T): Boolean {
        return remember(this, action) {
            actions.any { it == action }
        }
    }
}
