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
internal class ConsentViewModel @Inject internal constructor(
    private val pdfService: ConsentPdfService,
    private val consentDataSource: ConsentDataSource,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ConsentUiState())
    val uiState: StateFlow<ConsentUiState> = _uiState

    fun onAction(action: ConsentAction) {
        when (action) {
            is ConsentAction.TextFieldUpdate -> {
                when (action.type) {
                    TextFieldType.FIRST_NAME -> {
                        _uiState.update { it.copy(name = it.name.copy(givenName = action.newValue)) }
                    }

                    TextFieldType.LAST_NAME -> {
                        _uiState.update { it.copy(name = it.name.copy(familyName = action.newValue)) }
                    }
                }
            }

            is ConsentAction.AddPath -> {
                _uiState.update { it.copy(paths = it.paths + action.path) }
            }

            is ConsentAction.Undo -> {
                _uiState.update { it.copy(paths = it.paths.dropLast(1)) }
            }

            is ConsentAction.Consent -> {
                viewModelScope.launch {
                    consentDataSource.store(
                        {
                            pdfService.createDocument(
                                action.exportConfiguration,
                                uiState.value.name,
                                uiState.value.paths,
                                uiState.value.markdownElements,
                            )
                        },
                        action.documentIdentifier,
                    )
                }
            }
        }
    }
}
