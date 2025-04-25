package edu.stanford.bdh.heartbeat.app.home

import android.webkit.WebView
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.bdh.heartbeat.app.R
import edu.stanford.bdh.heartbeat.app.account.AccountManager
import edu.stanford.bdh.heartbeat.app.choir.ChoirRepository
import edu.stanford.spezi.modules.design.component.AsyncTextButton
import edu.stanford.spezi.modules.design.component.ScreenViewModel
import edu.stanford.spezi.modules.utils.MessageNotifier
import edu.stanford.spezi.ui.Colors
import edu.stanford.spezi.ui.StringResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

sealed interface HomeAction {
    data class WebViewCreated(val webView: WebView) : HomeAction
    data object AccountClicked : HomeAction
    data object CloseAccountClicked : HomeAction
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val accountManager: AccountManager,
    private val choirRepository: ChoirRepository,
    private val messageNotifier: MessageNotifier,
) : ScreenViewModel() {
    private val accountUiState = buildAccountUiState()
    private val _screenState = MutableStateFlow(
        HomeUiState(
            title = StringResource(R.string.app_name),
            accountUiState = null,
            showAccountButton = accountUiState != null,
            onAction = ::onAction,
        )
    )

    override val screenState = _screenState.asStateFlow()

    private fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.AccountClicked -> {
                _screenState.update { it.copy(accountUiState = accountUiState) }
            }

            is HomeAction.WebViewCreated -> {
                action.webView.loadUrl("https://heartbeatstudy.stanford.edu")
            }

            is HomeAction.CloseAccountClicked -> dismissAccount()
        }
    }

    private fun buildAccountUiState(): AccountUiState? {
        val accountInfo = accountManager.getAccountInfo() ?: return null
        return AccountUiState(
            email = accountInfo.email,
            name = accountInfo.name,
            actions = listOf(
                AccountActionItem(
                    title = "Delete your account",
                    color = { Colors.onBackground },
                    confirmation = "Do you really want to delete your account? This action cannot be reversed.",
                    confirmButton = confirmButton(action = ::deleteAccount),
                ),
                AccountActionItem(
                    title = "Sign out",
                    color = { Colors.error },
                    confirmation = "Do you really want to sign out?",
                    confirmButton = confirmButton(action = ::signOut),
                )
            ),
            onDismiss = ::dismissAccount
        )
    }

    private fun dismissAccount() {
        _screenState.update { it.copy(accountUiState = null) }
    }

    private fun confirmButton(action: suspend () -> Unit) = AsyncTextButton(
        coroutineScope = { viewModelScope },
        title = "Confirm",
        action = action,
    )

    private suspend fun deleteAccount() {
        runCatching {
            choirRepository.unenrollParticipant()
            accountManager.deleteCurrentUser()
        }.onFailure { _ ->
            messageNotifier.notify("An error occurred while deleting your account")
        }.onSuccess { dismissAccount() }
    }

    private suspend fun signOut() {
        accountManager.signOut()
            .onFailure {
                messageNotifier.notify("An error occurred while signing you out!")
            }.onSuccess { dismissAccount() }
    }
}
