package edu.stanford.spezi.module.onboarding.consent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConsentViewModel @Inject internal constructor(
    private val pdfService: PdfService
) : ViewModel() {
    private val _uiState = MutableStateFlow(ConsentUiState())
    val uiState: StateFlow<ConsentUiState> = _uiState

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
                    pdfService.createPdf(
                        uiState.value
                    )
                }
                _uiState.value
            }
        }
    }
}
