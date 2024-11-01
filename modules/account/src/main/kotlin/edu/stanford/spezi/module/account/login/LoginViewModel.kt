package edu.stanford.spezi.module.account.login

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.core.utils.MessageNotifier
import edu.stanford.spezi.module.account.AccountEvents
import edu.stanford.spezi.module.account.AccountNavigationEvent
import edu.stanford.spezi.module.account.R
import edu.stanford.spezi.module.account.manager.AuthenticationManager
import edu.stanford.spezi.module.account.register.AuthValidator
import edu.stanford.spezi.module.account.register.FieldState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class LoginViewModel @Inject constructor(
    private val navigator: Navigator,
    private val authenticationManager: AuthenticationManager,
    private val accountEvents: AccountEvents,
    private val messageNotifier: MessageNotifier,
    private val authValidator: AuthValidator,
    @ApplicationContext private val context: Context,
) : ViewModel() {
    private var hasAttemptedSubmit: Boolean = false
    private val email: String
        get() = _uiState.value.email.value
    private val password: String
        get() = _uiState.value.password.value

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    fun onAction(action: Action) {
        when (action) {
            is Action.TextFieldUpdate -> {
                _uiState.update {
                    updateTextField(action)
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

            is Action.Async.GoogleSignInOrSignUp -> {
                execute(action = action, block = ::onGoogleSignInOrSignUp)
            }

            is Action.Async.ForgotPassword -> {
                execute(action = action, block = ::onForgotPassword)
            }

            is Action.Async.PasswordSignIn -> {
                execute(action = action, block = ::onPasswordSignIn)
            }

            is Action.EmailClicked -> {
                val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:${action.email}")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(emailIntent)
            }
        }
    }

    private suspend fun onPasswordSignIn() {
        val uiState = _uiState.value
        if (authValidator.isFormValid(
                password = uiState.password.value,
                email = uiState.email.value
            )
        ) {
            authenticationManager.signIn(
                email = email,
                password = password,
            ).onSuccess {
                accountEvents.emit(event = AccountEvents.Event.SignInSuccess)
            }.onFailure {
                accountEvents.emit(event = AccountEvents.Event.SignInFailure)
                messageNotifier.notify(R.string.error_sign_in_failed)
            }
        }

        hasAttemptedSubmit = true
        _uiState.update {
            it.copy(
                email = it.email.copy(
                    error = authValidator.isValidEmail(email = uiState.email.value)
                        .errorMessageOrNull()
                ),
                password = it.password.copy(
                    error = authValidator.isValidPassword(password = uiState.password.value)
                        .errorMessageOrNull()
                ),
            )
        }
    }

    private fun navigateToRegister() {
        navigator.navigateTo(
            AccountNavigationEvent.RegisterScreen(
                email = email,
                password = password,
            )
        )
    }

    private fun updateTextField(
        action: Action.TextFieldUpdate,
    ): UiState {
        val uiState = _uiState.value
        val newValue = FieldState(action.newValue)
        val result = when (action.type) {
            TextFieldType.PASSWORD -> authValidator.isValidPassword(action.newValue)
            TextFieldType.EMAIL -> authValidator.isValidEmail(action.newValue)
        }
        val error =
            if (hasAttemptedSubmit && result.isValid.not()) result.errorMessageOrNull() else null
        return when (action.type) {
            TextFieldType.PASSWORD -> uiState.copy(
                password = newValue.copy(error = error),
                isFormValid = authValidator.isFormValid(
                    password = newValue.value,
                    email = uiState.email.value
                ),
                isPasswordSignInEnabled = uiState.email.value.isNotEmpty() && newValue.value.isNotEmpty()
            )

            TextFieldType.EMAIL -> uiState.copy(
                email = newValue.copy(error = error),
                isFormValid = authValidator.isFormValid(
                    password = uiState.password.value,
                    email = newValue.value,
                ),
                isPasswordSignInEnabled = newValue.value.isNotEmpty() && uiState.password.value.isNotEmpty()
            )
        }
    }

    private suspend fun onForgotPassword() {
        if (authValidator.isValidEmail(email).isValid) {
            authenticationManager.sendForgotPasswordEmail(email)
                .onSuccess {
                    messageNotifier.notify(R.string.email_sent)
                }
                .onFailure {
                    messageNotifier.notify(R.string.failed_to_send_email)
                }
        } else {
            messageNotifier.notify(R.string.please_enter_a_valid_email)
        }
    }

    private suspend fun onGoogleSignInOrSignUp() {
        authenticationManager.signInWithGoogle()
            .onSuccess {
                accountEvents.emit(event = AccountEvents.Event.SignInSuccess)
            }.onFailure {
                accountEvents.emit(event = AccountEvents.Event.SignInFailure)
                messageNotifier.notify(R.string.error_sign_in_failed)
            }
    }

    private fun execute(
        action: Action.Async,
        block: suspend () -> Unit,
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(pendingActions = it.pendingActions + action) }
            block()
            _uiState.update { it.copy(pendingActions = it.pendingActions - action) }
        }
    }
}
