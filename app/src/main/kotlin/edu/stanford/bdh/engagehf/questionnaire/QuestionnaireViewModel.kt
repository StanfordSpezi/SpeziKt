package edu.stanford.bdh.engagehf.questionnaire

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.uhn.fhir.parser.IParser
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.spezi.core.navigation.NavigationEvent
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.core.utils.MessageNotifier
import edu.stanford.spezi.core.utils.extensions.decode
import edu.stanford.spezi.spezi.core.logging.speziLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.QuestionnaireResponse
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class QuestionnaireViewModel @Inject internal constructor(
    private val questionnaireRepository: QuestionnaireRepository,
    private val navigator: Navigator,
    savedStateHandle: SavedStateHandle,
    private val notifier: MessageNotifier,
    private val jsonParser: IParser,
) : ViewModel() {
    private val logger by speziLogger()

    private val _uiState = MutableStateFlow<State>(State.Loading)
    val uiState = _uiState.asStateFlow()

    private val questionnaireId: String = savedStateHandle.decode(QUESTIONNAIRE_SAVE_STATE_PARAM)

    init {
        loadQuestionnaire()
    }

    private fun loadQuestionnaire() {
        viewModelScope.launch {
            questionnaireRepository.getQuestionnaire(questionnaireId)
                .mapCatching { questionnaire ->
                    jsonParser.encodeResourceToString((questionnaire))
                }
                .onSuccess { questionnaireString ->
                    _uiState.update {
                        State.QuestionnaireLoaded(
                            questionnaireString = questionnaireString,
                            isSaving = false,
                        )
                    }
                }.onFailure {
                    _uiState.update { State.Error(message = "Failed to load questionnaire") }
                }
        }
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.SaveQuestionnaireResponse -> {
                val loadedState = _uiState.value as? State.QuestionnaireLoaded ?: return
                val response = action.response
                response.setAuthored(Date())
                logger.i { "Save questionnaire response: $response" }
                viewModelScope.launch {
                    _uiState.update { loadedState.copy(isSaving = true) }
                    val result = questionnaireRepository.save(response)
                    _uiState.update { loadedState.copy(isSaving = false) }
                    result.onSuccess {
                        navigator.navigateTo(NavigationEvent.PopBackStack)
                    }.onFailure {
                        notifier.notify("Failed to save questionnaire response")
                    }
                }
            }

            Action.Cancel -> {
                navigator.navigateTo(NavigationEvent.PopBackStack)
            }
        }
    }

    sealed interface Action {
        data class SaveQuestionnaireResponse(val response: QuestionnaireResponse) : Action
        data object Cancel : Action
    }

    sealed interface State {
        data object Loading : State
        data class QuestionnaireLoaded(
            val questionnaireString: String,
            val isSaving: Boolean,
        ) : State {
            val showCancelButton = true
        }
        data class Error(val message: String) : State
    }

    companion object {
        private const val QUESTIONNAIRE_SAVE_STATE_PARAM = "questionnaireId"
    }
}
