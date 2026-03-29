package edu.stanford.spezi.ui.account

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import edu.stanford.spezi.testing.screenshot.ScreenshotTest
import edu.stanford.spezi.ui.AsyncTextButton
import edu.stanford.spezi.ui.ImageResource
import edu.stanford.spezi.ui.StringResource
import org.junit.Test

class AlternativeSignInScreenshotTest : ScreenshotTest() {

    @Test
    fun `AlternativeSignIn screenshot`() {
        val content = AlternativeSignIn(
            divider = LabeledHorizontalDivider(
                label = StringResource("or"),
            ),
            buttons = listOf(
                AsyncTextButton(
                    title = StringResource("Sign in with Google"),
                    icon = ImageResource(R.drawable.ic_google),
                    action = {}
                ),
                AsyncTextButton(
                    title = StringResource("Sign in with Stanford"),
                    icon = ImageResource(Icons.Default.School),
                    action = {}
                )
            )
        )
        screenshot {
            content.Content()
        }
    }
}
