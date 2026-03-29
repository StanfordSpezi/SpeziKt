package edu.stanford.spezi.ui.account

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import edu.stanford.spezi.ui.ComposableContent
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.account.internal.AccountSectionContent
import edu.stanford.spezi.ui.theme.Spacings
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.ThemePreviews

/**
 * Represents a section of the sign up form.
 *
 * @param title The title of the section.
 * @param entries The entries in the section.
 */
data class SignUpSection(
    val title: StringResource,
    val entries: List<AnySignUpFormEntry>,
) : ComposableContent {

    @Composable
    override fun Content(modifier: Modifier) {
        AccountSectionContent(
            modifier = modifier,
            title = title,
            entries = entries,
        )
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    val section = SignUpSection(
        title = StringResource("NAME"),
        entries = listOf(
            SignUpFormEntry(
                title = StringResource("First name"),
                entry = StringDataEntry(placeholder = StringResource("Enter your first name")),
                value = "John",
                onValueChange = {}
            ),
            SignUpFormEntry(
                title = StringResource("Last name"),
                entry = StringDataEntry(
                    placeholder = StringResource("Enter your last name"),
                    hideContent = false,
                ),
                value = "Smith",
                onValueChange = {}
            ),
            SignUpFormEntry(
                title = StringResource("Consent"),
                entry = BooleanDataEntry(description = StringResource("Accept data collection")),
                value = true,
                onValueChange = {}
            ),
        )
    )

    SpeziTheme {
        section.Content(modifier = Modifier.padding(Spacings.medium))
    }
}
