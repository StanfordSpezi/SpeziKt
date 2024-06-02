package edu.stanford.spezi.module.onboarding.invitation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.spezi.core.navigation.Navigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvitationCodeViewModel @Inject internal constructor(
    private val invitationAuthManager: InvitationAuthManager,
    private val navigator: Navigator,
    private val invitationCodeRepository: InvitationCodeRepository,
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(InvitationCodeUiState(invitationCode = "", error = null))
    val uiState = _uiState.asStateFlow()

    private val screenInfo = invitationCodeRepository.getScreenInfo()

    init {
        _uiState.update {
            it.copy(
                title = screenInfo.title,
                description = screenInfo.description
            )
        }
    }


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
                    navigator.navigateTo(screenInfo.alreadyHaveAnAccountNavigationEvent)
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
        viewModelScope.launch {
            val result = invitationAuthManager.checkInvitationCode(uiState.value.invitationCode)
            if (result.isSuccess) {
                navigator.navigateTo(screenInfo.redeemButtonNavigationEvent)
            } else {
                _uiState.update {
                    it.copy(error = "Invitation Code is already used or incorrect")
                }
            }
        }
    }
}