package edu.stanford.spezi.modules.onboarding.consent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.spezi.modules.account.manager.UserSessionManager
import edu.stanford.spezi.ui.markdown.MarkdownParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class ConsentViewModel @Inject internal constructor(
    private val consentManager: ConsentManager,
    private val markdownParser: MarkdownParser,
    private val pdfCreationService: PdfCreationService,
    private val userSessionManager: UserSessionManager,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ConsentUiState())
    val uiState: StateFlow<ConsentUiState> = _uiState

    init {
        viewModelScope.launch {
            val markdownText = consentManager.getMarkdownText()
            _uiState.update {
                it.copy(markdownElements = markdownParser.parse(markdownText))
            }
        }
    }

    fun onAction(action: ConsentAction) {
        when (action) {
            is ConsentAction.TextFieldUpdate -> {
                when (action.type) {
                    TextFieldType.FIRST_NAME -> {
                        val firstName = FieldState(value = action.newValue)
                        _uiState.update { it.copy(firstName = firstName) }
                    }

                    TextFieldType.LAST_NAME -> {
                        val lastName = FieldState(value = action.newValue)
                        _uiState.update { it.copy(lastName = lastName) }
                    }
                }
            }

            is ConsentAction.AddPath -> {
                _uiState.update { it.copy(paths = it.paths + action.paths.toList()) }
            }

            is ConsentAction.UndoPath -> {
                _uiState.update { it.copy(paths = it.paths.dropLast(1)) }
            }

            is ConsentAction.Consent -> {
                onConsentAction()
            }
            is ConsentAction.ClearPath -> {
                _uiState.update { it.copy(paths = emptyList()) }
            }
        }
    }

    private fun onConsentAction() {
        viewModelScope.launch {
            val pdfBytes = pdfCreationService.createPdf(uiState = uiState.value)
            userSessionManager.uploadConsentPdf(pdfBytes = pdfBytes)
                .onSuccess { consentManager.onConsented() }
                .onFailure { consentManager.onConsentFailure(error = it) }
        }
    }
}
