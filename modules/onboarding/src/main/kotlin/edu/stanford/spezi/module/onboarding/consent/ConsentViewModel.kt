package edu.stanford.spezi.module.onboarding.consent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.spezi.core.design.component.markdown.MarkdownParser
import edu.stanford.spezi.core.utils.MessageNotifier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConsentViewModel @Inject internal constructor(
    private val consentManager: ConsentManager,
    private val markdownParser: MarkdownParser,
    private val messageNotifier: MessageNotifier,
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
        _uiState.update { currentState ->
            when (action) {
                is ConsentAction.TextFieldUpdate -> {
                    when (action.type) {
                        TextFieldType.FIRST_NAME -> {
                            currentState.copy(firstName = FieldState(value = action.newValue))
                        }

                        TextFieldType.LAST_NAME -> {
                            currentState.copy(lastName = FieldState(value = action.newValue))
                        }
                    }
                }

                is ConsentAction.AddPath -> {
                    currentState.copy(paths = currentState.paths + action.path)
                }

                is ConsentAction.Undo -> {
                    currentState.copy(paths = currentState.paths.dropLast(1))
                }

                is ConsentAction.Consent -> {
                    viewModelScope.launch {
                        consentManager.onConsented(currentState).onFailure {
                            messageNotifier.notify("Something went wrong, failed to submit the consent!")
                        }
                    }
                    currentState
                }
            }
        }
    }
}
