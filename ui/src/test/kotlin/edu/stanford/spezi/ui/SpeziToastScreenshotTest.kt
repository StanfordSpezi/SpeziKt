package edu.stanford.spezi.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import edu.stanford.spezi.testing.screenshot.ScreenshotTest
import org.junit.Test

class SpeziToastScreenshotTest : ScreenshotTest() {

    @Test
    fun `SpeziToast with icon and short message screenshot`() {
        val toast = SpeziToast(
            image = ImageResource(Icons.Default.CheckCircle),
            message = StringResource("Changes saved successfully."),
            onClick = {},
        )

        screenshot { toast.Content() }
    }

    @Test
    fun `SpeziToast with icon and long message screenshot`() {
        val toast = SpeziToast(
            image = ImageResource(Icons.Default.Error),
            message = StringResource(
                "An unexpected error occurred while loading your data. Please check your connection and try again."
            ),
            onClick = {},
        )

        screenshot { toast.Content() }
    }

    @Test
    fun `SpeziToast with long message and no icon screenshot`() {
        val toast = SpeziToast(
            image = null,
            message = StringResource(
                "An unexpected error occurred while loading your data. Please check your connection and try again."
            ),
            onClick = {},
        )

        screenshot { toast.Content() }
    }
}
