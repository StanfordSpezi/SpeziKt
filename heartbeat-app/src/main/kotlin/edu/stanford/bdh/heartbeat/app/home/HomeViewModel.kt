package edu.stanford.bdh.heartbeat.app.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.bdh.heartbeat.app.account.AccountManager
import edu.stanford.bdh.heartbeat.app.choir.ChoirRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val url: String,
    val isLoadingDelete: Boolean = false,
    val showsDeleteDialog: Boolean = false,
    val isLoadingSignOut: Boolean = false,
    val showsSignOutDialog: Boolean = false,
)

sealed interface HomeAction {
    data class ShowDeleteDialog(val value: Boolean) : HomeAction
    data object Delete : HomeAction
    data class ShowSignOutDialog(val value: Boolean) : HomeAction
    data object SignOut : HomeAction
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val accountManager: AccountManager,
    private val choirRepository: ChoirRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState(url = "https://heartbeatstudy.stanford.edu"))
    val uiState = _uiState.asStateFlow()

    fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.ShowDeleteDialog ->
                _uiState.update { it.copy(showsDeleteDialog = action.value) }
            is HomeAction.Delete -> {
                viewModelScope.launch {
                    _uiState.update { it.copy(isLoadingDelete = true) }
                    runCatching {
                        choirRepository.unenrollParticipant()
                        accountManager.signOut()
                    }
                    _uiState.update { it.copy(isLoadingDelete = false, showsDeleteDialog = false) }
                }
            }
            is HomeAction.ShowSignOutDialog ->
                _uiState.update { it.copy(showsSignOutDialog = action.value) }
            is HomeAction.SignOut -> {
                viewModelScope.launch {
                    _uiState.update { it.copy(isLoadingSignOut = true) }
                    accountManager.signOut()
                    _uiState.update { it.copy(isLoadingSignOut = false, showsSignOutDialog = false) }
                }
            }
        }
    }
}
