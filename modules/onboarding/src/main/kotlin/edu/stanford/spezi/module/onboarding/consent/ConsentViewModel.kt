package edu.stanford.spezi.module.onboarding.consent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConsentViewModel @Inject internal constructor(
    private val repository: ConsentRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ConsentUiState())
    val uiState: StateFlow<ConsentUiState> = _uiState
    private lateinit var consentData: ConsentData

    init {
        viewModelScope.launch {
            consentData = repository.getConsentData()
            _uiState.update {
                it.copy(markdownText = consentData.markdownText)
            }
        }
    }


    fun onAction(action: ConsentAction) {
        _uiState.value = when (action) {
            is ConsentAction.TextFieldUpdate -> {
                when (action.type) {
                    TextFieldType.FIRST_NAME -> {
                        _uiState.value.copy(firstName = FieldState(value = action.newValue))
                    }

                    TextFieldType.LAST_NAME -> {
                        _uiState.value.copy(lastName = FieldState(value = action.newValue))
                    }
                }
            }

            is ConsentAction.AddPath -> {
                _uiState.value.copy(paths = _uiState.value.paths + action.path)
            }

            is ConsentAction.Undo -> {
                _uiState.value.copy(paths = _uiState.value.paths.dropLast(1))
            }

            is ConsentAction.Consent -> {
                viewModelScope.launch {
                    consentData.onAction(_uiState.value)
                }
                _uiState.value
            }
        }
    }
}
