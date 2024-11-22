package edu.stanford.spezi.module.account.firebase.account.views

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.design.theme.ThemePreviews
import edu.stanford.spezi.core.design.views.validation.Validate
import edu.stanford.spezi.core.design.views.validation.ValidationRule
import edu.stanford.spezi.core.design.views.validation.nonEmpty
import edu.stanford.spezi.core.design.views.validation.state.ReceiveValidation
import edu.stanford.spezi.core.design.views.validation.state.ValidationContext
import edu.stanford.spezi.core.design.views.validation.views.VerifiableTextField
import edu.stanford.spezi.module.account.account.compositionLocal.PasswordFieldType

@Composable
fun FirebaseSecurityAlert(
    onCloseRequest: () -> Unit,
) {
    val validation = remember { mutableStateOf(ValidationContext()) }
    val password = remember { mutableStateOf("") }

    fun onDismissRequest() {
        password.value = ""
        onCloseRequest()
    }

    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        confirmButton = {
            TextButton(
                onClick = {
                    if (validation.value.validateHierarchy()) {

                    } else {

                    }
                }
            ) {
                Text(StringResource("Login").text())
            }
        },
        modifier = Modifier,
        dismissButton = {
            TextButton(onClick = {

            }) {
                Text(StringResource("Cancel").text())
            }
        },
        title = {
            Text(StringResource("Authentication Required").text())
        },
        text = {
            ReceiveValidation(validation) {
                Validate(password.value, ValidationRule.nonEmpty) {
                    VerifiableTextField(password, disableAutocorrection = true) {
                        Text(PasswordFieldType.PASSWORD.stringResource.text())
                    }
                }
            }
        },
    )
}

@ThemePreviews
@Composable
private fun FirebaseSecurityAlertPreview() {
    FirebaseSecurityAlert {
        println("Should close")
    }
}
