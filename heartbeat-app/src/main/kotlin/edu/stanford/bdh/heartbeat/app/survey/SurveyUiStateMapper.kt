package edu.stanford.bdh.heartbeat.app.survey

import edu.stanford.bdh.heartbeat.app.choir.api.types.AssessmentStep
import edu.stanford.bdh.heartbeat.app.choir.api.types.FormField
import edu.stanford.bdh.heartbeat.app.fake.FakeConfigs
import edu.stanford.bdh.heartbeat.app.survey.ui.QuestionButton
import edu.stanford.bdh.heartbeat.app.survey.ui.QuestionFieldLabel
import edu.stanford.bdh.heartbeat.app.survey.ui.QuestionNumberInfo
import edu.stanford.bdh.heartbeat.app.survey.ui.SurveyProgress
import edu.stanford.bdh.heartbeat.app.survey.ui.SurveyQuestionState
import edu.stanford.bdh.heartbeat.app.survey.ui.SurveyQuestionTitle
import edu.stanford.bdh.heartbeat.app.survey.ui.SurveyUiState
import edu.stanford.bdh.heartbeat.app.survey.ui.fields.ChoicesFormFieldItem
import edu.stanford.bdh.heartbeat.app.survey.ui.fields.DatePickerFormFieldItem
import edu.stanford.bdh.heartbeat.app.survey.ui.fields.FormFieldItem
import edu.stanford.bdh.heartbeat.app.survey.ui.fields.HeadingFormFieldItem
import edu.stanford.bdh.heartbeat.app.survey.ui.fields.TextFormFieldItem
import edu.stanford.bdh.heartbeat.app.survey.ui.fields.UnsupportedFormFieldItem
import javax.inject.Inject

class SurveyUiStateMapper @Inject constructor() {

    fun map(
        assessmentStep: AssessmentStep,
        onAction: (SurveyAction) -> Unit,
    ): SurveyUiState {
        val question = assessmentStep.question
        val displayStatus = assessmentStep.displayStatus
        val questionFields = question.fields ?: emptyList()
        return SurveyUiState(
            pageTitle = displayStatus.pageTitle ?: "Heartbeat Study",
            questionState = SurveyQuestionState.Question(
                progress = SurveyProgress(value = displayStatus.progress?.toFloat() ?: 0f),
                title = SurveyQuestionTitle(question.title1),
                fields = mapFormFields(formFields = questionFields, onAction = onAction),
                backButton = if (displayStatus.showBack == true) {
                    QuestionButton(
                        title = "Back",
                        onClick = { onAction(SurveyAction.Back) },
                        enabled = true
                    )
                } else {
                    null
                },
                continueButton = QuestionButton(
                    title = if (question.terminal == true) "Finish" else "Continue",
                    onClick = { onAction(SurveyAction.Continue) },
                    enabled = FakeConfigs.FORCE_ENABLE_CONTINUE || questionFields.none { it.required == true }
                )
            )
        )
    }

    private fun mapFormFields(
        formFields: List<FormField>,
        onAction: (SurveyAction) -> Unit,
    ): List<FormFieldItem> {
        return formFields.mapIndexed { index, formField ->
            val info = QuestionNumberInfo(current = index + 1, total = formFields.size)
            val fieldLabel =
                formField.label?.takeIf { it.isNotEmpty() }?.let { QuestionFieldLabel(it) }
            val fieldId = formField.fieldId

            when (formField.type) {
                FormField.Type.NUMBER,
                FormField.Type.TEXT,
                FormField.Type.TEXT_AREA -> TextFormFieldItem(
                    fieldId = fieldId,
                    info = info,
                    fieldLabel = fieldLabel,
                    warning = mapWarning(formField = formField),
                    displayWarning = false,
                    style = when (formField.type) {
                        FormField.Type.TEXT_AREA -> TextFormFieldItem.Style.TEXT_AREA
                        FormField.Type.NUMBER -> TextFormFieldItem.Style.NUMERIC
                        else -> TextFormFieldItem.Style.TEXT
                    },
                    value = "",
                    onValueChange = {
                        onAction(SurveyAction.Update(fieldId = fieldId, answer = AnswerUpdate.Text(it)))
                    }
                )

                FormField.Type.HEADING -> HeadingFormFieldItem(
                    fieldId = fieldId,
                    text = formField.label
                )

                FormField.Type.DATE_PICKER -> DatePickerFormFieldItem(
                    fieldId = fieldId,
                    info = info,
                    fieldLabel = fieldLabel,
                    value = "",
                    onValueChange = { onAction(SurveyAction.Update(fieldId, AnswerUpdate.Date(it))) }
                )

                FormField.Type.CHECKBOXES, FormField.Type.RADIOS, FormField.Type.DROPDOWN ->
                    mapChoiceField(
                        formField = formField,
                        info = info,
                        fieldLabel = fieldLabel,
                        onAction = onAction
                    )

                else -> UnsupportedFormFieldItem(
                    fieldId = fieldId,
                    type = formField.type.name,
                    info = info,
                    fieldLabel = fieldLabel,
                )
            }
        }
    }

    private fun mapChoiceField(
        formField: FormField,
        info: QuestionNumberInfo,
        fieldLabel: QuestionFieldLabel?,
        onAction: (SurveyAction) -> Unit,
    ): ChoicesFormFieldItem {
        val style = when (formField.type) {
            FormField.Type.CHECKBOXES -> ChoicesFormFieldItem.Style.Checkboxes
            FormField.Type.RADIOS -> ChoicesFormFieldItem.Style.Radios
            FormField.Type.DROPDOWN -> ChoicesFormFieldItem.Style.Dropdown(
                label = "Select an option...",
                initialExpanded = false
            )

            else -> error("Unsupported choice type: ${formField.type}")
        }
        return ChoicesFormFieldItem(
            fieldId = formField.fieldId,
            info = info,
            fieldLabel = fieldLabel,
            style = style,
            selectedIds = emptySet(),
            options = formField.values?.map { ChoicesFormFieldItem.Option(it.id, it.label) }
                ?: emptyList(),
            onOptionClicked = {
                onAction(SurveyAction.Update(formField.fieldId, AnswerUpdate.OptionId(it)))
            }
        )
    }

    private fun mapWarning(formField: FormField) = if (formField.type == FormField.Type.NUMBER) {
        val min = formField.min
        val max = formField.max
        val rangeInfo = if (min != null && max != null) " from $min to $max." else ""
        "Please enter a valid number$rangeInfo"
    } else if (formField.required == true) {
        "This answer is required!"
    } else {
        null
    }
}
