package edu.stanford.spezi.module.account.login

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.stanford.spezi.core.navigation.DefaultNavigationEvent
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.module.account.AccountNavigationEvent
import edu.stanford.spezi.module.account.cred.manager.CredentialLoginManagerAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val navigator: Navigator,
    private val credentialLoginManagerAuth: CredentialLoginManagerAuth,
    @ApplicationContext private val appContext: Context,
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    fun onAction(action: Action) {
        _uiState.update {
            when (action) {
                is Action.TextFieldUpdate -> {
                    updateTextField(action, it)
                }

                is Action.TogglePasswordVisibility -> {
                    it.copy(passwordVisibility = !it.passwordVisibility)
                }

                is Action.NavigateToRegister -> {
                    navigateToRegister()
                    it
                }

                is Action.GoogleSignIn -> {
                    googleSignIn()
                    it
                }

                is Action.SetIsAlreadyRegistered -> {
                    handleIsAlreadyRegistered(action, it)
                }

                is Action.PasswordCredentialSignIn -> {
                    passwordSignIn()
                    it
                }

                is Action.ForgotPassword -> {
                    forgotPassword()
                    it
                }

                Action.GoogleSignUp -> {
                    googleSignUp()
                    it
                }
            }
        }
    }

    private fun handleIsAlreadyRegistered(
        action: Action.SetIsAlreadyRegistered,
        it: UiState,
    ): UiState {
        viewModelScope.launch {
            if (action.isAlreadyRegistered) {
                // launch credential manger with authorized accounts
            } else {
                // launch credential manager without authorized accounts
            }
        }
        return it.copy(isAlreadyRegistered = action.isAlreadyRegistered)
    }

    private fun navigateToRegister() {
        navigator.navigateTo(
            AccountNavigationEvent.RegisterScreen(
                isGoogleSignUp = false,
                email = uiState.value.email,
                password = uiState.value.password,
            )
        )
    }

    private fun updateTextField(
        action: Action.TextFieldUpdate,
        it: UiState,
    ): UiState {
        val newValue = action.newValue
        return when (action.type) {
            TextFieldType.PASSWORD -> it.copy(password = newValue)
            TextFieldType.EMAIL -> it.copy(email = newValue)
        }
    }

    private fun forgotPassword() {
        if (uiState.value.email.isEmpty()) {
            Toast.makeText(appContext, "Please enter your email", Toast.LENGTH_SHORT)
                .show()
        } else {
            sendForgotPasswordEmail(uiState.value.email)
        }
    }

    private fun googleSignUp() {
        navigator.navigateTo(
            AccountNavigationEvent.RegisterScreen(
                isGoogleSignUp = true,
                email = uiState.value.email,
                password = uiState.value.password
            )
        )
    }

    private fun sendForgotPasswordEmail(email: String) {
        viewModelScope.launch {
            if (credentialLoginManagerAuth.sendForgotPasswordEmail(email).isSuccess) {
                Toast.makeText(appContext, "Email sent", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(appContext, "Failed to send email", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun passwordSignIn() {
        viewModelScope.launch {
            val result = credentialLoginManagerAuth.handlePasswordSignIn(
                _uiState.value.email,
                _uiState.value.password,
                appContext
            )
            if (result) {
                navigator.navigateTo(DefaultNavigationEvent.BluetoothScreen)
            } else {
                Toast.makeText(appContext, "Failed to sign in", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun googleSignIn() {
        credentialLoginManagerAuth.handleGoogleSignIn(appContext, viewModelScope) { success ->
            if (success) {
                navigator.navigateTo(DefaultNavigationEvent.BluetoothScreen)
            } else {
                Toast.makeText(appContext, "Failed to sign in", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
