package edu.stanford.spezi.module.account.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.spezi.core.utils.MessageNotifier
import edu.stanford.spezi.module.account.AccountEvents
import edu.stanford.spezi.module.account.manager.AuthenticationManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject internal constructor(
    private val authenticationManager: AuthenticationManager,
    private val messageNotifier: MessageNotifier,
    private val accountEvents: AccountEvents,
    private val authValidator: AuthValidator,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    fun onAction(action: Action) {
        _uiState.update {
            when (action) {
                is Action.TextFieldUpdate -> {
                    val newValue = FieldState(action.newValue)
                    val updatedUiState = when (action.type) {
                        TextFieldType.EMAIL -> it.copy(email = newValue)
                        TextFieldType.PASSWORD -> it.copy(password = newValue)
                    }
                    updatedUiState.copy(
                        isFormValid = authValidator.isFormValid(
                            password = updatedUiState.password.value,
                            email = updatedUiState.email.value
                        ),
                        isRegisterButtonEnabled = isRegisterButtonEnabled(updatedUiState)
                    )
                }

                is Action.OnRegisterPressed -> {
                    onRegisteredPressed()
                }

                is Action.TogglePasswordVisibility -> {
                    it.copy(isPasswordVisible = !it.isPasswordVisible)
                }
            }
        }
    }

    private fun onRegisteredPressed(): RegisterUiState {
        val uiState = _uiState.value
        return if (authValidator.isFormValid(
                password = uiState.password.value,
                email = uiState.email.value,
            )
        ) {
            viewModelScope.launch {
                authenticationManager.signUpWithEmailAndPassword(
                    email = uiState.email.value,
                    password = uiState.password.value,
                )
                    .onSuccess {
                        accountEvents.emit(AccountEvents.Event.SignUpSuccess)
                    }.onFailure {
                        accountEvents.emit(AccountEvents.Event.SignUpFailure)
                        messageNotifier.notify("Failed to sign up")
                    }
            }
            uiState
        } else {
            uiState.copy(
                email = uiState.email.copy(
                    error = authValidator.isValidEmail(uiState.email.value).errorMessageOrNull()
                ),
                password = uiState.password.copy(
                    error = authValidator.isValidPassword(uiState.password.value)
                        .errorMessageOrNull()
                ),
                isFormValid = authValidator.isFormValid(
                    password = uiState.password.value,
                    email = uiState.email.value,
                ),
            )
        }
    }

    private fun isRegisterButtonEnabled(uiState: RegisterUiState): Boolean {
        return uiState.email.value.isNotEmpty() && uiState.password.value.isNotEmpty()
    }
}
