package edu.stanford.spezi.ui.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import edu.stanford.spezi.ui.AsyncTextButton
import edu.stanford.spezi.ui.CloseButton
import edu.stanford.spezi.ui.ComposableContent
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.theme.Colors
import edu.stanford.spezi.ui.theme.Spacings
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.TextStyles
import edu.stanford.spezi.ui.theme.ThemePreviews
import edu.stanford.spezi.ui.theme.bold

/**
 * Composable content rendering the overview screen
 *
 * @param title title of the screen
 * @param header header of the screen
 * @param sections list of sections to be rendered
 * @param logout button to logout
 * @param onClose action to be performed when the close button is clicked
 * @param onEdit action to be performed when the edit button is clicked
 */
data class AccountOverviewLayout(
    val title: StringResource,
    val header: AccountProfileHeader,
    val sections: List<AccountOverviewSection>,
    val logout: AsyncTextButton,
    val onClose: () -> Unit,
    val onEdit: () -> Unit,
) : ComposableContent {
    @Composable
    override fun Content(modifier: Modifier) {
        Column(
            modifier = modifier
                .padding(Spacings.medium)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Spacings.large),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CloseButton(onClick = onClose)

                Text(
                    text = title.text(),
                    style = TextStyles.bodyLarge.bold(),
                )

                TextButton(onClick = onEdit) {
                    Text(text = stringResource(edu.stanford.spezi.ui.R.string.edit_action_title))
                }
            }

            header.Content()

            sections.forEach {
                it.Content(modifier = Modifier.fillMaxWidth())
            }

            logout.Content(modifier = Modifier.fillMaxWidth())
        }
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
    val layout = AccountOverviewLayout(
        title = StringResource("Account overview"),
        header = AccountProfileHeader(
            initials = "LS",
            name = "Leland Stanford",
            email = "lelandstanford@stanford.edu",
        ),
        sections = listOf(
            section,
        ),
        logout = AsyncTextButton(
            title = StringResource("Logout"),
            containerColor = { Colors.error },
            action = {}
        ),
        onClose = {},
        onEdit = {},
    )

    SpeziTheme {
        layout.Content(modifier = Modifier.fillMaxSize())
    }
}
