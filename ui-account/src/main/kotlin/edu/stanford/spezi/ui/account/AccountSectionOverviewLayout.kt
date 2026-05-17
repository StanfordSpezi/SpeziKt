package edu.stanford.spezi.ui.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import edu.stanford.spezi.ui.ComposableContent
import edu.stanford.spezi.ui.ImageResource
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.theme.Sizes
import edu.stanford.spezi.ui.theme.Spacings
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.ThemePreviews

/**
 * Layout displaying an icon and a single section of tappable account overview rows.
 *
 * @param icon Icon shown at the top of the layout.
 * @param section Rows to display.
 */
data class AccountSectionOverviewLayout(
    val icon: ImageResource,
    val section: AccountOverviewSection,
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
            section.Content(modifier = Modifier.fillMaxWidth())
        }
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    val nameRow = AccountOverviewItem(
        title = StringResource("Full Name"),
        valueDisplay = StringDataDisplay(),
        value = "Leland Stanford",
        leadingImage = null,
        showArrow = true,
        onClick = {},
    )
    val emailRow = AccountOverviewItem(
        title = StringResource("Email Address"),
        valueDisplay = StringDataDisplay(),
        value = "lelandstanford@stanford.edu",
        leadingImage = null,
        showArrow = true,
        onClick = {},
    )
    val layout = AccountSectionOverviewLayout(
        section = AccountOverviewSection(
            title = null,
            items = listOf(nameRow, emailRow),
        ),
        icon = ImageResource(Icons.Default.AccountCircle),
    )
    SpeziTheme {
        layout.Content(modifier = Modifier.fillMaxSize())
    }
}
