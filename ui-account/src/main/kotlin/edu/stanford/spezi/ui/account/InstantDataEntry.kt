package edu.stanford.spezi.ui.account

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import edu.stanford.spezi.ui.DatePickerFormFieldItemComposable
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.ThemePreviews
import java.time.Instant

/**
 * A data entry composable for [Instant] based values, e.g. birth dates.
 *
 * @param placeholder The placeholder text to display when the field is empty.
 * @param formatter A function that takes an [Instant] and returns a formatted string.
 */
data class InstantDataEntry(
    val placeholder: StringResource,
    val formatter: (Instant) -> StringResource,
) : DataEntryComposable<Instant> {
    @Composable
    override fun Content(value: Instant, onValueChange: (Instant) -> Unit, modifier: Modifier) {
        DatePickerFormFieldItemComposable(
            placeholder = placeholder,
            onValueChange = onValueChange,
            value = formatter(value).text(),
            modifier = modifier,
        )
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    val entry = InstantDataEntry(
        placeholder = StringResource("Enter your birthdate"),
        formatter = { StringResource("01.01.2026") },
    )
    SpeziTheme {
        entry.Content(value = Instant.now(), onValueChange = {}, modifier = Modifier.fillMaxWidth())
    }
}
