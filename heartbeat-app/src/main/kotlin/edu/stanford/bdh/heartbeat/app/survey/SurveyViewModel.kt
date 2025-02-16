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
import edu.stanford.bdh.heartbeat.app.survey.ui.QuestionButton
import edu.stanford.bdh.heartbeat.app.survey.ui.SurveyProgress
import edu.stanford.bdh.heartbeat.app.survey.ui.SurveyQuestionState
import edu.stanford.bdh.heartbeat.app.survey.ui.SurveyQuestionTitle
import edu.stanford.bdh.heartbeat.app.survey.ui.SurveyUiState
import edu.stanford.bdh.heartbeat.app.survey.ui.fields.ChoicesFieldItemPreviewParameterProvider
import edu.stanford.bdh.heartbeat.app.survey.ui.fields.datePickerFormField
import edu.stanford.bdh.heartbeat.app.survey.ui.fields.textFieldArea
import edu.stanford.bdh.heartbeat.app.survey.ui.fields.textFieldItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SurveyUiState2(
    val surveyToken: String? = null,
    val step: AssessmentStep,
    val isLoading: Boolean = false,
    val answers: SurveyAnswers = SurveyAnswers(),
    val showsHandlingOnboardingAlert: Boolean = false,
    val showsAssessmentContinueAlert: Boolean = false,
    val isContinueButtonEnabled: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface SurveyAction {
    data class ChangeAnswer(val id: String, val answer: AnswerFormat) : SurveyAction
    data object Continue : SurveyAction
    data object Back : SurveyAction
}

@HiltViewModel(assistedFactory = SurveyViewModel.Factory::class)
class SurveyViewModel @AssistedInject constructor(
    @Assisted private val state: MainUiState.Authenticated.Survey.Content,
    private val repository: ChoirRepository,
) : ViewModel() {
    private val onboarding get() = state.onboarding

    private val _state = MutableStateFlow(
        SurveyUiState(
            pageTitle = state.onboarding.displayStatus.pageTitle ?: "",
            questionState = SurveyQuestionState.Question(
                progress = SurveyProgress(state.onboarding.displayStatus.progress?.toFloat() ?: 0f),
                title = SurveyQuestionTitle(state.onboarding.question.title1),
                fields = ChoicesFieldItemPreviewParameterProvider().values.toList() + datePickerFormField + textFieldArea + textFieldItem,
                continueButton = QuestionButton(
                    title = "Continue",
                    enabled = false,
                    onClick = {},
                )
            )
        )
    )

    private val _uiState = MutableStateFlow(
        SurveyUiState2(
            surveyToken = onboarding.displayStatus.surveyToken,
            step = AssessmentStep(
                displayStatus = onboarding.displayStatus,
                question = onboarding.question,
            ),
            isLoading = false
        )
    )

    val uiState = _state.asStateFlow()

    fun onAction(action: SurveyAction) {
        when (action) {
            is SurveyAction.ChangeAnswer ->
                handleChangeAnswer(action)

            is SurveyAction.Continue ->
                handleContinue(backRequest = false)

            is SurveyAction.Back ->
                handleContinue(backRequest = true)
        }
    }

    private fun handleChangeAnswer(action: SurveyAction.ChangeAnswer) {
        _uiState.update { state ->
            state.copy(
                answers = state.answers.copyWithChange(
                    id = action.id,
                    answer = action.answer
                ),
                isContinueButtonEnabled = state.step.question.fields?.all {
                    it.required != true || state.answers.answer(it.fieldId) != null
                } == true
            )
        }
    }

    private fun handleContinue(backRequest: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val state = _uiState.value
            repository.continueAssessment(
                token = state.surveyToken ?: "",
                submit = AssessmentSubmit(
                    submitStatus = state.step.let { step ->
                        SubmitStatus(
                            questionId = step.displayStatus.questionId,
                            questionType = QuestionType.FORM,
                            stepNumber = (step.displayStatus.stepNumber ?: "0").toDoubleOrNull()
                                ?: 0.0,
                            surveySectionId = step.displayStatus.surveySectionId,
                            sessionToken = step.displayStatus.sessionToken,
                            locale = step.displayStatus.locale,
                            backRequest = backRequest
                        )
                    },
                    answers = AssessmentSubmit.AnswersPayload(
                        value1 = state.answers.asFormAnswer()
                    )
                )
            ).onSuccess { success ->
                _uiState.update {
                    it.copy(
                        step = success,
                        answers = SurveyAnswers(),
                        isLoading = false
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        errorMessage = error.message ?: "An unknown error occurred.",
                        showsAssessmentContinueAlert = true,
                        isContinueButtonEnabled = false,
                        isLoading = false,
                    )
                }
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
