package edu.stanford.spezi.ui.account

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.ui.Modifier
import edu.stanford.spezi.testing.screenshot.ScreenshotTest
import edu.stanford.spezi.ui.ImageResource
import edu.stanford.spezi.ui.StringResource
import org.junit.Test

class AccountSectionOverviewLayoutScreenshotTest : ScreenshotTest() {

    @Test
    fun `AccountSectionOverviewLayout name and email screenshot`() {
        val layout = AccountSectionOverviewLayout(
            icon = ImageResource(Icons.Default.AccountCircle),
            section = AccountOverviewSection(
                title = null,
                items = listOf(
                    AccountOverviewItem(
                        title = StringResource("Full Name"),
                        valueDisplay = StringDataDisplay(),
                        value = "Leland Stanford",
                        leadingImage = null,
                        showArrow = true,
                        onClick = {},
                    ),
                    AccountOverviewItem(
                        title = StringResource("Email Address"),
                        valueDisplay = StringDataDisplay(),
                        value = "lelandstanford@stanford.edu",
                        leadingImage = null,
                        showArrow = true,
                        onClick = {},
                    ),
                ),
            ),
        )

        screenshot {
            layout.Content(modifier = Modifier.fillMaxSize())
        }
    }

    @Test
    fun `AccountSectionOverviewLayout security screenshot`() {
        val layout = AccountSectionOverviewLayout(
            icon = ImageResource(Icons.Default.Lock),
            section = AccountOverviewSection(
                title = null,
                items = listOf(
                    AccountOverviewItem(
                        title = StringResource("Change Password"),
                        valueDisplay = StringDataDisplay(),
                        value = "",
                        leadingImage = null,
                        showArrow = true,
                        onClick = {},
                    ),
                ),
            ),
        )

        screenshot {
            layout.Content(modifier = Modifier.fillMaxSize())
        }
    }
}
