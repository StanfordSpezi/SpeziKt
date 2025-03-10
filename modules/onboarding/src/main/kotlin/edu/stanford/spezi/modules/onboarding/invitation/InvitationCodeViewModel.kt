package edu.stanford.spezi.modules.onboarding.invitation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.spezi.modules.account.manager.InvitationAuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvitationCodeViewModel @Inject internal constructor(
    private val invitationAuthManager: InvitationAuthManager,
    invitationCodeRepository: InvitationCodeRepository,
) : ViewModel() {
    private val screenData = invitationCodeRepository.getScreenData()
    private val _uiState =
        MutableStateFlow(
            InvitationCodeUiState(
                title = screenData.title,
                description = screenData.description,
                error = null,
            )
        )
    val uiState = _uiState.asStateFlow()

    fun onAction(action: Action) {
        when (action) {
            is Action.UpdateInvitationCode -> {
                _uiState.update {
                    it.copy(invitationCode = action.invitationCode)
                }
            }

            is Action.ClearError -> {
                _uiState.update {
                    it.copy(error = null)
                }
            }

            Action.RedeemInvitationCode -> {
                redeemInvitationCode()
            }
        }
    }

    private fun redeemInvitationCode() {
        viewModelScope.launch {
            val result = invitationAuthManager.checkInvitationCode(uiState.value.invitationCode)
            if (result.isSuccess) {
                screenData.redeemAction()
            } else {
                _uiState.update {
                    it.copy(error = "Invitation Code is already used or incorrect")
                }
            }
        }
    }
}
