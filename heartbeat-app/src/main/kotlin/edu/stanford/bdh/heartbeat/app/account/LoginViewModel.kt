package edu.stanford.bdh.heartbeat.app.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val showsForgotPasswordDialog: Boolean = false,
    val showsRegistrationDialog: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface LoginAction {
    data class UsernameChanged(val value: String) : LoginAction
    data class PasswordChanged(val value: String) : LoginAction
    data class ShowRegistrationDialog(val value: Boolean) : LoginAction
    data class ShowForgotPasswordDialog(val value: Boolean) : LoginAction
    data object CloseErrorDialog : LoginAction
    data object Submit : LoginAction
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val accountManager: AccountManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.UsernameChanged ->
                _uiState.update { it.copy(username = action.value) }
            is LoginAction.PasswordChanged ->
                _uiState.update { it.copy(password = action.value) }
            is LoginAction.ShowRegistrationDialog ->
                _uiState.update { it.copy(showsRegistrationDialog = action.value) }
            is LoginAction.ShowForgotPasswordDialog ->
                _uiState.update { it.copy(showsForgotPasswordDialog = action.value) }
            is LoginAction.CloseErrorDialog ->
                _uiState.update { it.copy(errorMessage = null) }
            is LoginAction.Submit -> {
                viewModelScope.launch {
                    _uiState.update { it.copy(isLoading = true) }
                    accountManager.signIn(uiState.value.username, uiState.value.password)
                        .onSuccess {
                            _uiState.update {
                                it.copy(
                                    username = "",
                                    password = "",
                                    isLoading = false,
                                )
                            }
                        }
                        .onFailure { error ->
                            _uiState.update {
                                it.copy(
                                    errorMessage = error.message ?: "An unknown error occurred.",
                                    isLoading = false,
                                )
                            }
                        }
                }
            }
        }
    }
}
