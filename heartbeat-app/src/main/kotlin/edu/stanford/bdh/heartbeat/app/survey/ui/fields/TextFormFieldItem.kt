package edu.stanford.bdh.heartbeat.app.survey.ui.fields

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import edu.stanford.bdh.heartbeat.app.survey.ui.QuestionFieldLabel
import edu.stanford.bdh.heartbeat.app.survey.ui.QuestionNumberInfo
import edu.stanford.bdh.heartbeat.app.survey.ui.SurveyCard
import edu.stanford.bdh.heartbeat.app.survey.ui.SurveyItemPreview
import edu.stanford.spezi.core.design.component.bringIntoViewOnFocusedEvent
import edu.stanford.spezi.core.design.theme.ThemePreviews

data class TextFormFieldItem(
    override val fieldId: String,
    val style: Style,
    val info: QuestionNumberInfo,
    val fieldLabel: QuestionFieldLabel?,
    val value: String,
    val onValueChange: (String) -> Unit,
) : FormFieldItem {

    enum class Style {
        NUMERIC,
        TEXT
    }

    @Composable
    override fun Content(modifier: Modifier) {
        SurveyCard(modifier = modifier) {
            info.Content(Modifier)
            fieldLabel?.Content(Modifier)
            TextField(
                value = value,
                modifier = Modifier
                    .bringIntoViewOnFocusedEvent()
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = if (style == Style.NUMERIC) KeyboardType.Number else KeyboardType.Text
                ),
                onValueChange = onValueChange,
                placeholder = { Text("Enter your input here...") }
            )
        }
    }
}

val textFieldItem = TextFormFieldItem(
    fieldId = "",
    style = TextFormFieldItem.Style.NUMERIC,
    info = QuestionNumberInfo(2, 11),
    fieldLabel = QuestionFieldLabel("Phone number"),
    value = "",
    onValueChange = {},
)

@ThemePreviews
@Composable
private fun Previews() {

    SurveyItemPreview {
        textFieldItem.Content(Modifier)
    }
}
