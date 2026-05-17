package edu.stanford.spezi.ui.account

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.ui.Modifier
import edu.stanford.spezi.testing.screenshot.ScreenshotTest
import edu.stanford.spezi.ui.AsyncTextButton
import edu.stanford.spezi.ui.ImageResource
import edu.stanford.spezi.ui.StringResource
import org.junit.Test

class AccountChangePasswordLayoutScreenshotTest : ScreenshotTest() {

    @Test
    fun `AccountChangePasswordLayout no error screenshot`() {
        val layout = AccountChangePasswordLayout(
            newPassword = "hunter2",
            confirmPassword = "hunter2",
            onNewPasswordChange = {},
            onConfirmPasswordChange = {},
            validationMessage = null,
            saveButton = AsyncTextButton(title = StringResource("Save"), action = {}),
            icon = ImageResource(Icons.Default.Lock),
        )

        screenshot {
            layout.Content(modifier = Modifier.fillMaxSize())
        }
    }

    @Test
    fun `AccountChangePasswordLayout with validation error screenshot`() {
        val layout = AccountChangePasswordLayout(
            newPassword = "abc",
            confirmPassword = "xyz",
            onNewPasswordChange = {},
            onConfirmPasswordChange = {},
            validationMessage = StringResource("Passwords do not match."),
            saveButton = AsyncTextButton(title = StringResource("Save"), action = {}),
            icon = ImageResource(Icons.Default.Lock),
        )

        screenshot {
            layout.Content(modifier = Modifier.fillMaxSize())
        }
    }
}
