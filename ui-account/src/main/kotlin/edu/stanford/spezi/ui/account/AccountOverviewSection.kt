package edu.stanford.spezi.ui.account

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import edu.stanford.spezi.ui.ComposableContent
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.account.internal.AccountSectionContent
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.ThemePreviews

/**
 * Data class representing a section in the account overview screen.
 *
 * @param title The title of the section.
 * @param items The list of items to be displayed in the section.
 */
data class AccountOverviewSection(
    val title: StringResource?,
    val items: List<AnyAccountOverviewItem>,
) : ComposableContent {

    @Composable
    override fun Content(modifier: Modifier) {
        AccountSectionContent(
            title = title,
            entries = items,
            modifier = modifier,
        )
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    val name = AccountOverviewItem(
        title = StringResource("Name"),
        valueDisplay = StringDataDisplay(),
        value = "John Smith",
        leadingImage = null,
        showArrow = true,
        onClick = {},
    )

    val genderIdentity = AccountOverviewItem(
        title = StringResource("Gender identity"),
        valueDisplay = StringDataDisplay(),
        value = "Prefer not to state",
        leadingImage = null,
        showArrow = false,
        onClick = {},
    )

    val section = AccountOverviewSection(
        title = StringResource("NAME"),
        items = listOf(
            name,
            genderIdentity,
        ),
    )

    SpeziTheme {
        section.Content(modifier = Modifier.fillMaxWidth())
    }
}
