package edu.stanford.spezi.modules.account

import edu.stanford.spezi.modules.navigation.NavigationEvent

sealed class AccountNavigationEvent : NavigationEvent {
    data class RegisterScreen(
        val email: String = "",
        val password: String = "",
    ) : AccountNavigationEvent()

    data object LoginScreen : AccountNavigationEvent()
}
