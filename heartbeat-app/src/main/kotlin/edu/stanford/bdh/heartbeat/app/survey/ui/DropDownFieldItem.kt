package edu.stanford.bdh.heartbeat.app.survey.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import edu.stanford.spezi.core.design.theme.Colors
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.ThemePreviews

data class DropDownFieldItem(
    override val id: String,
    override val required: Boolean,
    val info: QuestionNumberInfo,
    val fieldLabel: QuestionFieldLabel,
    val dropdownLabel: String,
    val options: List<Option>,
    val selectedId: String?,
    val onOptionClicked: (String) -> Unit,
) : FormFieldItem {

    data class Option(val id: String, val label: String)

    @Composable
    override fun Content(modifier: Modifier) {
        var expanded by remember { mutableStateOf(true) }

        SurveyCard(modifier = Modifier.clickable { expanded = !expanded }) {

            Column {
                info.Content(Modifier)

                fieldLabel.Content(Modifier)

                Row(
                    modifier = Modifier
                        .clickable { expanded = !expanded }
                        .fillMaxWidth()
                        .padding(bottom = Spacings.medium),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = dropdownLabel,
                        modifier = Modifier.weight(1f),
                    )
                    val image = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown
                    Icon(imageVector = image, contentDescription = "Dropdown")
                }

                HorizontalDivider(color = Colors.black20)
                if (expanded) {
                    Column {
                        options.forEach { option ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onOptionClicked(option.id) }
                                    .padding(vertical = Spacings.medium),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = option.label, modifier = Modifier.weight(1f))
                                if (selectedId == option.id) {
                                    Icon(imageVector = Icons.Filled.Check, contentDescription = "Dropdown")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun Previews() {
    val dropDown = DropDownFieldItem(
        id = "",
        required = true,
        info = QuestionNumberInfo(2, 11),
        options = List(10) { DropDownFieldItem.Option(id = "$it", label = "Option ${it + 1}")},
        fieldLabel = QuestionFieldLabel("State"),
        dropdownLabel = "Select an option",
        selectedId = "2",
        onOptionClicked = {}
    )
    SurveyPreview {
        dropDown.Content(Modifier)
    }
}