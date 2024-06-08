package edu.stanford.spezi.module.account

import edu.stanford.spezi.core.navigation.NavigationEvent

sealed class AccountNavigationEvent : NavigationEvent {
    data class RegisterScreen(val isGoogleSignIn: Boolean) : AccountNavigationEvent()
    data class LoginScreen(val isAlreadyRegistered: Boolean) : AccountNavigationEvent()
}
