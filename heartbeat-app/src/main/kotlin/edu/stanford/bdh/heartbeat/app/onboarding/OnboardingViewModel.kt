package edu.stanford.bdh.heartbeat.app.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.stanford.bdh.heartbeat.app.choir.ChoirRepository
import edu.stanford.bdh.heartbeat.app.choir.api.types.AssessmentStep
import edu.stanford.bdh.heartbeat.app.choir.api.types.AssessmentSubmit
import edu.stanford.bdh.heartbeat.app.choir.api.types.QuestionType
import edu.stanford.bdh.heartbeat.app.choir.api.types.SubmitStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

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
    data object Reload : OnboardingAction
    data object Continue : OnboardingAction
    data object Back : OnboardingAction
}

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val repository: ChoirRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState = _uiState.asStateFlow()

    fun onAction(action: OnboardingAction) {
        when (action) {
            is OnboardingAction.ChangeAnswer ->
                handleChangeAnswer(action)
            is OnboardingAction.Reload ->
                handleReload()
            is OnboardingAction.Continue ->
                handleContinue(backRequest = false)
            is OnboardingAction.Back ->
                handleContinue(backRequest = true)
        }
    }

    private fun handleChangeAnswer(action: OnboardingAction.ChangeAnswer) {
        TODO("Check if all answers have been answered and set uiState.continueButtonEnabled appropriately")
        _uiState.update { state ->
            state.copy(
                answers = state.answers.copyWithChange(
                    id = action.id,
                    answer = action.answer
                )
            )
        }
    }

    private fun handleReload() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            runCatching {
                repository.getOnboarding()
            }.onSuccess { onboarding ->
                _uiState.update {
                    it.copy(
                        surveyToken = onboarding.displayStatus.surveyToken,
                        step = AssessmentStep(
                            displayStatus = onboarding.displayStatus,
                            question = AssessmentStep.QuestionPayload(
                                value1 = onboarding.question
                            )
                        ),
                        isLoading = false
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        showsAssessmentContinueAlert = true,
                        isContinueButtonEnabled = false,
                        errorMessage = error.message ?: "An unknown error occurred.",
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun handleContinue(backRequest: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val state = _uiState.value
            runCatching {
                repository.continueAssessment(
                    token = state.surveyToken ?: error("No survey token available."),
                    submit = AssessmentSubmit(
                        submitStatus = state.step?.let { step ->
                            SubmitStatus(
                                questionId = step.displayStatus.questionId,
                                questionType = QuestionType.FORM,
                                stepNumber = (step.displayStatus.stepNumber ?: "0").toDoubleOrNull() ?: 0.0,
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
                )
            }.onSuccess { success ->
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
}
