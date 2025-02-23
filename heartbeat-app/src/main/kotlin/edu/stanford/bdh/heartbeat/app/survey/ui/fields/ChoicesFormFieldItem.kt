package edu.stanford.bdh.heartbeat.app.survey.ui.fields

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import edu.stanford.bdh.heartbeat.app.survey.ui.QuestionFieldLabel
import edu.stanford.bdh.heartbeat.app.survey.ui.QuestionNumberInfo
import edu.stanford.bdh.heartbeat.app.survey.ui.SurveyCard
import edu.stanford.bdh.heartbeat.app.survey.ui.SurveyItemPreview
import edu.stanford.spezi.core.design.theme.Colors
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.TextStyles
import edu.stanford.spezi.core.design.theme.ThemePreviews

data class ChoicesFormFieldItem(
    override val fieldId: String,
    val style: Style,
    val info: QuestionNumberInfo,
    val fieldLabel: QuestionFieldLabel?,
    val options: List<Option>,
    val selectedIds: Set<String>,
    val onOptionClicked: (String) -> Unit,
) : FormFieldItem {

    private val isDropDown get() = style is Style.Dropdown

    sealed interface Style {
        data object Radios : Style
        data object Checkboxes : Style
        data class Dropdown(val label: String, val initialExpanded: Boolean) : Style
    }

    data class Option(val id: String, val label: String)

    @Composable
    override fun Body(modifier: Modifier) {
        var expanded by remember {
            mutableStateOf(if (style is Style.Dropdown) style.initialExpanded else true)
        }

        SurveyCard(modifier = modifier) {
            info.body
            fieldLabel?.body

            if (style is Style.Dropdown) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded }
                        .padding(vertical = Spacings.medium),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val hasSelections = selectedIds.isNotEmpty()
                    val alpha = if (hasSelections) 1f else 0.5f
                    Text(
                        text = style.label,
                        modifier = Modifier
                            .alpha(alpha)
                            .weight(1f),
                        style = if (hasSelections) TextStyles.titleMedium else LocalTextStyle.current
                    )
                    val image =
                        if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown
                    Icon(imageVector = image, contentDescription = "Dropdown")
                }
            }

            if (isDropDown) HorizontalDivider(color = Colors.black20)
            Options(expanded = expanded)
        }
    }

    @Composable
    private fun Options(expanded: Boolean) {
        if (expanded) {
            val rowPadding = if (isDropDown) Spacings.medium else Spacings.extraSmall
            options.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = isDropDown) { onOptionClicked(option.id) }
                        .padding(vertical = rowPadding),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = option.label, modifier = Modifier.padding(end = Spacings.small).weight(1f))
                    Choice(id = option.id)
                }
            }
        }
    }

    @Composable
    private fun Choice(id: String) {
        when (style) {
            is Style.Dropdown -> {
                if (id in selectedIds) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Dropdown"
                    )
                }
            }

            is Style.Radios -> {
                RadioButton(
                    selected = id in selectedIds,
                    onClick = { onOptionClicked(id) }
                )
            }

            is Style.Checkboxes -> {
                Checkbox(
                    checked = id in selectedIds,
                    onCheckedChange = { onOptionClicked(id) }
                )
            }
        }
    }
}

class ChoicesFieldItemPreviewParameterProvider :
    PreviewParameterProvider<ChoicesFormFieldItem> {
    private val base = ChoicesFormFieldItem(
        fieldId = "",
        info = QuestionNumberInfo(1, 2),
        options = List(2 * 2) { ChoicesFormFieldItem.Option(id = "$it", label = "Option ${it + 1}") },
        fieldLabel = QuestionFieldLabel("State"),
        selectedIds = setOf("2"),
        style = ChoicesFormFieldItem.Style.Radios,
        onOptionClicked = {},
    )

    override val values: Sequence<ChoicesFormFieldItem>
        get() = sequenceOf(
            base.copy(fieldLabel = QuestionFieldLabel("Radio field item")),
            base.copy(
                fieldLabel = QuestionFieldLabel("Checkboxes field item"),
                style = ChoicesFormFieldItem.Style.Checkboxes,
                selectedIds = setOf("2", "4")
            ),
            base.copy(
                fieldLabel = QuestionFieldLabel("Dropdown field item collapsed"),
                style = ChoicesFormFieldItem.Style.Dropdown(
                    label = "Select an option...",
                    initialExpanded = false
                ),
                selectedIds = emptySet()
            ),
            base.copy(
                fieldLabel = QuestionFieldLabel("Dropdown field item expanded"),
                style = ChoicesFormFieldItem.Style.Dropdown(label = "Option 3", initialExpanded = true),
                selectedIds = setOf("2")
            )
        )
}

@ThemePreviews
@Composable
private fun Previews(@PreviewParameter(ChoicesFieldItemPreviewParameterProvider::class) model: ChoicesFormFieldItem) {
    SurveyItemPreview {
        model.body
    }
}
