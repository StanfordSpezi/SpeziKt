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

    private var currentAssessmentStep = with(state.onboarding) {
        AssessmentStep(
            question = question,
            displayStatus = displayStatus
        )
    }
    private val session = Session().apply { setup(assessmentStep = currentAssessmentStep) }

    private val _uiState = MutableStateFlow(
        surveyUiStateMapper.map(
            assessmentStep = currentAssessmentStep,
            onAction = ::onAction
        )
    )

    val uiState = _uiState.asStateFlow()

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

            is SurveyAction.Back -> handleContinue(backRequest = true)
        }
    }

    private fun handleUpdate(action: SurveyAction.Update) {
        val questionsState = _uiState.value.questionState as? SurveyQuestionState.Question ?: return
        val fieldMap = questionsState.fields.associateBy { it.fieldId }.toMutableMap()
        val fieldId = action.fieldId
        val fieldItem = fieldMap[fieldId]
        val answer = action.answer
        val answerValue = stringValue(answer = answer)
        when (fieldItem) {
            is TextAreaFormFieldItem -> {
                if (answerValue.isEmpty()) {
                    session.choices.remove(fieldId)
                } else {
                    session.choices[fieldId] = setOf(answerValue)
                }
                fieldMap[fieldId] = fieldItem.copy(value = answerValue)
            }

            is TextFormFieldItem -> {
                if (fieldItem.style == TextFormFieldItem.Style.NUMERIC && answerValue.toDoubleOrNull() == null) return
                session.choices[fieldId] = setOfNotNull(answerValue.takeIf { it.isNotBlank() })
                fieldMap[fieldId] = fieldItem.copy(value = answerValue)
            }

            is ChoicesFormFieldItem -> {
                var style = fieldItem.style
                val newSelectedIds = when (style) {
                    is ChoicesFormFieldItem.Style.Radios -> setOf(answerValue)
                    is ChoicesFormFieldItem.Style.Dropdown -> {
                        val newLabel = fieldItem.options.find { it.id == answerValue }?.label
                        style = style.copy(label = newLabel ?: style.label)
                        setOf(answerValue)
                    }
                    is ChoicesFormFieldItem.Style.Checkboxes -> {
                        val currentSelections = fieldItem.selectedIds.toMutableSet()
                        if (!currentSelections.add(answerValue)) currentSelections.remove(answerValue)
                        currentSelections
                    }
                }
                session.choices[fieldId] = newSelectedIds
                fieldMap[fieldId] = fieldItem.copy(
                    selectedIds = newSelectedIds.toList(),
                    style = style
                )
            }

            is DatePickerFormFieldItem -> {
                session.choices[fieldId] = setOf(answerValue)
                fieldMap[fieldId] = fieldItem.copy(value = answerValue)
            }

            else -> return
        }

        _uiState.update {
            it.copy(
                questionState = questionsState.copy(
                    fields = fieldMap.values.toList(),
                    continueButton = questionsState.continueButton.copy(
                        enabled = session.isContinueAllowed()
                    )
                ),
            )
        }
    }

    private fun handleContinue(backRequest: Boolean) {
        viewModelScope.launch {
            val currentQuestionsState = _uiState.value.questionState
            _uiState.update { it.copy(questionState = SurveyQuestionState.Loading) }
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
                        fieldAnswers = session.choices.map { entry ->
                            FormFieldAnswer(
                                fieldId = entry.key,
                                choice = entry.value.toList()
                            )
                        }
                    )
                )
            ).onSuccess { success ->
                currentAssessmentStep = success
                session.setup(assessmentStep = success)
                _uiState.update {
                    surveyUiStateMapper.map(
                        assessmentStep = success,
                        onAction = ::onAction
                    )
                }
            }.onFailure { _ ->
                messageNotifier.notify("An error occurred when submitting your answer")
                _uiState.update { it.copy(questionState = currentQuestionsState) }
            }
        }
    }

    private fun stringValue(answer: AnswerUpdate) = when (answer) {
        is AnswerUpdate.Text -> answer.value
        is AnswerUpdate.OptionId -> answer.value
        is AnswerUpdate.Date -> dateFormatter.format(answer.value, DateFormat.MM_DD_YYYY, ZoneId.of("UTC"))
    }

    private inner class Session {
        private val requiredFields = hashSetOf<String>()
        val choices: MutableMap<String, Set<String>> = mutableMapOf()

        fun setup(assessmentStep: AssessmentStep) {
            requiredFields.clear()
            choices.clear()
            assessmentStep.question.fields?.forEach {
                if (it.required == true) requiredFields.add(it.fieldId)
            }
        }

        fun isContinueAllowed() = requiredFields.all { id -> choices[id]?.isNotEmpty() == true }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            content: MainUiState.Authenticated.Survey.Content,
        ): SurveyViewModel
    }
}
