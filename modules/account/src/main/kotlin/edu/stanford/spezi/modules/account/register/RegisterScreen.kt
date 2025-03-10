package edu.stanford.spezi.modules.account.register

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.spezi.modules.account.R
import edu.stanford.spezi.modules.design.component.VerticalSpacer
import edu.stanford.spezi.modules.design.component.validated.outlinedtextfield.ValidatedOutlinedTextField
import edu.stanford.spezi.ui.Colors.primary
import edu.stanford.spezi.ui.Sizes
import edu.stanford.spezi.ui.Spacings
import edu.stanford.spezi.ui.SpeziTheme
import edu.stanford.spezi.ui.TextStyles.titleLarge
import edu.stanford.spezi.ui.TextStyles.titleSmall
import edu.stanford.spezi.ui.testing.testIdentifier

@Composable
fun RegisterScreen(
    email: String,
    password: String,
) {
    val viewModel = hiltViewModel<RegisterViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(key1 = email, key2 = password) {
        viewModel.onAction(Action.TextFieldUpdate(email, TextFieldType.EMAIL))
        viewModel.onAction(Action.TextFieldUpdate(password, TextFieldType.PASSWORD))
    }

    RegisterScreen(
        uiState = uiState,
        onAction = viewModel::onAction
    )
}

@Composable
fun RegisterScreen(
    uiState: RegisterUiState,
    onAction: (Action) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        modifier = Modifier
            .testIdentifier(RegisterScreenTestIdentifier.ROOT)
            .fillMaxSize()
            .padding(Spacings.medium)
            .imePadding()
            .verticalScroll(rememberScrollState())
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        keyboardController?.hide()
                    }
                )
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Create a new Account Icon",
            tint = primary,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(Sizes.Icon.large)
        )
        Text(
            text = stringResource(R.string.create_a_new_account),
            style = titleLarge,
        )
        VerticalSpacer()
        Text(
            text = stringResource(R.string.please_fill_out_the_details_below_to_create_your_new_account),
            style = titleSmall,
        )
        VerticalSpacer(height = Spacings.large)
        IconLeadingContent(
            icon = Icons.Outlined.Email,
            content = {
                ValidatedOutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.email.value,
                    onValueChange = {
                        onAction(Action.TextFieldUpdate(it, TextFieldType.EMAIL))
                    },
                    labelText = stringResource(R.string.e_mail_address),
                    errorText = uiState.email.error,
                )
            })
        VerticalSpacer(height = Spacings.small)
        IconLeadingContent(
            icon = Icons.Outlined.Lock,
            content = {
                ValidatedOutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.password.value,
                    onValueChange = {
                        onAction(Action.TextFieldUpdate(it, TextFieldType.PASSWORD))
                    },
                    labelText = stringResource(R.string.password),
                    errorText = uiState.password.error,
                    trailingIcon = {
                        IconButton(onClick = { onAction(Action.TogglePasswordVisibility) }) {
                            val iconId: Int = if (uiState.isPasswordVisible) {
                                edu.stanford.spezi.modules.design.R.drawable.ic_visibility
                            } else {
                                edu.stanford.spezi.modules.design.R.drawable.ic_visibility_off
                            }
                            Icon(
                                painter = painterResource(id = iconId),
                                contentDescription = if (uiState.isPasswordVisible) {
                                    stringResource(
                                        R.string.hide_password
                                    )
                                } else {
                                    stringResource(R.string.show_password)
                                }
                            )
                        }
                    },
                    visualTransformation = if (uiState.isPasswordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                )
            })
        Spacer(modifier = Modifier.height(Spacings.large))
        Button(
            onClick = {
                onAction(Action.OnRegisterPressed)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.isRegisterButtonEnabled
        ) {
            Text(stringResource(R.string.signup))
        }
    }
}

@Preview
@Composable
private fun RegisterScreenPreview(
    @PreviewParameter(RegisterScreenProvider::class) uiState: RegisterUiState,
) {
    SpeziTheme {
        RegisterScreen(
            uiState = uiState,
            onAction = { }
        )
    }
}

private class RegisterScreenProvider : PreviewParameterProvider<RegisterUiState> {
    override val values: Sequence<RegisterUiState> = sequenceOf(
        RegisterUiState(
            email = FieldState("", null),
            password = FieldState("", null),
            isFormValid = false
        ),
        RegisterUiState(
            email = FieldState("test@test.de", null),
            password = FieldState("password", null),
            isFormValid = true
        ),
        RegisterUiState(
            email = FieldState("test", "Invalid email"),
            password = FieldState("pass", "Password to short"),
            isFormValid = false,
        ),
    )
}

enum class RegisterScreenTestIdentifier {
    ROOT,
}
