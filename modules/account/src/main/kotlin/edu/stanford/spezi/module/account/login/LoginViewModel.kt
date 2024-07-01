package edu.stanford.spezi.module.account.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.core.utils.MessageNotifier
import edu.stanford.spezi.module.account.AccountEvents
import edu.stanford.spezi.module.account.AccountNavigationEvent
import edu.stanford.spezi.module.account.manager.CredentialLoginManagerAuth
import edu.stanford.spezi.module.account.register.FieldState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class LoginViewModel @Inject constructor(
    private val navigator: Navigator,
    private val credentialLoginManagerAuth: CredentialLoginManagerAuth,
    private val accountEvents: AccountEvents,
    private val messageNotifier: MessageNotifier,
    private val validator: LoginFormValidator,
) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    private var hasAttemptedSubmit: Boolean = false

    fun onAction(action: Action) {
        when (action) {
            is Action.TextFieldUpdate -> {
                _uiState.update {
                    updateTextField(action, it)
                }
            }

            is Action.TogglePasswordVisibility -> {
                _uiState.update {
                    it.copy(passwordVisibility = !it.passwordVisibility)
                }
            }

            is Action.NavigateToRegister -> {
                navigateToRegister()
            }

            is Action.GoogleSignInOrSignUp -> {
                if (uiState.value.isAlreadyRegistered) {
                    googleSignIn()
                } else {
                    googleSignUp()
                }
            }

            is Action.SetIsAlreadyRegistered -> {
                _uiState.update {
                    handleIsAlreadyRegistered(action, it)
                }
            }

            is Action.ForgotPassword -> {
                forgotPassword()
            }

            Action.PasswordSignInOrSignUp -> {
                handleLoginOrRegister()
            }
        }
    }

    private fun handleLoginOrRegister() {
        val uiState = _uiState.value
        if (uiState.isAlreadyRegistered && validator.isFormValid(uiState)) {
            passwordSignIn()
        }

        if (!uiState.isAlreadyRegistered && validator.isFormValid(uiState)) {
            navigateToRegister()
        }

        hasAttemptedSubmit = true
        _uiState.update {
            it.copy(
                email = it.email.copy(
                    error = validator.isValidEmail(email = uiState.email.value).errorMessageOrNull()
                ),
                password = it.password.copy(
                    error = validator.isValidPassword(password = uiState.password.value)
                        .errorMessageOrNull()
                ),
            )
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
                email = uiState.value.email.value,
                password = uiState.value.password.value,
            )
        )
    }

    private fun updateTextField(
        action: Action.TextFieldUpdate,
        uiState: UiState,
    ): UiState {
        val newValue = FieldState(action.newValue)
        val result = when (action.type) {
            TextFieldType.PASSWORD -> validator.isValidPassword(action.newValue)
            TextFieldType.EMAIL -> validator.isValidEmail(action.newValue)
        }
        val error =
            if (hasAttemptedSubmit && result.isValid.not()) result.errorMessageOrNull() else null
        return when (action.type) {
            TextFieldType.PASSWORD -> uiState.copy(
                password = newValue.copy(error = error),
                isFormValid = validator.isFormValid(uiState),
                isPasswordSignInEnabled = uiState.email.value.isNotEmpty() && newValue.value.isNotEmpty()
            )

            TextFieldType.EMAIL -> uiState.copy(
                email = newValue.copy(error = error),
                isFormValid = validator.isFormValid(uiState),
                isPasswordSignInEnabled = newValue.value.isNotEmpty() && uiState.password.value.isNotEmpty()
            )
        }
    }

    private fun forgotPassword() {
        if (validator.isValidEmail(uiState.value.email.value).isValid) {
            sendForgotPasswordEmail(uiState.value.email.value)
        } else {
            messageNotifier.notify("Please enter a valid email")
        }
    }

    private fun googleSignUp() {
        navigator.navigateTo(
            AccountNavigationEvent.RegisterScreen(
                isGoogleSignUp = true,
                email = uiState.value.email.value,
                password = uiState.value.password.value
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
                _uiState.value.email.value,
                _uiState.value.password.value,
            )
            if (result.isSuccess) {
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
