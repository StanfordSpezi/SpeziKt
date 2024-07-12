package edu.stanford.spezi.core.navigation

/**
 * Represents an event that triggers a navigation action.
 */
interface NavigationEvent {
    /**
     * Attempts to navigate to the previous screen in the back stack.
     */
    data object PopBackStack : NavigationEvent

    /**
     * Attempts to navigate up in the navigation hierarchy.
     */
    data object NavigateUp : NavigationEvent
}
