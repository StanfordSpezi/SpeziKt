package edu.stanford.spezi.ui.account

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import edu.stanford.spezi.testing.screenshot.ScreenshotTest
import org.junit.Test

class AccountProfileHeaderScreenshotTest : ScreenshotTest() {

    @Test
    fun `AccountProfileHeader screenshot`() {
        val header = AccountProfileHeader(
            initials = "LS",
            name = "Leland Stanford",
            description = "lelandstanford@stanford.edu"
        )

        screenshot {
            header.Content(modifier = Modifier.fillMaxWidth())
        }
    }
}
