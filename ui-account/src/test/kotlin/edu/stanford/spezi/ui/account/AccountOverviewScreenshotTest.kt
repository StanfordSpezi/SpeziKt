package edu.stanford.spezi.ui.account

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Security
import androidx.compose.ui.Modifier
import edu.stanford.spezi.testing.screenshot.ScreenshotTest
import edu.stanford.spezi.ui.AsyncTextButton
import edu.stanford.spezi.ui.ImageResource
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.theme.Colors
import org.junit.Test
import java.time.Instant

class AccountOverviewScreenshotTest : ScreenshotTest() {

    @Suppress("LongMethod")
    @Test
    fun `AccountOverviewLayout screenshot`() {
        val initialSection = AccountOverviewSection(
            title = null,
            items = listOf(
                AccountOverviewItem(
                    title = StringResource("Name, E-Mail Address"),
                    valueDisplay = StringDataDisplay(),
                    value = "",
                    leadingImage = ImageResource(Icons.Default.AccountBox),
                    showArrow = true,
                    onClick = {},
                ),
                AccountOverviewItem(
                    title = StringResource("Sign-In & Security"),
                    valueDisplay = StringDataDisplay(),
                    value = "",
                    leadingImage = ImageResource(Icons.Default.Security),
                    showArrow = true,
                    onClick = {},
                )
            )
        )

        val personalDetailsSection = AccountOverviewSection(
            title = StringResource("PERSONAL DETAILS"),
            items = listOf(
                AccountOverviewItem(
                    title = StringResource("Gender identity"),
                    valueDisplay = StringDataDisplay(),
                    value = "Prefer not to state",
                    leadingImage = null,
                    showArrow = false,
                    onClick = {},
                ),
                AccountOverviewItem(
                    title = StringResource("Date of Birth"),
                    valueDisplay = ValueTextDisplay<Instant> {
                        StringResource("Mar 9, 1824")
                    },
                    value = Instant.now(),
                    leadingImage = null,
                    showArrow = false,
                    onClick = {},
                ),
            ),
        )

        val licensesSection = AccountOverviewSection(
            title = null,
            items = listOf(
                AccountOverviewItem(
                    title = StringResource("License information"),
                    valueDisplay = StringDataDisplay(),
                    value = "",
                    leadingImage = null,
                    showArrow = true,
                    onClick = {},
                ),
            )
        )

        val layout = AccountOverviewLayout(
            title = StringResource("Account overview"),
            header = AccountProfileHeader(
                initials = "LS",
                name = "Leland Stanford",
                email = "lelandstanford@stanford.edu",
            ),
            sections = listOf(
                initialSection,
                personalDetailsSection,
                licensesSection,
            ),
            logout = AsyncTextButton(
                title = StringResource("Logout"),
                containerColor = { Colors.error },
                action = {}
            ),
            onClose = {},
            onEdit = {},
        )
        screenshot {
            layout.Content(modifier = Modifier.fillMaxSize())
        }
    }
}
