package edu.stanford.spezi.module.account.login

import android.content.Context
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

data class UiState(
    val password: String = "",
    val email: String = "",
    val passwordVisibility: Boolean = false,
    val showProgress: Boolean = false,
    val showFilterByAuthorizedAccounts: Boolean = true,
    val googleIdTokenCredential: GoogleIdTokenCredential? = null,
    val isAlreadyRegistered: Boolean = false,
)

enum class TextFieldType {
    PASSWORD, EMAIL
}

enum class NavigationTarget {
    LOGIN, REGISTER
}

sealed interface Action {
    data class TextFieldUpdate(val newValue: String, val type: TextFieldType) : Action
    data object TogglePasswordVisibility : Action
    data class NavigateToRegister(val type: NavigationTarget) : Action
    data class GoogleSignIn(val context: Context) : Action

    data class SetIsAlreadyRegistered(val isAlreadyRegistered: Boolean) : Action
}