package edu.stanford.bdh.heartbeat.app.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.bdh.heartbeat.app.account.AccountInfo
import edu.stanford.bdh.heartbeat.app.account.AccountManager
import edu.stanford.bdh.heartbeat.app.choir.api.types.AssessmentStep
import edu.stanford.bdh.heartbeat.app.fake.FakeConfigs
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.core.utils.MessageNotifier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
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

        sealed interface Survey : Authenticated {
            data object LoadingFailed : Survey
            data class Content(
                val assessmentStep: AssessmentStep,
                val onCompleted: () -> Unit,
            ) : Survey
        }
    }

    data object HomePage : MainUiState
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
    private val startSurveyUseCase: StartSurveyUseCase,
    private val messageNotifier: MessageNotifier,
) : ViewModel() {
    private val logger by speziLogger()
    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            accountManager.observeAccountInfo().distinctUntilChanged().collect { accountInfo ->
                logger.i { "Received new account info update $accountInfo" }
                update(accountInfo = accountInfo)
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
                        .onSuccess { update(accountInfo = it) }
                        .onFailure {
                            messageNotifier.notify("An error occurred while reloading your status")
                            _uiState.update { previousState }
                        }
                }
            }

            is MainAction.ReloadOnboarding -> {
                viewModelScope.launch {
                    _uiState.update { MainUiState.Loading }
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

    private fun update(accountInfo: AccountInfo?) {
        when {
            FakeConfigs.ONBOARDING_COMPLETED && FakeConfigs.ENABLED -> {
                viewModelScope.launch {
                    if (accountInfo == null) accountManager.reloadAccountInfo()
                    _uiState.update { MainUiState.HomePage }
                }
            }
            accountInfo == null -> _uiState.update { MainUiState.Unauthenticated }
            !accountInfo.isEmailVerified -> _uiState.update {
                MainUiState.Authenticated.RequiresEmailVerification(
                    showSignoutDialog = false
                )
            }

            else -> loadOnboarding()
        }
    }

    private fun loadOnboarding() {
        viewModelScope.launch {
            logger.i { "Invoking getOnboarding" }
            _uiState.update { MainUiState.Loading }
            startSurveyUseCase()
                .onSuccess { assessmentStep ->
                    logger.i { "Assessment step loaded successfully" }
                    _uiState.update {
                        MainUiState.Authenticated.Survey.Content(
                            assessmentStep = assessmentStep,
                            onCompleted = {
                                messageNotifier.notify("We appreciate your participation in the study!")
                                _uiState.update { MainUiState.HomePage }
                            }
                        )
                    }
                }.onFailure {
                    logger.e(it) { "Failed to load onboarding" }
                    _uiState.update { MainUiState.Authenticated.Survey.LoadingFailed }
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
