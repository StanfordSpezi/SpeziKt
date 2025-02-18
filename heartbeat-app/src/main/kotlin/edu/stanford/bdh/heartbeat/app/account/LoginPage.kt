package edu.stanford.bdh.heartbeat.app.account

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.TextStyles

@Composable
fun LoginPage() {
    val viewModel = hiltViewModel<LoginViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    LoginPage(uiState, viewModel::onAction)
}

@Composable
private fun LoginPage(
    uiState: LoginUiState,
    onAction: (LoginAction) -> Unit,
) {
    if (uiState.showsRegistrationDialog) {
        RegisterPage {
            onAction(LoginAction.ShowRegistrationDialog(false))
        }
    }

    if (uiState.showsForgotPasswordDialog) {
        ForgotPasswordPage {
            onAction(LoginAction.ShowForgotPasswordDialog(false))
        }
    }

    Column(modifier = Modifier.fillMaxWidth().padding(Spacings.medium)) {
        Text(
            "Your Account",
            style = TextStyles.headlineLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.size(Spacings.medium))

        Text(
            "You may login to your existing account. Or create a new one if you don't have one already.",
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Spacer(modifier = Modifier.size(Spacings.medium))

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.username,
            onValueChange = {
                onAction(LoginAction.UsernameChanged(it))
            },
            label = {
                Text("E-Mail Address")
            }
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.password,
            onValueChange = {
                onAction(LoginAction.PasswordChanged(it))
            },
            visualTransformation = PasswordVisualTransformation(),
            label = {
                Text("Password")
            }
        )

        TextButton(
            onClick = {
                onAction(LoginAction.ShowForgotPasswordDialog(true))
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Forgot Password?")
        }

        Button(
            onClick = {
                onAction(LoginAction.Submit)
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Login")
        }

        TextButton(
            onClick = {
                onAction(LoginAction.ShowRegistrationDialog(true))
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Don't have an account yet? Sign up!")
        }
    }
}
