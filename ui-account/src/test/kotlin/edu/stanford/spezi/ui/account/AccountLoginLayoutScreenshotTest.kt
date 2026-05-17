package edu.stanford.spezi.ui.account

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.ui.Modifier
import edu.stanford.spezi.resources.Drawables
import edu.stanford.spezi.testing.screenshot.ScreenshotTest
import edu.stanford.spezi.ui.AsyncTextButton
import edu.stanford.spezi.ui.ImageResource
import edu.stanford.spezi.ui.SpeziInputField
import edu.stanford.spezi.ui.StringResource
import org.junit.Test

class AccountLoginLayoutScreenshotTest : ScreenshotTest() {
    private val baseLayout = AccountLoginLayout(
        title = StringResource("Your account"),
        description = StringResource("Please login to your account. Or create a new one if you don't have one already."),
        userIdInput = SpeziInputField(
            value = "email@stanford.edu",
            placeholder = StringResource("Enter your email"),
            onValueChanged = {}
        ),
        passwordInput = SpeziInputField(
            value = "123456",
            hideContent = true,
            placeholder = StringResource("Enter your password"),
            onValueChanged = {}
        ),
        forgotPasswordLink = ForgotPasswordLink(
            text = StringResource("Forgot password?"),
            onClick = {}
        ),
        loginButton = AsyncTextButton(
            title = StringResource("Login"),
            action = {}
        ),
        signUpLink = SignUpLink(
            infoText = StringResource("Don't have an account yet?"),
            signUpText = StringResource("Sign up"),
            onClick = {},
        ),
        alternativeSignIn = AlternativeSignIn(
            divider = LabeledHorizontalDivider(label = StringResource("or")),
            buttons = listOf(
                AsyncTextButton(
                    title = StringResource("Sign in with Google"),
                    icon = ImageResource(Drawables.ic_google),
                    action = {}
                ),
                AsyncTextButton(
                    title = StringResource("Sign in with Stanford"),
                    icon = ImageResource(Icons.Default.School),
                    action = {}
                )
            )
        ),
    )

    @Test
    fun `AccountLoginLayout filled screenshot`() {
        screenshot {
            baseLayout.Content(modifier = Modifier.fillMaxSize())
        }
    }

    @Test
    fun `AccountLoginLayout empty no alternative sign in screenshot`() {
        screenshot {
            val newLayout = baseLayout.copy(
                userIdInput = baseLayout.userIdInput.copy(value = ""),
                passwordInput = baseLayout.passwordInput.copy(value = ""),
                alternativeSignIn = null,
            )
            newLayout.Content(modifier = Modifier.fillMaxSize())
        }
    }
}
