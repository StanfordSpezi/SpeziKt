package edu.stanford.bdh.heartbeat.app.survey.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import edu.stanford.bdh.heartbeat.app.survey.ui.fields.FormFieldItem
import edu.stanford.spezi.core.design.theme.ThemePreviews

data class TextFieldItem(
    override val id: String,
    val info: QuestionNumberInfo,
    val fieldLabel: QuestionFieldLabel,
    val value: String,
    val onValueChange: (String) -> Unit,
) : FormFieldItem {

    @Composable
    override fun Content(modifier: Modifier) {
        SurveyCard(modifier = modifier) {
            info.Content(Modifier)
            fieldLabel.Content(Modifier)
            TextField(
                value = value,
                modifier = Modifier.fillMaxWidth(),
                onValueChange = { onValueChange(it) },
                placeholder = { Text("Enter your input here...") }
            )
        }
    }
}

@ThemePreviews
@Composable
private fun Previews() {
    val textFieldItem = TextFieldItem(
        id = "",
        info = QuestionNumberInfo(2, 11),
        fieldLabel = QuestionFieldLabel("Phone number"),
        value = "",
        onValueChange = {},
    )
    SurveyItemPreview {
        textFieldItem.Content(Modifier)
    }
}