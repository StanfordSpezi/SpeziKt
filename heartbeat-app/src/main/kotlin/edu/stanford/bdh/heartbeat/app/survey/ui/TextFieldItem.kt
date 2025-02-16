package edu.stanford.bdh.heartbeat.app.survey.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import edu.stanford.spezi.core.design.theme.ThemePreviews

data class TextFieldItem(
    override val id: String,
    override val required: Boolean,
    val info: QuestionNumberInfo,
    val fieldLabel: QuestionFieldLabel,
    val value: String,
    val onValueChange: (String) -> Unit,
) : FormFieldItem {

    @Composable
    override fun Content(modifier: Modifier) {
        var expanded by remember { mutableStateOf(true) }

        SurveyCard(modifier = Modifier.clickable { expanded = !expanded }) {

            Column {
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
}

@ThemePreviews
@Composable
private fun Previews() {
    val textFieldItem = TextFieldItem(
        id = "",
        required = true,
        info = QuestionNumberInfo(2, 11),
        fieldLabel = QuestionFieldLabel("Phone number"),
        value = "",
        onValueChange = {},
    )
    SurveyPreview {
        textFieldItem.Content(Modifier)
    }
}