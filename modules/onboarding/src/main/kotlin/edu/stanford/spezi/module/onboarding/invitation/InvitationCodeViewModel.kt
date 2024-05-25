package edu.stanford.spezi.module.onboarding.invitation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.spezi.core.coroutines.di.Dispatching
import edu.stanford.spezi.core.navigation.NavigationEvent
import edu.stanford.spezi.core.navigation.Navigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvitationCodeViewModel @Inject internal constructor(
    private val iam: InvitationAuthManager,
    @Dispatching.IO private val scope: CoroutineScope,
    private val navigator: Navigator
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(InvitationCodeUiState(invitationCode = "", error = null))
    val uiState = _uiState.asStateFlow()

    fun onAction(action: Action) {
        _uiState.update {
            when (action) {
                is Action.UpdateInvitationCode -> {
                    val newValue = action.invitationCode
                    it.copy(invitationCode = newValue)
                }

                is Action.ClearError -> {
                    it.copy(error = null)
                }

                Action.AlreadyHasAccountPressed -> {
                    navigator.navigateTo(NavigationEvent.LoginScreen)
                    it
                }

                Action.RedeemInvitationCode -> {
                    redeemInvitationCode()
                    it
                }
            }
        }
    }

    private fun redeemInvitationCode() {
        scope.launch {
            val result = iam.checkInvitationCode(uiState.value.invitationCode)
            if (result.isSuccess) {
                navigator.navigateTo(NavigationEvent.RegisterScreen)
            } else {
                _uiState.update {
                    it.copy(error = "Invitation Code is already used or incorrect")
                }
            }
        }
    }
}