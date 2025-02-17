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
import edu.stanford.bdh.heartbeat.app.choir.api.types.FormField
import edu.stanford.bdh.heartbeat.app.choir.api.types.FormFieldAnswer
import edu.stanford.bdh.heartbeat.app.choir.api.types.SubmitStatus
import edu.stanford.bdh.heartbeat.app.fake.FakeConfigs
import edu.stanford.bdh.heartbeat.app.main.MainUiState
import edu.stanford.bdh.heartbeat.app.survey.ui.SurveyQuestionState
import edu.stanford.bdh.heartbeat.app.survey.ui.fields.ChoicesFormFieldItem
import edu.stanford.bdh.heartbeat.app.survey.ui.fields.DatePickerFormFieldItem
import edu.stanford.bdh.heartbeat.app.survey.ui.fields.TextFormFieldItem
import edu.stanford.spezi.core.logging.speziLogger
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
    private val logger by speziLogger()

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
            is TextFormFieldItem -> {
                val isValidAnswer = isValidAnswer(value = answerValue, fieldItem = fieldItem)
                val sanitized = answerValue.takeIf { isValidAnswer }
                session.store(fieldId = fieldId, answer = sanitized)
                fieldMap[fieldId] = fieldItem.copy(
                    value = answerValue,
                    displayWarning = isValidAnswer.not()
                )
            }

            is ChoicesFormFieldItem -> {
                var style = fieldItem.style
                val newSelectedIds = when (style) {
                    is ChoicesFormFieldItem.Style.Checkboxes -> {
                        fieldItem.selectedIds.toMutableSet().apply {
                            if (!add(answerValue)) remove(answerValue)
                        }
                    }

                    is ChoicesFormFieldItem.Style.Radios -> setOf(answerValue)
                    is ChoicesFormFieldItem.Style.Dropdown -> {
                        val newLabel = fieldItem.options.find { it.id == answerValue }?.label
                        style = style.copy(label = newLabel ?: style.label)
                        setOf(answerValue)
                    }
                }
                session.store(fieldId = fieldId, answers = newSelectedIds)
                fieldMap[fieldId] = fieldItem.copy(
                    selectedIds = newSelectedIds,
                    style = style
                )
            }

            is DatePickerFormFieldItem -> {
                session.store(fieldId = fieldId, answer = answerValue)
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
            }.onFailure { error ->
                logger.e(error) { "Failure while submitting the answer" }
                messageNotifier.notify("An error occurred when submitting your answer")
                _uiState.update { it.copy(questionState = currentQuestionsState) }
            }
        }
    }

    private fun stringValue(answer: AnswerUpdate) = when (answer) {
        is AnswerUpdate.Text -> answer.value
        is AnswerUpdate.OptionId -> answer.value
        // TODO: This is the formatted displayed date, also sent as choice.
        //    Clarify expected date format from backend.
        is AnswerUpdate.Date -> dateFormatter.format(
            instant = answer.value,
            format = DateFormat.MM_DD_YYYY,
            zoneId = ZoneId.of("UTC")
        )
    }

    private fun isValidAnswer(value: String, fieldItem: TextFormFieldItem): Boolean {
        val formField = session.formFields[fieldItem.fieldId]
        return when {
            fieldItem.style == TextFormFieldItem.Style.NUMERIC -> {
                val answerValue = value.toDoubleOrNull() ?: return false
                val min = formField?.min?.toDoubleOrNull() ?: Double.MIN_VALUE
                val max = formField?.max?.toDoubleOrNull() ?: Double.MAX_VALUE
                answerValue in min..max
            }

            formField?.required == true && value.isBlank() -> false
            else -> true
        }
    }

    private inner class Session {
        val formFields = mutableMapOf<String, FormField>()
        val requiredFields = hashSetOf<String>()
        val choices = mutableMapOf<String, Set<String>>()

        fun setup(assessmentStep: AssessmentStep) {
            requiredFields.clear()
            choices.clear()
            formFields.clear()
            assessmentStep.question.fields?.forEach {
                formFields[it.fieldId] = it
                if (it.required == true) requiredFields.add(it.fieldId)
            }
        }

        fun store(fieldId: String, answer: String?) {
            if (answer.isNullOrEmpty()) choices.remove(fieldId) else choices[fieldId] =
                setOf(answer)
        }

        fun store(fieldId: String, answers: Set<String>) {
            if (answers.isEmpty()) choices.remove(fieldId) else choices[fieldId] = answers
        }

        fun isContinueAllowed() = FakeConfigs.FORCE_ENABLE_CONTINUE ||
            requiredFields.all { id -> choices[id]?.isNotEmpty() == true }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            content: MainUiState.Authenticated.Survey.Content,
        ): SurveyViewModel
    }
}
