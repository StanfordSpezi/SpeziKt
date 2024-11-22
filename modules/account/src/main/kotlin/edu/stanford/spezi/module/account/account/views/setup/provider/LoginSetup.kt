package edu.stanford.spezi.module.account.account.views.setup.provider

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.stanford.spezi.core.design.component.Button
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.design.theme.Colors
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.TextStyles
import edu.stanford.spezi.core.design.theme.ThemePreviews
import edu.stanford.spezi.core.design.views.validation.Validate
import edu.stanford.spezi.core.design.views.validation.ValidationEngine
import edu.stanford.spezi.core.design.views.validation.ValidationRule
import edu.stanford.spezi.core.design.views.validation.configuration.LocalValidationEngineConfiguration
import edu.stanford.spezi.core.design.views.validation.nonEmpty
import edu.stanford.spezi.core.design.views.validation.state.ReceiveValidation
import edu.stanford.spezi.core.design.views.validation.state.ValidationContext
import edu.stanford.spezi.core.design.views.validation.views.TextFieldType
import edu.stanford.spezi.core.design.views.validation.views.VerifiableTextField
import edu.stanford.spezi.core.design.views.views.model.ViewState
import edu.stanford.spezi.core.design.views.views.views.button.SuspendButton
import edu.stanford.spezi.core.design.views.views.viewstate.ViewStateAlert
import edu.stanford.spezi.module.account.account.compositionLocal.LocalAccount
import edu.stanford.spezi.module.account.account.model.UserIdPasswordCredential
import edu.stanford.spezi.module.account.account.service.configuration.UserIdConfiguration
import edu.stanford.spezi.module.account.account.service.configuration.userIdConfiguration
import java.util.EnumSet

private enum class LoginFocusState {
    USERID, PASSWORD
}

@Composable
internal fun LoginSetup(
    login: suspend (UserIdPasswordCredential) -> Unit,
    passwordReset: (@Composable () -> Unit)?,
    supportsSignup: Boolean,
    presentingSignup: MutableState<Boolean>,
) {
    val focusedField = remember { mutableStateOf<LoginFocusState?>(null) }
    val userId = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val state = remember { mutableStateOf<ViewState>(ViewState.Idle) }
    val validation = remember { mutableStateOf(ValidationContext()) }
    val presentingPasswordReset = remember { mutableStateOf(false) }

    ViewStateAlert(state)

    if (presentingPasswordReset.value && passwordReset != null) {
        passwordReset()
    }

    ReceiveValidation(validation) {
        Column {
            LoginSetupFields(
                userId,
                password,
                passwordReset != null,
                presentingPasswordReset
            )

            SuspendButton(
                state = state,
                onClick = {
                    if (!validation.value.validateHierarchy()) return@SuspendButton
                    focusedField.value = null

                    val credential = UserIdPasswordCredential(userId.value, password.value)
                    login(credential)
                },
                enabled = validation.value.allInputValid,
                modifier = Modifier.padding(top = 8.dp, bottom = 12.dp).fillMaxWidth()
            ) {
                Text(
                    StringResource("UP_LOGIN").text(),
                    Modifier.padding(8.dp),
                    color = Colors.primary,
                )
            }

            if (supportsSignup) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        StringResource("No account yet?").text(),
                        style = TextStyles.bodySmall
                    )
                    Text(" ")
                    Text(
                        StringResource("Sign Up").text(),
                        style = TextStyles.bodySmall,
                        color = Colors.secondary,
                        modifier = Modifier.clickable { presentingSignup.value = true }
                    )
                }
            }
        }
    }
}

@Composable
private fun LoginSetupFields(
    userId: MutableState<String>,
    password: MutableState<String>,
    hasPasswordReset: Boolean,
    presentingPasswordReset: MutableState<Boolean>,
) {
    val account = LocalAccount.current
    val userIdConfiguration = account?.accountService?.configuration?.userIdConfiguration ?: UserIdConfiguration.emailAddress

    val configuration = remember {
        EnumSet.of(
            ValidationEngine.ConfigurationOption
                .HIDE_FAILED_VALIDATION_ON_EMPTY_SUBMIT
        )
    }
    CompositionLocalProvider(LocalValidationEngineConfiguration provides configuration) {
        Column { // swiftlint:disable:this closure_body_length
            Validate(userId.value, ValidationRule.nonEmpty) {
                VerifiableTextField(
                    userIdConfiguration.idType.stringResource.text(),
                    userId,
                    Modifier.padding(bottom = 0.5.dp)
                )
                // TODO: focused
                // TODO: TextContentType
                // TODO: KeyboardType
            }

            Validate(password.value, ValidationRule.nonEmpty) {
                VerifiableTextField(
                    StringResource("UP_PASSWORD").text(),
                    password,
                    type = TextFieldType.SECURE
                )
                // TODO: .focused($focusedField, equals: .userId)
                // TODO: .textContentType(.password)
            }

            if (hasPasswordReset) {
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = { presentingPasswordReset.value = true }) {
                        Text(
                            StringResource("Forgot password?").text(),
                            style = TextStyles.bodySmall,
                            color = Colors.secondary, // iOS: systemGray
                        )
                    }
                }
            }

            if (!hasPasswordReset) {
                Spacer(Modifier.fillMaxWidth().heightIn(max = 10.dp))
            }
            // TODO: .disableFieldAssistants()
            // TODO: .textFieldStyle(.roundedBorder)
            // TODO: .font(.title3)
        }
    }
}

@ThemePreviews
@Composable
private fun LoginSetupPreview() {
    val presentingSignup = remember { mutableStateOf(false) }
    SpeziTheme(isPreview = true) {
        LoginSetup(
            login = { println("Login: '${it.userId}', '${it.password}'") },
            passwordReset = {
                Text("Password reset")
            },
            supportsSignup = true,
            presentingSignup = presentingSignup,
        )
    }
}
