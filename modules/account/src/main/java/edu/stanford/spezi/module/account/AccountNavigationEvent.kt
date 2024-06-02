package edu.stanford.spezi.module.account

import edu.stanford.spezi.core.navigation.NavigationEvent

sealed class AccountNavigationEvent : NavigationEvent {
    data object RegisterScreen : AccountNavigationEvent()
    data object LoginScreen : AccountNavigationEvent()

}