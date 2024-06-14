package edu.stanford.spezi.module.account.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.core.utils.MessageNotifier
import edu.stanford.spezi.module.account.AccountEvents
import edu.stanford.spezi.module.account.AccountNavigationEvent
import edu.stanford.spezi.module.account.cred.manager.CredentialLoginManagerAuth
import edu.stanford.spezi.module.account.register.FieldState
import edu.stanford.spezi.module.account.register.FormValidator
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

                is Action.GoogleSignInOrSignUp -> {
                    if (uiState.value.isAlreadyRegistered) {
                        googleSignIn()
                    } else {
                        googleSignUp()
                    }
                    it
                }

                is Action.SetIsAlreadyRegistered -> {
                    handleIsAlreadyRegistered(action, it)
                }

                is Action.ForgotPassword -> {
                    forgotPassword()
                    it
                }

                Action.PasswordSignInOrSignUp -> {
                    handleLoginOrRegister()
                    it
                }
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

        _uiState.update {
            it.copy(
                hasAttemptedSubmit = true,
                email = FieldState(
                    uiState.email.value,
                    error = validator.emailResult(uiState.email.value).errorMessageOrNull()
                ),
                password = FieldState(
                    uiState.password.value,
                    error = validator.passwordResult(uiState.password.value).errorMessageOrNull()
                )
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
            TextFieldType.PASSWORD -> validator.passwordResult(action.newValue)
            TextFieldType.EMAIL -> validator.emailResult(action.newValue)
        }
        val error =
            if (uiState.hasAttemptedSubmit && result is FormValidator.Result.Invalid) result.errorMessageOrNull() else null
        return when (action.type) {
            TextFieldType.PASSWORD -> uiState.copy(
                password = newValue.copy(error = error),
                isFormValid = validator.isFormValid(uiState)
            )

            TextFieldType.EMAIL -> uiState.copy(
                email = newValue.copy(error = error),
                isFormValid = validator.isFormValid(uiState)
            )
        }
    }

    private fun forgotPassword() {
        if (validator.isEmailValid(uiState.value.email.value)) {
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
