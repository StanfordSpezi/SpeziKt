package edu.stanford.spezikt.spezi_module.onboarding.invitation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.spezikt.coroutines.di.Dispatching
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvitationCodeViewModel @Inject internal constructor(
    private val fam: FirebaseAuthManager,
    @Dispatching.IO private val scope: CoroutineScope,
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
            }
        }
    }

    fun redeemInvitationCode() {
        scope.launch {
            val result = fam.checkInvitationCode(uiState.value.invitationCode)
            if (result.isSuccess) {
                // TODO navigate to login or register screen
            } else {
                _uiState.update {
                    it.copy(error = "Invitation Code is already used or incorrect")
                }
            }
        }
    }
}