package edu.stanford.spezi.ui.account

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import edu.stanford.spezi.ui.SpeziInputFieldComposable
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.ThemePreviews

/**
 * A data entry composable for string based account values
 *
 * @param placeholder The placeholder text to display when the field is empty.
 * @param hideContent Whether to hide the content of the field, e.g. password fields.
 */
data class StringDataEntry(
    val placeholder: StringResource?,
    val hideContent: Boolean = false,
) : DataEntryComposable<String> {

    @Composable
    override fun Content(value: String, onValueChange: (String) -> Unit, modifier: Modifier) {
        SpeziInputFieldComposable(
            value = value,
            onValueChanged = onValueChange,
            modifier = modifier,
            hideContent = hideContent,
            placeholder = placeholder,
        )
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    val entry = StringDataEntry(placeholder = StringResource("Enter your email"))

    SpeziTheme {
        entry.Content(value = "email@stanford.edu", onValueChange = {})
    }
}
