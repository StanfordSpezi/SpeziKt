package edu.stanford.spezi.module.account

import edu.stanford.spezi.core.navigation.NavigationEvent

sealed class AccountNavigationEvent : NavigationEvent {
    data class RegisterScreen(
        val isGoogleSignUp: Boolean = false,
        val email: String = "",
        val password: String = "",
    ) : AccountNavigationEvent()

    data object LoginScreen : AccountNavigationEvent()
}
