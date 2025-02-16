package edu.stanford.bdh.heartbeat.app.survey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.bdh.heartbeat.app.choir.ChoirRepository
import edu.stanford.bdh.heartbeat.app.choir.api.types.AssessmentStep
import edu.stanford.bdh.heartbeat.app.choir.api.types.AssessmentSubmit
import edu.stanford.bdh.heartbeat.app.choir.api.types.QuestionType
import edu.stanford.bdh.heartbeat.app.choir.api.types.SubmitStatus
import edu.stanford.bdh.heartbeat.app.main.MainUiState
import edu.stanford.bdh.heartbeat.app.survey.ui.SurveyQuestionState
import edu.stanford.spezi.core.utils.MessageNotifier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface SurveyAction {
    data class ChangeAnswer(val id: String, val answer: AnswerFormat) : SurveyAction
    data object Continue : SurveyAction
    data object Back : SurveyAction
}

@HiltViewModel(assistedFactory = SurveyViewModel.Factory::class)
class SurveyViewModel @AssistedInject constructor(
    @Assisted private val state: MainUiState.Authenticated.Survey.Content,
    private val repository: ChoirRepository,
    private val surveyUiStateMapper: SurveyUiStateMapper,
    private val messageNotifier: MessageNotifier,
) : ViewModel() {
    private val onboarding get() = state.onboarding

    private var currentAssessmentStep = AssessmentStep(
        question = onboarding.question,
        displayStatus = onboarding.displayStatus
    )

    private val _state = MutableStateFlow(
        surveyUiStateMapper.map(
            assessmentStep = currentAssessmentStep,
            onAction = ::onAction
        )
    )

    val uiState = _state.asStateFlow()

    private fun onAction(action: SurveyAction) {
        when (action) {
            is SurveyAction.ChangeAnswer ->
                handleChangeAnswer(action)

            is SurveyAction.Continue -> {
                if (currentAssessmentStep.question.terminal == true) {
                    state.onCompleted()
                } else {
                    handleContinue(backRequest = false)
                }
            }

            is SurveyAction.Back ->
                handleContinue(backRequest = true)
        }
    }

    private fun handleChangeAnswer(action: SurveyAction.ChangeAnswer) {

    }

    private fun handleContinue(backRequest: Boolean) {
        viewModelScope.launch {
            val currentQuestionsState = _state.value.questionState
            _state.update { it.copy(questionState = SurveyQuestionState.Loading) }
            val displayStatus = currentAssessmentStep.displayStatus
            repository.continueAssessment(
                token = displayStatus.surveyToken ?: "",
                submit = AssessmentSubmit(
                    submitStatus = SubmitStatus(
                        questionId = displayStatus.questionId,
                        questionType = displayStatus.questionType,
                        stepNumber = (displayStatus.stepNumber ?: "0").toDoubleOrNull()
                            ?: 0.0,
                        surveySectionId = displayStatus.surveySectionId,
                        sessionToken = displayStatus.sessionToken,
                        locale = displayStatus.locale,
                        backRequest = backRequest
                    ),
                    answers = AssessmentSubmit.AnswersPayload(
                        value1 = null
                    )
                )
            ).onSuccess { success ->
                currentAssessmentStep = success
                _state.update {
                    surveyUiStateMapper.map(
                        assessmentStep = success,
                        onAction = ::onAction
                    )
                }
            }.onFailure { _ ->
                messageNotifier.notify("An error occurred when submitting your answer")
                _state.update { it.copy(questionState = currentQuestionsState) }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            content: MainUiState.Authenticated.Survey.Content,
        ): SurveyViewModel
    }
}
