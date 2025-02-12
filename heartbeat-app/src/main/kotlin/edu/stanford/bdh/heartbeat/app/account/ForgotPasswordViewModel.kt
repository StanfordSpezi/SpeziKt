package edu.stanford.bdh.heartbeat.app.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ForgotPasswordUiState(
    val username: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface ForgotPasswordAction {
    data class UsernameChanged(val value: String) : ForgotPasswordAction
    data object Submit : ForgotPasswordAction
}

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val accountManager: AccountManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState = _uiState.asStateFlow()

    fun onAction(action: ForgotPasswordAction) {
        when (action) {
            is ForgotPasswordAction.UsernameChanged ->
                _uiState.update { it.copy(username = action.value) }
            is ForgotPasswordAction.Submit -> {
                _uiState.update { it.copy(isLoading = true) }
                val state = _uiState.value
                viewModelScope.launch {
                    accountManager.sendForgotPasswordEmail(state.username)
                        .onSuccess {
                            _uiState.update {
                                it.copy(
                                    username = "",
                                    isLoading = false,
                                    isSuccess = true,
                                )
                            }
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
