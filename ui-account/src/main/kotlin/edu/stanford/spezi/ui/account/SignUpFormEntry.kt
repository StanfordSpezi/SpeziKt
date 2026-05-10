package edu.stanford.spezi.ui.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import edu.stanford.spezi.ui.ComposableContent
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.theme.Colors
import edu.stanford.spezi.ui.theme.Spacings
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.TextStyles
import edu.stanford.spezi.ui.theme.ThemePreviews
import edu.stanford.spezi.ui.theme.bold

/**
 * Renders an entry in the Sign up form
 *
 * @param title The title of the entry
 * @param entry The entry itself
 * @param value The value of the entry
 * @param onValueChange The callback to be invoked when the value changes
 * @param validationMessage The validation message to be displayed if the value is invalid
 */
data class SignUpFormEntry<Value>(
    val title: StringResource,
    val entry: DataEntryComposable<Value>,
    val value: Value,
    val onValueChange: (Value) -> Unit,
    val validationMessage: StringResource? = null,
) : ComposableContent {
    @Composable
    override fun Content(modifier: Modifier) {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Spacings.small),
        ) {
            Text(
                modifier = Modifier.padding(top = Spacings.small),
                text = title.text(),
                style = TextStyles.bodyMedium.bold()
            )
            entry.Content(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
            )
            validationMessage?.let {
                Text(
                    text = it.text(),
                    color = Colors.error,
                )
            }
        }
    }
}

typealias AnySignUpFormEntry = SignUpFormEntry<*>

@ThemePreviews
@Composable
private fun Preview() {
    val entry = SignUpFormEntry(
        title = StringResource("Last name"),
        entry = StringDataEntry(
            placeholder = StringResource("Enter your last name"),
            hideContent = false,
        ),
        value = "Smith",
        onValueChange = {}
    )

    SpeziTheme {
        entry.Content()
    }
}
