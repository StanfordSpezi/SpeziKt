package edu.stanford.bdh.heartbeat.app.onboarding

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OnboardingUiState(
    val surveyToken: String? = null,
    val step: AssessmentStep? = null,
    val isLoading: Boolean = false,
    val answers: OnboardingAnswers = OnboardingAnswers(),
    val showsHandlingOnboardingAlert: Boolean = false,
    val showsAssessmentContinueAlert: Boolean = false,
    val isContinueButtonEnabled: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface OnboardingAction {
    data class ChangeAnswer(val id: String, val answer: AnswerFormat) : OnboardingAction
    data object Continue : OnboardingAction
    data object Back : OnboardingAction
}

@HiltViewModel(assistedFactory = OnboardingViewModel.Factory::class)
class OnboardingViewModel @AssistedInject constructor(
    @Assisted private val state: MainUiState.Authenticated.Questionnaire.Content,
    private val repository: ChoirRepository,
) : ViewModel() {
    private val onboarding get() = state.onboarding

    private val _uiState = MutableStateFlow(OnboardingUiState(
        surveyToken = onboarding.displayStatus.surveyToken,
        step = AssessmentStep(
            displayStatus = onboarding.displayStatus,
            question = onboarding.question,
        ),
        isLoading = false
    ))
    val uiState = _uiState.asStateFlow()

    fun onAction(action: OnboardingAction) {
        when (action) {
            is OnboardingAction.ChangeAnswer ->
                handleChangeAnswer(action)

            is OnboardingAction.Continue ->
                handleContinue(backRequest = false)

            is OnboardingAction.Back ->
                handleContinue(backRequest = true)
        }
    }

    private fun handleChangeAnswer(action: OnboardingAction.ChangeAnswer) {
        _uiState.update { state ->
            state.copy(
                answers = state.answers.copyWithChange(
                    id = action.id,
                    answer = action.answer
                ),
                isContinueButtonEnabled = state.step?.question?.fields?.all {
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
                token = state.surveyToken ?: error("No survey token available."),
                submit = AssessmentSubmit(
                    submitStatus = state.step?.let { step ->
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
                        answers = OnboardingAnswers(),
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
            content: MainUiState.Authenticated.Questionnaire.Content,
        ): OnboardingViewModel
    }
}
