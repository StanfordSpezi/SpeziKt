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
import edu.stanford.bdh.heartbeat.app.survey.ui.fields.TextAreaFormFieldItem
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
                continueButton = QuestionButton(
                    title = "Continue",
                    onClick = { onAction(SurveyAction.Continue) },
                    enabled = FakeConfigs.FORCE_ENABLE_CONTINUE || questionFields.none { it.required == true }
                )
            )
        )
    }

    private fun mapFormFields(
        formFields: List<FormField>,
        onAction: (SurveyAction) -> Unit,
    ) : List<FormFieldItem> {
        return formFields.mapIndexed { index, formField ->
            val info = QuestionNumberInfo(current = index + 1, total = formFields.size)
            val fieldLabel = formField.label.takeIf { it?.isNotEmpty() == true }?.let { QuestionFieldLabel(label = it) }
            val fieldId = formField.fieldId
            when (formField.type) {
                FormField.Type.NUMBER -> TextFormFieldItem(
                    fieldId = fieldId,
                    info = info,
                    fieldLabel = fieldLabel,
                    style = TextFormFieldItem.Style.NUMERIC,
                    value = "",
                    onValueChange = { }
                )
                FormField.Type.TEXT -> TextFormFieldItem(
                    fieldId = fieldId,
                    info = info,
                    fieldLabel = fieldLabel,
                    style = TextFormFieldItem.Style.TEXT,
                    value = "",
                    onValueChange = { }
                )
                FormField.Type.HEADING -> HeadingFormFieldItem(
                    fieldId = fieldId,
                    text = formField.label,
                )
                FormField.Type.CHECKBOXES -> ChoicesFormFieldItem(
                    fieldId = fieldId,
                    info = info,
                    fieldLabel = fieldLabel,
                    style = ChoicesFormFieldItem.Style.Checkboxes,
                    selectedIds = emptyList(),
                    options = formField.values?.map {
                        ChoicesFormFieldItem.Option(id = it.id, label = it.label)
                    } ?: emptyList(),
                    onOptionClicked = { },
                )
                FormField.Type.RADIOS -> ChoicesFormFieldItem(
                    fieldId = fieldId,
                    info = info,
                    fieldLabel = fieldLabel,
                    style = ChoicesFormFieldItem.Style.Radios,
                    selectedIds = emptyList(),
                    options = formField.values?.map {
                        ChoicesFormFieldItem.Option(id = it.id, label = it.label)
                    } ?: emptyList(),
                    onOptionClicked = { }
                )
                FormField.Type.DROPDOWN -> ChoicesFormFieldItem(
                    fieldId = fieldId,
                    info = info,
                    fieldLabel = fieldLabel,
                    style = ChoicesFormFieldItem.Style.Dropdown(label = "Select an option...", initialExpanded = false),
                    selectedIds = emptyList(),
                    options = formField.values?.map {
                        ChoicesFormFieldItem.Option(id = it.id, label = it.label)
                    } ?: emptyList(),
                    onOptionClicked = { }
                )
                FormField.Type.DATE_PICKER -> DatePickerFormFieldItem(
                    fieldId = fieldId,
                    info = info,
                    fieldLabel = fieldLabel,
                    value = "",
                    onValueChange = { }
                )
                FormField.Type.TEXT_AREA -> TextAreaFormFieldItem(
                    fieldId = fieldId,
                    info = info,
                    fieldLabel = fieldLabel,
                    value = "",
                    onValueChange = { }
                )
                else -> UnsupportedFormFieldItem(
                    fieldId = formField.fieldId,
                    info = info,
                    fieldLabel = fieldLabel,
                    type = formField.type.name
                )
            }
        }
    }
}