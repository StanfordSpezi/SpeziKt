package edu.stanford.spezi.ui.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import edu.stanford.spezi.ui.AsyncTextButton
import edu.stanford.spezi.ui.ComposableContent
import edu.stanford.spezi.ui.ImageResource
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.theme.Colors
import edu.stanford.spezi.ui.theme.Sizes
import edu.stanford.spezi.ui.theme.Spacings
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.TextStyles
import edu.stanford.spezi.ui.theme.ThemePreviews
import edu.stanford.spezi.ui.theme.bold

/**
 * Layout for editing a single account key value, with an icon, entry field, optional validation message, and save button.
 *
 * @param icon Icon shown at the top of the layout.
 * @param title Label of the account key being edited.
 * @param entryComposable Entry widget for the key's value type.
 * @param value Current value pre-filled into the entry widget.
 * @param validationMessage Inline error shown below the entry field, or `null` if the value is valid.
 * @param onValueChange Called when the user changes the value.
 * @param saveButton Primary action button.
 */
data class AccountEditLayout<Value : Any>(
    val icon: ImageResource,
    val title: StringResource,
    val entryComposable: DataEntryComposable<Value>,
    val value: Value,
    val validationMessage: StringResource?,
    val onValueChange: (Value) -> Unit,
    val saveButton: AsyncTextButton,
) : ComposableContent {

    @Composable
    override fun Content(modifier: Modifier) {
        Column(
            modifier = modifier
                .padding(Spacings.medium)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Spacings.medium),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            icon.Content(modifier = Modifier.size(Sizes.Icon.medium))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacings.small),
            ) {
                Text(
                    modifier = Modifier.padding(top = Spacings.small),
                    text = title.text(),
                    style = TextStyles.bodyMedium.bold(),
                )
                entryComposable.Content(
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

            saveButton.Content(modifier = Modifier.fillMaxWidth())
        }
    }
}

/** Type-erased alias for [AccountEditLayout] when the value type is not statically known. */
typealias AnyAccountEditLayout = AccountEditLayout<*>

@ThemePreviews
@Composable
private fun Preview() {
    val layout = AccountEditLayout(
        icon = ImageResource(Icons.Default.Edit),
        title = StringResource("Email address"),
        entryComposable = StringDataEntry(placeholder = StringResource("Enter your email")),
        value = "user@stanford.edu",
        validationMessage = null,
        onValueChange = {},
        saveButton = AsyncTextButton(title = StringResource("Save"), action = {}),
    )

    SpeziTheme {
        layout.Content()
    }
}
