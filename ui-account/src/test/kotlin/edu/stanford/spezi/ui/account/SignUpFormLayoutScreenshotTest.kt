package edu.stanford.spezi.ui.account

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.ui.Modifier
import edu.stanford.spezi.testing.screenshot.ScreenshotTest
import edu.stanford.spezi.ui.AsyncTextButton
import edu.stanford.spezi.ui.ImageResource
import edu.stanford.spezi.ui.StringResource
import org.junit.Test
import java.time.Instant

class SignUpFormLayoutScreenshotTest : ScreenshotTest() {

    @Suppress("LongMethod")
    @Test
    fun `SignUpFormLayout screenshot`() {
        val layout = SignUpFormLayout(
            headerIcon = ImageResource(Icons.Default.AccountCircle),
            title = StringResource("Create a new Account"),
            description = StringResource("Please fill out the details below to create your new account."),
            sections = listOf(
                SignUpSection(
                    title = StringResource("CREDENTIALS"),
                    entries = listOf(
                        SignUpFormEntry(
                            title = StringResource("Email address"),
                            entry = StringDataEntry(placeholder = StringResource("Enter your email")),
                            value = "your.email@stanford.edu",
                            onValueChange = {}
                        ),
                        SignUpFormEntry(
                            title = StringResource("Password"),
                            entry = StringDataEntry(
                                placeholder = StringResource("Enter your password"),
                                hideContent = true,
                            ),
                            value = "12345778",
                            onValueChange = {}
                        ),
                    )
                ),
                SignUpSection(
                    title = StringResource("PERSONAL DETAILS"),
                    entries = listOf(
                        SignUpFormEntry(
                            title = StringResource("Name"),
                            entry = StringDataEntry(placeholder = StringResource("Enter your name")),
                            value = "John",
                            validationMessage = null,
                            onValueChange = {}
                        ),
                        SignUpFormEntry(
                            title = StringResource("Date of birth"),
                            entry = InstantDataEntry(
                                placeholder = StringResource("Select your birthday"),
                                formatter = { StringResource("01.01.2026") },
                            ),
                            value = Instant.now(),
                            onValueChange = {}
                        ),
                    )
                ),
            ),
            signUpButton = AsyncTextButton(
                title = StringResource("Signup"),
                action = {}
            ),
        )

        screenshot {
            layout.Content(modifier = Modifier.fillMaxSize())
        }
    }
}
