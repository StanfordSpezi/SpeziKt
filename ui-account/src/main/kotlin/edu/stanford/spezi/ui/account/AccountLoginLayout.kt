package edu.stanford.spezi.ui.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import edu.stanford.spezi.ui.AsyncTextButton
import edu.stanford.spezi.ui.CloseButton
import edu.stanford.spezi.ui.ComposableContent
import edu.stanford.spezi.ui.ImageResource
import edu.stanford.spezi.ui.SpeziInputField
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.theme.Spacings
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.TextStyles
import edu.stanford.spezi.ui.theme.ThemePreviews
import edu.stanford.spezi.ui.theme.bold

/**
 * Composable content rendering the login screen
 *
 * @param title title of the screen
 * @param description description of the screen
 * @param userIdInput input field for the user id
 * @param passwordInput input field for the password
 * @param forgotPasswordLink link to the forgot password screen
 * @param signUpLink link to the sign up screen
 * @param loginButton button to login
 * @param alternativeSignIn alternative sign in options
 * @param onClose action to be performed when the close button is clicked
 */
data class AccountLoginLayout(
    val title: StringResource,
    val description: StringResource,
    val userIdInput: SpeziInputField,
    val passwordInput: SpeziInputField,
    val forgotPasswordLink: ForgotPasswordLink?,
    val signUpLink: SignUpLink,
    val loginButton: AsyncTextButton,
    val alternativeSignIn: AlternativeSignIn?,
    val onClose: () -> Unit,
) : ComposableContent {
    @Composable
    override fun Content(modifier: Modifier) {
        Column(
            modifier = modifier
                .padding(Spacings.medium)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Spacings.medium),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterStart,
            ) {
                CloseButton(onClick = onClose)
            }

            Text(
                text = title.text(),
                style = TextStyles.headlineLarge.bold(),
            )

            Text(
                text = description.text(),
                textAlign = TextAlign.Center,
                style = TextStyles.bodyMedium,
            )

            userIdInput.Content()

            Column {
                passwordInput.Content()
                forgotPasswordLink?.let {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = Spacings.small),
                        contentAlignment = Alignment.CenterEnd,
                    ) {
                        it.Content()
                    }
                }
            }

            loginButton.Content(modifier = Modifier.fillMaxWidth())
            signUpLink.Content()

            alternativeSignIn?.Content()
        }
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    val layout = AccountLoginLayout(
        title = StringResource("Your account"),
        description = StringResource("Please login to your account. Or create a new one if you don't have one already."),
        userIdInput = SpeziInputField(
            value = "email@stanford.edu",
            placeholder = StringResource("E-Mail Address"),
            onValueChanged = {}
        ),
        passwordInput = SpeziInputField(
            value = "123456",
            hideContent = true,
            placeholder = StringResource("Password"),
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
        ),
        onClose = {},
    )

    SpeziTheme {
        layout.Content()
    }
}
