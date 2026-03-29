package edu.stanford.spezi.ui.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.theme.Colors
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.ThemePreviews

/**
 * Composable for displaying a data entry with a boolean value.
 *
 * @param description The description of the data entry.
 */
data class BooleanDataEntry(val description: StringResource) : DataEntryComposable<Boolean> {

    @Composable
    override fun Content(value: Boolean, onValueChange: (Boolean) -> Unit, modifier: Modifier) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = description.text(),
                color = Colors.secondary,
            )

            Switch(
                checked = value,
                onCheckedChange = onValueChange,
            )
        }
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    val entry = BooleanDataEntry(description = StringResource("Accept data collection"))

    SpeziTheme {
        entry.Content(
            modifier = Modifier.fillMaxWidth(),
            value = true,
            onValueChange = {},
        )
    }
}
