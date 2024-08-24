package edu.stanford.bdh.engagehf.questionnaire

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.uhn.fhir.parser.IParser
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.core.navigation.NavigationEvent
import edu.stanford.spezi.core.navigation.Navigator
import edu.stanford.spezi.core.utils.MessageNotifier
import edu.stanford.spezi.core.utils.extensions.decode
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

    private val _uiState = MutableStateFlow<State>(
        State.Loading(
            savedStateHandle.decode(QUESTIONNAIRE_SAVE_STATE_PARAM)
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        loadQuestionnaire()
    }

    private fun loadQuestionnaire() {
        viewModelScope.launch {
            val questionnaireId = (_uiState.value as? State.Loading)?.questionnaireId
            questionnaireId?.let {
                questionnaireRepository.byId(it).onFailure {
                    notifier.notify("Failed to load questionnaire")
                }.onSuccess { questionnaire ->
                    val questionnaireString =
                        jsonParser.encodeResourceToString((questionnaire))
                    _uiState.update {
                        State.QuestionnaireLoaded(
                            args = bundleOf(
                                "questionnaire" to questionnaireString,
                                "show-cancel-button" to true
                            )
                        )
                    }
                }
            }
        }
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.SaveQuestionnaireResponse -> {
                val response = action.response
                response.setAuthored(Date())
                logger.i { "Save questionnaire response: $response" }
                viewModelScope.launch {
                    val questionnaire = _uiState.value as? State.QuestionnaireLoaded
                        ?: error("Invalid state")
                    _uiState.update {
                        State.Loading(null)
                    }
                    questionnaireRepository.save(response).onFailure {
                        notifier.notify("Failed to save questionnaire response")
                        _uiState.update {
                            State.QuestionnaireLoaded(questionnaire.args)
                        }
                    }.onSuccess {
                        navigator.navigateTo(NavigationEvent.PopBackStack)
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
        data class Loading(val questionnaireId: String?) : State
        data class QuestionnaireLoaded(val args: Bundle) : State
        data class Error(val message: String) : State
    }

    companion object {
        private const val QUESTIONNAIRE_SAVE_STATE_PARAM = "questionnaireId"
    }
}
