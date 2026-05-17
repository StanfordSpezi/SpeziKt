package edu.stanford.spezi.ui.account

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.Modifier
import edu.stanford.spezi.testing.screenshot.ScreenshotTest
import edu.stanford.spezi.ui.AsyncTextButton
import edu.stanford.spezi.ui.ImageResource
import edu.stanford.spezi.ui.StringResource
import org.junit.Test

class AccountEditLayoutScreenshotTest : ScreenshotTest() {

    @Test
    fun `AccountEditLayout string field no validation error screenshot`() {
        val layout = AccountEditLayout(
            icon = ImageResource(Icons.Default.Edit),
            title = StringResource("Email address"),
            entryComposable = StringDataEntry(placeholder = StringResource("Enter your email")),
            value = "user@stanford.edu",
            validationMessage = null,
            onValueChange = {},
            saveButton = AsyncTextButton(title = StringResource("Save"), action = {}),
        )

        screenshot {
            layout.Content(modifier = Modifier.fillMaxSize())
        }
    }

    @Test
    fun `AccountEditLayout string field with validation error screenshot`() {
        val layout = AccountEditLayout(
            icon = ImageResource(Icons.Default.Edit),
            title = StringResource("Email address"),
            entryComposable = StringDataEntry(placeholder = StringResource("Enter your email")),
            value = "not-an-email",
            validationMessage = StringResource("Please enter a valid email address."),
            onValueChange = {},
            saveButton = AsyncTextButton(title = StringResource("Save"), action = {}),
        )

        screenshot {
            layout.Content(modifier = Modifier.fillMaxSize())
        }
    }

    @Test
    fun `AccountEditLayout password field screenshot`() {
        val layout = AccountEditLayout(
            icon = ImageResource(Icons.Default.Edit),
            title = StringResource("Password"),
            entryComposable = StringDataEntry(
                placeholder = StringResource("Enter your new password"),
                hideContent = true,
            ),
            value = "secret123",
            validationMessage = null,
            onValueChange = {},
            saveButton = AsyncTextButton(title = StringResource("Save"), action = {}),
        )

        screenshot {
            layout.Content(modifier = Modifier.fillMaxSize())
        }
    }

    @Test
    fun `AccountEditLayout boolean field screenshot`() {
        val layout = AccountEditLayout(
            icon = ImageResource(Icons.Default.Edit),
            title = StringResource("Notifications"),
            entryComposable = BooleanDataEntry(description = StringResource("Receive email notifications")),
            value = true,
            validationMessage = null,
            onValueChange = {},
            saveButton = AsyncTextButton(title = StringResource("Save"), action = {}),
        )

        screenshot {
            layout.Content(modifier = Modifier.fillMaxSize())
        }
    }
}
