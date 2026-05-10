package edu.stanford.spezi.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import edu.stanford.spezi.testing.screenshot.ScreenshotTest
import org.junit.Test

class LoadingLayoutScreenshotTest : ScreenshotTest() {

    @Test
    fun `LoadingLayout with message screenshot`() {
        val layout = LoadingLayout(
            message = StringResource("Loading data, please wait...")
        )

        screenshot { layout.Content(modifier = Modifier.fillMaxSize()) }
    }

    @Test
    fun `LoadingLayout without message screenshot`() {
        val layout = LoadingLayout()

        screenshot { layout.Content(modifier = Modifier.fillMaxSize()) }
    }
}
