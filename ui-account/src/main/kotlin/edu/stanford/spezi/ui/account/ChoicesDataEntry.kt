package edu.stanford.spezi.ui.account

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import edu.stanford.spezi.ui.ChoicesFormFieldItem
import edu.stanford.spezi.ui.ChoicesFormFieldItemComposable
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.ThemePreviews

/**
 * Composable for displaying a data entry with a list of choices.
 *
 * @param choices The list of choices.
 * @param style The style of the choices.
 * @param optionTransformer A function that transforms a choice into a [ChoicesFormFieldItem.Option].
 * @param valueTransformer A function that transforms an option id into a value.
 */
data class ChoicesDataEntry<Value>(
    val choices: List<Value>,
    val style: ChoicesFormFieldItem.Style = ChoicesFormFieldItem.Style.Radios,
    val optionTransformer: (Value) -> ChoicesFormFieldItem.Option,
    val valueTransformer: (id: String) -> Value,
) : DataEntryComposable<Value> {

    @Composable
    override fun Content(value: Value, onValueChange: (Value) -> Unit, modifier: Modifier) {
        ChoicesFormFieldItemComposable(
            modifier = modifier,
            style = style,
            options = choices.map { optionTransformer(it) },
            selectedIds = setOf(optionTransformer(value).id),
            onOptionClicked = { onValueChange(valueTransformer(it)) },
        )
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    val options = listOf(1, 2)
    val entry = ChoicesDataEntry(
        choices = options,
        optionTransformer = { ChoicesFormFieldItem.Option(id = it.toString(), label = StringResource("Option: $it")) },
        valueTransformer = { it },
    )

    SpeziTheme {
        entry.Content(value = "1", onValueChange = {}, modifier = Modifier)
    }
}
