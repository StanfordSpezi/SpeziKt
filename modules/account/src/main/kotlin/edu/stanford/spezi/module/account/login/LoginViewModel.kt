package edu.stanford.spezi.module.account.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.core.utils.MessageNotifier
import edu.stanford.spezi.module.account.AccountEvents
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
    private val accountEvents: AccountEvents,
    private val messageNotifier: MessageNotifier,
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
            messageNotifier.notify("Please enter your email")
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
                messageNotifier.notify("Email sent")
            } else {
                messageNotifier.notify("Failed to send email")
            }
        }
    }

    private fun passwordSignIn() {
        viewModelScope.launch {
            val result = credentialLoginManagerAuth.handlePasswordSignIn(
                _uiState.value.email,
                _uiState.value.password,
            )
            if (result) {
                accountEvents.emit(event = AccountEvents.Event.SignInSuccess)
            } else {
                accountEvents.emit(event = AccountEvents.Event.SignInFailure)
                messageNotifier.notify("Failed to sign in")
            }
        }
    }

    private fun googleSignIn() {
        viewModelScope.launch {
            credentialLoginManagerAuth.handleGoogleSignIn()
                .onSuccess { success ->
                    if (success) {
                        accountEvents.emit(event = AccountEvents.Event.SignInSuccess)
                    } else {
                        accountEvents.emit(event = AccountEvents.Event.SignInFailure)
                        messageNotifier.notify("Failed to sign in")
                    }
                }.onFailure {
                    accountEvents.emit(event = AccountEvents.Event.SignInFailure)
                    messageNotifier.notify("Failed to sign in")
                }
        }
    }
}
