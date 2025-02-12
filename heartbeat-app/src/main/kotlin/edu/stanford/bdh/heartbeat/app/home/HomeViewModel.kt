package edu.stanford.bdh.heartbeat.app.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.bdh.heartbeat.app.account.AccountManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val url: String,
    val isLoadingSignOut: Boolean = false,
    val showsSignOutAlert: Boolean = false,
)

sealed interface HomeAction {
    data class ShowSignOutAlert(val value: Boolean) : HomeAction
    data object SignOut : HomeAction
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val accountManager: AccountManager,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState(url = "https://heartbeatstudy.stanford.edu"))
    val uiState = _uiState.asStateFlow()

    fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.SignOut -> {
                viewModelScope.launch {
                    _uiState.update { it.copy(isLoadingSignOut = true) }
                    accountManager.signOut()
                    _uiState.update { it.copy(isLoadingSignOut = false) }
                }
            }
            is HomeAction.ShowSignOutAlert ->
                _uiState.update { it.copy(showsSignOutAlert = action.value) }
        }
    }
}
