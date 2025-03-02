package edu.stanford.bdh.heartbeat.app.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface RegisterAction {
    data class UsernameChanged(val value: String) : RegisterAction
    data class PasswordChanged(val value: String) : RegisterAction
    data object Submit : RegisterAction
}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val accountManager: AccountManager,
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    fun onAction(action: RegisterAction) {
        when (action) {
            is RegisterAction.UsernameChanged ->
                _uiState.update { it.copy(username = action.value) }
            is RegisterAction.PasswordChanged ->
                _uiState.update { it.copy(password = action.value) }
            is RegisterAction.Submit -> {
                viewModelScope.launch {
                    _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                    accountManager.signUpWithEmailAndPassword(uiState.value.username, uiState.value.password)
                        .onSuccess {
                            _uiState.update {
                                it.copy(
                                    username = "",
                                    password = "",
                                    isLoading = false,
                                )
                            }
                            accountManager.sendVerificationEmail().getOrNull()
                        }
                        .onFailure { error ->
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = error.message ?: "An unknown error occurred."
                                )
                            }
                        }
                }
            }
        }
    }
}
