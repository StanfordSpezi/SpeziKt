package edu.stanford.bdh.heartbeat.app.survey.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.ThemePreviews

data class CheckboxFieldItem(
    override val id: String,
    override val required: Boolean,
    val info: QuestionNumberInfo,
    val fieldLabel: QuestionFieldLabel,
    val dropdownLabel: String,
    val options: List<Option>,
    val selectedIds: List<String>,
    val onOptionClicked: (String) -> Unit,
) : FormFieldItem {

    data class Option(val id: String, val label: String)

    @Composable
    override fun Content(modifier: Modifier) {

        SurveyCard {
            info.Content(Modifier)
            fieldLabel.Content(Modifier)

            options.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOptionClicked(option.id) }
                        .padding(vertical = Spacings.extraSmall),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = option.label, modifier = Modifier.weight(1f))
                    Checkbox(
                        checked = option.id in selectedIds,
                        onCheckedChange = { onOptionClicked(option.id) }
                    )
                }
            }
        }
    }
}


@ThemePreviews
@Composable
private fun Previews() {
    val checkBox = CheckboxFieldItem(
        id = "",
        required = true,
        info = QuestionNumberInfo(2, 11),
        options = List(10) { CheckboxFieldItem.Option(id = "$it", label = "Option ${it + 1}")},
        fieldLabel = QuestionFieldLabel("State"),
        dropdownLabel = "Select an option",
        selectedIds = listOf("2", "4"),
        onOptionClicked = {}
    )
    SurveyPreview {
        checkBox.Content(Modifier)
    }
}