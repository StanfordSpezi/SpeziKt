package edu.stanford.bdh.engagehf.navigation

import edu.stanford.spezi.modules.navigation.NavigationEvent

sealed interface AppNavigationEvent : NavigationEvent {
    data class AppScreen(val clearBackStack: Boolean) : AppNavigationEvent
    data class QuestionnaireScreen(val questionnaireId: String) : AppNavigationEvent
    data object ContactScreen : AppNavigationEvent
}
