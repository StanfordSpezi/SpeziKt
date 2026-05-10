package edu.stanford.spezi.ui.account

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.ThemePreviews

/**
 * A DataDisplayComposable for any value type.
 *
 * @param transform A function that takes a value and returns a StringResource to be displayed a the string representation of the value,
 */
data class ValueTextDisplay<Value>(
    val transform: (Value) -> StringResource,
) : DataDisplayComposable<Value> {
    @Composable
    override fun Content(value: Value, modifier: Modifier) {
        AccountValueText(
            text = transform(value).text(),
            modifier = modifier,
        )
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    val display = ValueTextDisplay<Boolean> {
        StringResource(if (it) "Accepted" else "Denied")
    }

    SpeziTheme {
        display.Content(value = false)
    }
}
