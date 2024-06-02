package edu.stanford.spezi.module.account.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.TextStyles.bodyLarge
import edu.stanford.spezi.core.design.theme.TextStyles.titleLarge
import edu.stanford.spezi.module.account.login.components.SignInWithGoogleButton
import edu.stanford.spezi.module.account.login.components.TextDivider


@Composable
fun LoginScreen(
    isAlreadyRegistered: Boolean,
) {
    val viewModel = hiltViewModel<LoginViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    viewModel.onAction(Action.SetIsAlreadyRegistered(isAlreadyRegistered))

    LoginScreen(
        uiState = uiState,
        onAction = viewModel::onAction
    )
}

@Composable
internal fun LoginScreen(
    uiState: UiState,
    onAction: (Action) -> Unit,
) {

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacings.medium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Your Account",
            style = titleLarge
        )
        Spacer(modifier = Modifier.height(Spacings.large))
        Text(
            text = "The ENGAGE-HF demonstrates the usage of the Firebase Account Module. \n\nYou may login to your existing account or create a new one if you don't have one already.",
            style = bodyLarge,
        )
        Spacer(modifier = Modifier.height(Spacings.large))
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.email,
            onValueChange = { email ->
                onAction(Action.TextFieldUpdate(email, TextFieldType.EMAIL))
            },
            label = { Text("E-Mail Address") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
        )
        Spacer(modifier = Modifier.height(Spacings.small))
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.password,
            onValueChange = {
                onAction(Action.TextFieldUpdate(it, TextFieldType.PASSWORD))
            },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = if (uiState.passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onAction(Action.TogglePasswordVisibility) })
        )
        TextButton(
            onClick = {
                // TODO
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Forgot Password?")
        }
        Spacer(modifier = Modifier.height(Spacings.medium))
        Button(
            onClick = {
                // TODO
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.email.isNotEmpty() && uiState.password.isNotEmpty()
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(Spacings.medium))
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Don't have an Account yet?")
            TextButton(
                enabled = !uiState.isAlreadyRegistered,
                onClick = {
                    onAction(Action.NavigateToRegister(NavigationTarget.REGISTER))
                },

                ) {
                Text("Signup")
            }
        }
        Spacer(modifier = Modifier.height(Spacings.medium))
        TextDivider(text = "or")
        Spacer(modifier = Modifier.height(Spacings.medium))
        SignInWithGoogleButton { onAction(Action.GoogleSignIn(context)) }
    }
}

@Preview
@Composable
private fun LoginScreenPreview(
    @PreviewParameter(LoginScreenPreviewProvider::class) uiState: UiState
) {
    SpeziTheme {
        LoginScreen(
            uiState = uiState,
            onAction = { }
        )
    }
}

private class LoginScreenPreviewProvider : PreviewParameterProvider<UiState> {
    override val values: Sequence<UiState> = sequenceOf(
        UiState(
            email = "",
            password = "",
            passwordVisibility = false,
        ),
        UiState(
            email = "test@test.de",
            password = "password",
            passwordVisibility = true,
            isAlreadyRegistered = true
        )
    )
}
