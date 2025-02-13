package edu.stanford.bdh.heartbeat.app.account

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.spezi.core.design.theme.Colors

@Composable
fun RegisterPage(onDismissRequest: () -> Unit) {
    val viewModel = hiltViewModel<RegisterViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    RegisterPage(uiState, viewModel::onAction, onDismissRequest)
}

@Composable
private fun RegisterPage(
    uiState: RegisterUiState,
    onAction: (RegisterAction) -> Unit,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text("Create new account")
        },
        text = {
            Column {
                Text(
                    "Please fill out the details below to create your new account."
                )

                OutlinedTextField(
                    label = {
                        Text("E-Mail Address")
                    },
                    value = uiState.username,
                    onValueChange = {
                        onAction(RegisterAction.UsernameChanged(it))
                    },
                )

                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = {
                        onAction(RegisterAction.PasswordChanged(it))
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    label = {
                        Text("Password")
                    }
                )

                uiState.errorMessage?.let {
                    Text(it, color = Colors.error)
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onAction(RegisterAction.Submit)
                }
            ) {
                Text("Signup")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Close")
            }
        }
    )
}
