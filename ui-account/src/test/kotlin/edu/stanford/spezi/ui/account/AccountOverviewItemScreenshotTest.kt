package edu.stanford.spezi.ui.account

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.SpatialAudioOff
import edu.stanford.spezi.testing.screenshot.ScreenshotTest
import edu.stanford.spezi.ui.ImageResource
import edu.stanford.spezi.ui.StringResource
import org.junit.Test
import java.time.Instant

class AccountOverviewItemScreenshotTest : ScreenshotTest() {

    @Test
    fun `AccountOverviewItem string screenshot`() {
        val item = AccountOverviewItem(
            title = StringResource("Email address"),
            valueDisplay = StringDataDisplay(),
            value = "email@stanford.edu",
            leadingImage = ImageResource(Icons.Default.Email),
            showArrow = true,
            onClick = {},
        )
        screenshot {
            item.Content()
        }
    }

    @Test
    fun `AccountOverviewItem Instant value screenshot`() {
        val item = AccountOverviewItem(
            title = StringResource("Date of birth"),
            valueDisplay = ValueTextDisplay<Instant>(
                transform = { instant -> StringResource("20/01/2000") },
            ),
            value = Instant.now(),
            leadingImage = ImageResource(Icons.Default.CalendarMonth),
            showArrow = true,
            onClick = {},
        )
        screenshot {
            item.Content()
        }
    }

    @Test
    fun `AccountOverviewItem Boolean value screenshot`() {
        val item = AccountOverviewItem(
            title = StringResource("Allow data collection"),
            valueDisplay = ValueTextDisplay(
                transform = { value -> StringResource("Off") }
            ),
            value = false,
            leadingImage = ImageResource(Icons.Default.SpatialAudioOff),
            showArrow = true,
            onClick = {},
        )
        screenshot {
            item.Content()
        }
    }
}
