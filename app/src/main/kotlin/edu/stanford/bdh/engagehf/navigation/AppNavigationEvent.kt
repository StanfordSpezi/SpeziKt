package edu.stanford.bdh.engagehf.navigation

import edu.stanford.spezi.core.navigation.NavigationEvent

sealed interface AppNavigationEvent : NavigationEvent {
    data class AppScreen(val clearBackStack: Boolean) : AppNavigationEvent
    data class QuestionnaireScreen(val questionnaireId: String) : AppNavigationEvent
}
