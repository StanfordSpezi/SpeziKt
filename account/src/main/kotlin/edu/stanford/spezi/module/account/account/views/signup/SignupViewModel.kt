package edu.stanford.spezi.module.account.account.views.signup

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.spezi.core.design.views.validation.state.ValidationContext
import edu.stanford.spezi.module.account.account.Account
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
internal class SignupViewModel @Inject constructor() : ViewModel() {

    @Inject internal lateinit var account: Account

    data class UiState(
        val validation: ValidationContext = ValidationContext(),
    )

    sealed interface Action {
        data class UpdateValidation(val context: ValidationContext) : Action
    }

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    fun onAction(action: Action) {
        when (action) {
            is Action.UpdateValidation ->
                _uiState.update { it.copy(validation = action.context) }
        }
    }
}
