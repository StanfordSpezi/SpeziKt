package edu.stanford.spezi.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import edu.stanford.spezi.testing.screenshot.ScreenshotTest
import org.junit.Test

class SpeziAppBarScreenshotTests : ScreenshotTest() {

    @Test
    fun `SpeziAppBar text title only screenshot`() {
        val appBar = speziAppBar {
            title("Hello Spezi")
        }
        screenshot { appBar.Content() }
    }

    @Test
    fun `SpeziAppBar complete screenshot`() {
        val appBar = speziAppBar {
            title("Hello Spezi")
            close { }
            action(ImageResource(Icons.Default.Favorite))
        }
        screenshot { appBar.Content() }
    }

    @Test
    fun `SpeziAppBar back only screenshot`() {
        val appBar = speziAppBar {
            back { }
        }
        screenshot { appBar.Content() }
    }

    @Test
    fun `SpeziAppBar composable navigation screenshot`() {
        val appBar = speziAppBar {
            navigation(ImageResource(Icons.Default.MoreVert))
        }
        screenshot { appBar.Content() }
    }

    @Test
    fun `SpeziAppBar widget navigation screenshot`() {
        val appBar = speziAppBar {
            navigation(SpeziAppBarWidgets.navigation(ImageResource(Icons.Default.Close)) { })
        }
        screenshot { appBar.Content() }
    }

    @Test
    fun `SpeziAppBar non center aligned screenshot`() {
        val appBar = speziAppBar {
            title("Hello Spezi")
            centerAlign(false)
        }
        screenshot { appBar.Content() }
    }
}
