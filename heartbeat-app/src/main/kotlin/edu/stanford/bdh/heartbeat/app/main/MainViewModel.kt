package edu.stanford.bdh.heartbeat.app.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.bdh.heartbeat.app.account.AccountInfo
import edu.stanford.bdh.heartbeat.app.account.AccountManager
import edu.stanford.bdh.heartbeat.app.choir.ChoirRepository
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.core.utils.MessageNotifier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface MainUiState {
    data object Loading : MainUiState

    data object Unauthenticated : MainUiState

    sealed interface Authenticated : MainUiState {

        data class RequiresEmailVerification(
            val showSignoutDialog: Boolean,
        ) : Authenticated

        sealed interface Onboarding : Authenticated {
            data object Loading : Onboarding
            data object Pending : Onboarding
            data object LoadingFailed : Onboarding
            data object Completed : Onboarding
        }
    }
}

sealed interface MainAction {
    data object ReloadOnboarding : MainAction
    data object ReloadUser : MainAction
    data object ResendVerificationEmail : MainAction
    data class ShowSignOutDialog(val value: Boolean) : MainAction
    data object SignOut : MainAction
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val accountManager: AccountManager,
    private val choirRepository: ChoirRepository,
    private val messageNotifier: MessageNotifier,
) : ViewModel() {
    private val logger by speziLogger()
    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            accountManager.observeAccountInfo().collect { accountInfo ->
                logger.i { "Received new account info update $accountInfo" }
                updateState(accountInfo = accountInfo)
                if (accountInfo == null) choirRepository.clear()
            }
        }
    }

    fun onAction(action: MainAction) {
        when (action) {
            is MainAction.ReloadUser -> {
                viewModelScope.launch {
                    val previousState = _uiState.value
                    _uiState.update { MainUiState.Loading }
                    accountManager.reloadAccountInfo()
                        .onSuccess { updateState(accountInfo = it) }
                        .onFailure { _uiState.update { previousState } }
                }
            }

            is MainAction.ReloadOnboarding -> {
                _uiState.update { MainUiState.Authenticated.Onboarding.Loading }
                viewModelScope.launch {
                    accountManager.reloadAccountInfo()
                    loadOnboarding()
                }
            }

            is MainAction.ResendVerificationEmail -> viewModelScope.launch {
                accountManager.sendVerificationEmail()
                    .onSuccess {
                        val message = "Verification email sent!"
                        logger.i { message }
                        messageNotifier.notify(message)
                    }
                    .onFailure {
                        val message = "Failed to send verification email!"
                        logger.e(it) { message }
                        messageNotifier.notify("Failed to send verification email!")
                    }
            }

            is MainAction.ShowSignOutDialog ->
                _uiState.update { currentState ->
                    if (currentState is MainUiState.Authenticated.RequiresEmailVerification) {
                        currentState.copy(showSignoutDialog = action.value)
                    } else {
                        currentState
                    }
                }

            is MainAction.SignOut -> handleSignOut()
        }
    }

    private fun updateState(accountInfo: AccountInfo?) {
        _uiState.update {
            when {
                accountInfo == null -> MainUiState.Unauthenticated
                !accountInfo.isEmailVerified -> MainUiState.Authenticated.RequiresEmailVerification(
                    showSignoutDialog = false
                )

                else -> MainUiState.Authenticated.Onboarding.Loading.also { loadOnboarding() }
            }
        }
    }

    private fun loadOnboarding() {
        viewModelScope.launch {
            choirRepository.getOnboarding()
                .onSuccess { onboarding ->
                    logger.i { "Onboarding loaded successfully" }
                    _uiState.update {
                        if (onboarding.question.terminal == true) {
                            MainUiState.Authenticated.Onboarding.Completed
                        } else {
                            MainUiState.Authenticated.Onboarding.Pending
                        }
                    }
                }.onFailure {
                    logger.e(it) { "Failed to load onboarding" }
                    _uiState.update { MainUiState.Authenticated.Onboarding.LoadingFailed }
                }
        }
    }

    private fun handleSignOut() {
        viewModelScope.launch {
            accountManager.signOut()
                .onSuccess {
                    _uiState.update { MainUiState.Unauthenticated }
                }.onFailure {
                    messageNotifier.notify("Failed to sign out")
                }
        }
    }
}
