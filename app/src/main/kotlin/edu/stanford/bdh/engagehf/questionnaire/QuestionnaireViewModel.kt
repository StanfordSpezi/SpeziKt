package edu.stanford.bdh.engagehf.questionnaire

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.uhn.fhir.context.FhirContext
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.spezi.core.logging.speziLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.QuestionnaireResponse
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class QuestionnaireViewModel @Inject internal constructor(
    private val questionnaireRepository: QuestionnaireRepository,
) : ViewModel() {
    private val logger by speziLogger()

    private val _uiState = MutableStateFlow<State>(State.Loading)
    val uiState = _uiState.asStateFlow()

    private val fhirContext = FhirContext.forR4()

    init {
        logger.i { "QuestionnaireViewModel created" }
        loadQuestionnaire()
    }

    private fun loadQuestionnaire() {
        viewModelScope.launch {
            val questionnaire =
                questionnaireRepository.observe("0").first()
                    .getOrNull() // TODO adjust to get it by Message Action
                    ?: error("Error loading questionnaire")
            val questionnaireString =
                fhirContext.newJsonParser().encodeResourceToString((questionnaire))
            _uiState.update {
                State.QuestionnaireLoaded(
                    args = bundleOf(
                        "questionnaire" to questionnaireString,
                        "show-cancel-button" to false
                    )
                )
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
                    questionnaireRepository.save(response)
                }
            }

            Action.Cancel -> {
                // TODO check if we want to allow this anyway
            }
        }
    }

    sealed interface Action {
        data class SaveQuestionnaireResponse(val response: QuestionnaireResponse) : Action
        data object Cancel : Action
    }

    sealed interface State {
        data object Loading : State
        data class QuestionnaireLoaded(val args: Bundle) : State
        data class Error(val message: String) : State
    }
}
