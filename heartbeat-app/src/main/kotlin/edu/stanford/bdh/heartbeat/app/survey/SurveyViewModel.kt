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
import edu.stanford.bdh.heartbeat.app.choir.api.types.FormAnswer
import edu.stanford.bdh.heartbeat.app.choir.api.types.FormFieldAnswer
import edu.stanford.bdh.heartbeat.app.choir.api.types.SubmitStatus
import edu.stanford.bdh.heartbeat.app.main.MainUiState
import edu.stanford.bdh.heartbeat.app.survey.ui.SurveyQuestionState
import edu.stanford.bdh.heartbeat.app.survey.ui.fields.ChoicesFormFieldItem
import edu.stanford.bdh.heartbeat.app.survey.ui.fields.DatePickerFormFieldItem
import edu.stanford.bdh.heartbeat.app.survey.ui.fields.TextAreaFormFieldItem
import edu.stanford.bdh.heartbeat.app.survey.ui.fields.TextFormFieldItem
import edu.stanford.spezi.core.utils.DateFormat
import edu.stanford.spezi.core.utils.DateFormatter
import edu.stanford.spezi.core.utils.MessageNotifier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId

sealed interface SurveyAction {
    data class Update(val fieldId: String, val answer: AnswerUpdate) : SurveyAction
    data object Continue : SurveyAction
    data object Back : SurveyAction
}

sealed interface AnswerUpdate {
    data class OptionId(val value: String) : AnswerUpdate
    data class Text(val value: String) : AnswerUpdate
    data class Date(val value: Instant) : AnswerUpdate
}

@HiltViewModel(assistedFactory = SurveyViewModel.Factory::class)
class SurveyViewModel @AssistedInject constructor(
    @Assisted private val state: MainUiState.Authenticated.Survey.Content,
    private val repository: ChoirRepository,
    private val surveyUiStateMapper: SurveyUiStateMapper,
    private val messageNotifier: MessageNotifier,
    private val dateFormatter: DateFormatter,
) : ViewModel() {
    private val answers: MutableMap<String, Set<AnswerUpdate>> = mutableMapOf()

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
            is SurveyAction.Update -> handleUpdate(action)

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

    private fun handleUpdate(action: SurveyAction.Update) {
        val questionsState = _state.value.questionState as? SurveyQuestionState.Question
        val fieldItem = questionsState?.fields?.find { it.fieldId == action.fieldId }
        val answer = action.answer
        val updatedFieldItem = when {
            fieldItem is TextAreaFormFieldItem && answer is AnswerUpdate.Text -> {
                if (answer.value.isEmpty()) {
                    answers.remove(fieldItem.fieldId)
                } else {
                    answers[fieldItem.fieldId] = setOf(answer)
                }
                fieldItem.copy(value = answer.value)
            }

            fieldItem is TextFormFieldItem && answer is AnswerUpdate.Text -> {
                val sanitized = if (fieldItem.style == TextFormFieldItem.Style.NUMERIC && answer.value.toDoubleOrNull() == null) {
                    null
                } else {
                    answer.value.takeIf { it.isNotEmpty() }
                }
                if (sanitized == null) {
                    answers.remove(fieldItem.fieldId)
                } else {
                    answers[fieldItem.fieldId] = setOf(answer)
                }
                fieldItem.copy(value = sanitized ?: fieldItem.value)
            }

            fieldItem is ChoicesFormFieldItem && answer is AnswerUpdate.OptionId -> {
                val optionId = answer.value
                var style: ChoicesFormFieldItem.Style = fieldItem.style
                val newSelectedIds = when (style) {
                    is ChoicesFormFieldItem.Style.Radios -> {
                        answers[fieldItem.fieldId] = setOf(answer)
                        listOf(optionId)
                    }

                    is ChoicesFormFieldItem.Style.Dropdown -> {
                        style = style.copy(label = fieldItem.options.find { it.id == answer.value }?.label ?: "")
                        answers[fieldItem.fieldId] = setOf(answer)
                        listOf(optionId)
                    }

                    is ChoicesFormFieldItem.Style.Checkboxes -> {
                        val currentSelections = fieldItem.selectedIds.toMutableSet()
                        if (currentSelections.contains(optionId)) {
                            currentSelections.remove(optionId)
                        } else {
                            currentSelections.add(
                                optionId
                            )
                        }
                        answers[fieldItem.fieldId] =
                            currentSelections.map { AnswerUpdate.OptionId(it) }.toSet()
                        currentSelections.toList()
                    }
                }
                fieldItem.copy(selectedIds = newSelectedIds, style = style)
            }

            fieldItem is DatePickerFormFieldItem && answer is AnswerUpdate.Date -> {
                val selectedDate = answer.value
                val formatted =
                    dateFormatter.format(selectedDate, DateFormat.MM_DD_YYYY, ZoneId.of("UTC"))
                answers[fieldItem.fieldId] = mutableSetOf(answer)
                fieldItem.copy(value = formatted)
            }

            else -> return
        }

        val newFieldItems = questionsState.fields.map {
            if (it.fieldId == updatedFieldItem.fieldId) updatedFieldItem else it
        }

        val requiredFields =
            currentAssessmentStep.question.fields?.filter { it.required == true } ?: emptyList()

        _state.update {
            it.copy(
                questionState = questionsState.copy(
                    fields = newFieldItems,
                    continueButton = questionsState.continueButton.copy(
                        enabled = requiredFields.all { answers.contains(fieldItem.fieldId) }
                    )
                ),
            )
        }
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
                    answers = FormAnswer(
                        fieldAnswers = answers.map { entry ->
                            FormFieldAnswer(
                                fieldId = entry.key,
                                choice = entry.value.map { answerUpdate ->
                                    when (answerUpdate) {
                                        is AnswerUpdate.Text -> answerUpdate.value
                                        is AnswerUpdate.OptionId -> answerUpdate.value
                                        is AnswerUpdate.Date -> answerUpdate.value.epochSecond.toString()
                                    }
                                }
                            )
                        }
                    )
                )
            ).onSuccess { success ->
                answers.clear()
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
