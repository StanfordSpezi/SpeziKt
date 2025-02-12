package edu.stanford.bdh.heartbeat.app.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.bdh.heartbeat.app.account.AccountInfo
import edu.stanford.bdh.heartbeat.app.account.AccountManager
import edu.stanford.bdh.heartbeat.app.choir.ChoirRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MainUiState(
    val accountInfo: AccountInfo? = null,
    val isLoadingOnboarding: Boolean = false,
    val hasFinishedOnboarding: Boolean = false,
)

sealed interface MainAction {
    data object Reload : MainAction
    data object ResendVerificationEmail : MainAction
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val accountManager: AccountManager,
    private val choirRepository: ChoirRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            accountManager.observeAccountInfo().collect { accountInfo ->
                val previousAccountInfo = _uiState.value.accountInfo
                _uiState.update {
                    it.copy(accountInfo = accountInfo)
                }
                if (previousAccountInfo == null && accountInfo != null) {
                    handleReload()
                }
            }
        }
    }

    fun onAction(action: MainAction) {
        when (action) {
            is MainAction.Reload ->
                handleReload()
            is MainAction.ResendVerificationEmail ->
                handleResendVerificationEmail()
        }
    }

    private fun handleResendVerificationEmail() {
        viewModelScope.launch {
            accountManager.sendVerificationEmail()
        }
    }

    private fun handleReload() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingOnboarding = true) }
            runCatching {
                choirRepository.getOnboarding()
            }.onSuccess { onboarding ->
                _uiState.update {
                    it.copy(
                        isLoadingOnboarding = false,
                        hasFinishedOnboarding = onboarding.question.terminal == true,
                    )
                }
            }.onFailure {
                _uiState.update {
                    it.copy(
                        isLoadingOnboarding = false,
                    )
                }
            }
        }
    }
}
