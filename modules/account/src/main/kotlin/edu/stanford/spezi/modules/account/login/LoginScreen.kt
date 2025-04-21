@file:Suppress("FunctionName", "UnusedPrivateMember")

package edu.stanford.spezi.modules.account.login

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.spezi.modules.account.R
import edu.stanford.spezi.modules.account.register.FieldState
import edu.stanford.spezi.modules.account.register.IconLeadingContent
import edu.stanford.spezi.modules.design.component.AsyncTextButton
import edu.stanford.spezi.modules.design.component.validated.outlinedtextfield.ValidatedOutlinedTextField
import edu.stanford.spezi.ui.Colors
import edu.stanford.spezi.ui.Spacings
import edu.stanford.spezi.ui.SpeziTheme
import edu.stanford.spezi.ui.TextStyles.bodyLarge
import edu.stanford.spezi.ui.TextStyles.titleLarge
import edu.stanford.spezi.ui.testIdentifier
import edu.stanford.spezi.modules.design.R as DesignR

@Composable
fun LoginScreen() {
    val viewModel = hiltViewModel<LoginViewModel>()
    val uiState by viewModel.uiState.collectAsState()

    LoginScreen(
        uiState = uiState, onAction = viewModel::onAction
    )
}

@Composable
internal fun LoginScreen(
    uiState: UiState,
    onAction: (Action) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .testIdentifier(LoginScreenTestIdentifier.ROOT)
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(Spacings.medium)
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
        Text(
            text = stringResource(R.string.account_your_account), style = titleLarge
        )
        Spacer(modifier = Modifier.height(Spacings.large))
        val annotatedText = buildAnnotatedString {
            append(stringResource(R.string.account_login_screen_description))
            pushStringAnnotation(
                tag = "EMAIL",
                annotation = stringResource(R.string.account_engage_support_email)
            )
            withStyle(
                style = SpanStyle(
                    color = Colors.primary
                )
            ) {
                append(stringResource(R.string.account_engage_support_email))
            }
            pop()
        }
        ClickableText(
            text = annotatedText,
            style = bodyLarge.copy(color = Colors.onBackground),
            onClick = { offset ->
                annotatedText.getStringAnnotations(
                    tag = "EMAIL",
                    start = offset,
                    end = offset
                ).firstOrNull()?.let {
                    onAction(Action.EmailClicked(it.item))
                }
            }
        )
        Spacer(modifier = Modifier.height(Spacings.large))
        IconLeadingContent(
            icon = Icons.Outlined.Email,
            content = {
                ValidatedOutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.email.value,
                    errorText = uiState.email.error,
                    onValueChange = { email ->
                        onAction(Action.TextFieldUpdate(email, TextFieldType.EMAIL))
                    },
                    labelText = "E-Mail Address",
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
                )
            })
        Spacer(modifier = Modifier.height(Spacings.small))
        IconLeadingContent(
            icon = Icons.Outlined.Lock,
            content = {
                ValidatedOutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.password.value,
                    errorText = uiState.password.error,
                    onValueChange = {
                        onAction(Action.TextFieldUpdate(it, TextFieldType.PASSWORD))
                    },
                    labelText = stringResource(R.string.account_password),
                    visualTransformation = if (uiState.passwordVisibility) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        onAction(Action.Async.PasswordSignIn)
                    }),
                    trailingIcon = {
                        IconButton(onClick = { onAction(Action.TogglePasswordVisibility) }) {
                            val iconId = if (uiState.passwordVisibility) {
                                DesignR.drawable.ic_visibility
                            } else {
                                DesignR.drawable.ic_visibility_off
                            }
                            Icon(
                                painter = painterResource(id = iconId),
                                contentDescription = if (uiState.passwordVisibility) {
                                    stringResource(
                                        R.string.account_hide_password
                                    )
                                } else {
                                    stringResource(R.string.account_show_password)
                                }
                            )
                        }
                    }
                )
            })
        val forgotPasswordAction = Action.Async.ForgotPassword
        AsyncTextButton(
            text = stringResource(R.string.account_forgot_password),
            isLoading = uiState.pendingActions.contains(forgotPasswordAction),
            containerColor = Colors.transparent,
            contentPadding = PaddingValues(0.dp),
            textColor = ButtonDefaults.buttonColors().containerColor,
            onClick = {
                onAction(forgotPasswordAction)
            },
            modifier = Modifier.align(Alignment.End)
        )
        Spacer(modifier = Modifier.height(Spacings.medium))
        val passwordSignIn = Action.Async.PasswordSignIn
        AsyncTextButton(
            isLoading = uiState.pendingActions.contains(passwordSignIn),
            text = stringResource(R.string.account_login),
            onClick = { onAction(passwordSignIn) },
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.isPasswordSignInEnabled
        )

        Spacer(modifier = Modifier.height(Spacings.medium))
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.account_no_account_yet))
            TextButton(
                onClick = {
                    onAction(Action.NavigateToRegister)
                },
            ) {
                Text(stringResource(R.string.account_signup))
            }
        }
        Spacer(modifier = Modifier.height(Spacings.medium))
//        TextDivider(stringResource(R.string.account_or))
//        VerticalSpacer()
//        val googleSignInOrSignUp = Action.Async.GoogleSignInOrSignUp
//        SignInWithGoogleButton(
//            isLoading = uiState.pendingActions.contains(googleSignInOrSignUp),
//            onButtonClick = { onAction(googleSignInOrSignUp) },
//        )
    }
}

@Preview
@Composable
private fun LoginScreenPreview(
    @PreviewParameter(LoginScreenPreviewProvider::class) uiState: UiState,
) {
    SpeziTheme {
        LoginScreen(uiState = uiState, onAction = { })
    }
}

private class LoginScreenPreviewProvider : PreviewParameterProvider<UiState> {
    override val values: Sequence<UiState> = sequenceOf(
        UiState(
            email = FieldState(""),
            password = FieldState(""),
            passwordVisibility = false,
        ), UiState(
            email = FieldState("test@test.de"),
            password = FieldState("password"),
            passwordVisibility = true,
        )
    )
}

enum class LoginScreenTestIdentifier {
    ROOT,
}
