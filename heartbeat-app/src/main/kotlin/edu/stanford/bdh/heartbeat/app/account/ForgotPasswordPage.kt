package edu.stanford.bdh.heartbeat.app.account

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.spezi.ui.Colors

@Composable
fun ForgotPasswordPage(onDismissRequest: () -> Unit) {
    val viewModel = hiltViewModel<ForgotPasswordViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    ForgotPasswordPage(uiState, viewModel::onAction, onDismissRequest)
}

@Composable
private fun ForgotPasswordPage(
    uiState: ForgotPasswordUiState,
    onAction: (ForgotPasswordAction) -> Unit,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text("Forgot Password")
        },
        text = {
            if (uiState.isSuccess) {
                Text("Sent out a link to reset the password.")
            } else {
                Column {
                    Text(
                        "Please enter your email address of your account. " +
                            "A password reset email will be sent to the linked email address."
                    )
                    OutlinedTextField(
                        label = {
                            Text("E-Mail Address")
                        },
                        value = uiState.username,
                        onValueChange = {
                            onAction(ForgotPasswordAction.UsernameChanged(it))
                        },
                    )
                    uiState.errorMessage?.let {
                        Text(it, color = Colors.error)
                    }
                }
            }
        },
        confirmButton = {
            if (!uiState.isSuccess) {
                TextButton(
                    onClick = {
                        onAction(ForgotPasswordAction.Submit)
                    }
                ) {
                    Text("Reset Password")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(if (uiState.isSuccess) "Done" else "Close")
            }
        }
    )
}
