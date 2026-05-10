package edu.stanford.spezi.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import edu.stanford.spezi.ui.ChoicesFormFieldItem.Option
import edu.stanford.spezi.ui.ChoicesFormFieldItem.Style
import edu.stanford.spezi.ui.theme.Spacings
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.TextStyles
import edu.stanford.spezi.ui.theme.ThemePreviews

data class ChoicesFormFieldItem(
    val style: Style,
    val options: List<Option>,
    val selectedIds: Set<String>,
    val onOptionClicked: (String) -> Unit,
) : ComposableContent {

    sealed interface Style {
        data object Radios : Style
        data object Checkboxes : Style
        data class Dropdown(val label: StringResource, val initialExpanded: Boolean) : Style
    }

    data class Option(val id: String, val label: StringResource)

    @Composable
    override fun Content(modifier: Modifier) {
        ChoicesFormFieldItemComposable(
            modifier = modifier,
            style = style,
            options = options,
            selectedIds = selectedIds,
            onOptionClicked = onOptionClicked,
        )
    }
}

@Composable
fun ChoicesFormFieldItemComposable(
    modifier: Modifier = Modifier,
    style: Style,
    options: List<Option>,
    selectedIds: Set<String>,
    onOptionClicked: (String) -> Unit,
) {
    val isDropDown = style is Style.Dropdown
    Column(modifier = modifier) {
        var expanded by remember {
            mutableStateOf(if (style is Style.Dropdown) style.initialExpanded else true)
        }

        if (style is Style.Dropdown) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(vertical = Spacings.medium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val hasSelections = selectedIds.isNotEmpty()
                Text(
                    text = style.label.text(),
                    modifier = Modifier
                        .alpha(hasSelections)
                        .weight(1f),
                    style = if (hasSelections) TextStyles.bodyMedium else LocalTextStyle.current
                )
                val image = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown
                Icon(imageVector = image, contentDescription = "Dropdown")
            }
        }

        if (isDropDown) HorizontalDivider()
        Options(
            expanded = expanded,
            style = style,
            selectedIds = selectedIds,
            options = options,
            onOptionClicked = onOptionClicked,
        )
    }
}

@Composable
private fun Options(
    expanded: Boolean,
    style: Style,
    options: List<Option>,
    onOptionClicked: (String) -> Unit,
    selectedIds: Set<String>,
) {
    if (expanded) {
        val isDropDown = style is Style.Dropdown
        options.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = isDropDown) { onOptionClicked(option.id) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = option.label.text(),
                    modifier = Modifier
                        .padding(end = Spacings.small)
                        .weight(1f)
                )
                Choice(
                    id = option.id,
                    style = style,
                    onOptionClicked = onOptionClicked,
                    selected = option.id in selectedIds,
                )
            }
        }
    }
}

@Composable
private fun Choice(
    id: String,
    style: Style,
    selected: Boolean,
    onOptionClicked: (String) -> Unit,
) {
    when (style) {
        is Style.Dropdown -> {
            Box(modifier = Modifier.minimumInteractiveComponentSize()) {
                if (selected) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Dropdown"
                    )
                }
            }
        }

        is Style.Radios -> {
            RadioButton(
                selected = selected,
                onClick = { onOptionClicked(id) }
            )
        }

        is Style.Checkboxes -> {
            Checkbox(
                checked = selected,
                onCheckedChange = { onOptionClicked(id) }
            )
        }
    }
}

private class ChoicesFieldItemPreviewParameterProvider :
    PreviewParameterProvider<ChoicesFormFieldItem> {
    private val base = ChoicesFormFieldItem(
        options = List(2 * 2) { Option(id = "$it", label = StringResource("Option ${it + 1}")) },
        selectedIds = setOf("12"),
        style = Style.Radios,
        onOptionClicked = {},
    )

    override val values: Sequence<ChoicesFormFieldItem>
        get() = sequenceOf(
            base,
            base.copy(
                style = Style.Checkboxes,
                selectedIds = setOf("2", "4")
            ),
            base.copy(
                style = Style.Dropdown(
                    label = StringResource("Select an option..."),
                    initialExpanded = false
                ),
                selectedIds = emptySet()
            ),
            base.copy(
                style = Style.Dropdown(label = StringResource("Option 3"), initialExpanded = true),
                selectedIds = setOf("2")
            )
        )
}

@ThemePreviews
@Composable
private fun Previews(@PreviewParameter(ChoicesFieldItemPreviewParameterProvider::class) model: ChoicesFormFieldItem) {
    SpeziTheme {
        model.Content()
    }
}
