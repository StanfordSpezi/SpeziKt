package edu.stanford.bdh.heartbeat.app.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.bdh.heartbeat.app.account.LoginPage
import edu.stanford.bdh.heartbeat.app.home.HomeViewModel
import edu.stanford.bdh.heartbeat.app.survey.SurveyViewModel
import edu.stanford.spezi.modules.design.component.CenteredBoxContent
import edu.stanford.spezi.modules.design.component.Screen
import edu.stanford.spezi.modules.design.component.VerticalSpacer
import edu.stanford.spezi.ui.Button
import edu.stanford.spezi.ui.Colors
import edu.stanford.spezi.ui.Spacings
import edu.stanford.spezi.ui.SpeziTheme
import edu.stanford.spezi.ui.TextStyles
import edu.stanford.spezi.ui.ThemePreviews

@Composable
fun MainPage() {
    val viewModel = hiltViewModel<MainViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    MainPage(uiState = uiState, onAction = viewModel::onAction)
}

@Composable
private fun MainPage(
    uiState: MainUiState,
    onAction: (MainAction) -> Unit,
) {
    when (uiState) {
        MainUiState.Loading -> CircularProgressIndicator()

        MainUiState.Unauthenticated -> LoginPage()
        is MainUiState.Authenticated.RequiresEmailVerification -> EmailVerification(
            uiState = uiState,
            onAction = onAction
        )

        MainUiState.HomePage -> Screen<HomeViewModel>()
        MainUiState.Authenticated.Survey.LoadingFailed -> OnboardingLoadingError(onAction = onAction)
        is MainUiState.Authenticated.Survey.Content -> {
            Screen<SurveyViewModel, SurveyViewModel.Factory>(
                creationCallback = { factory -> factory.create(uiState) },
                key = uiState.assessmentStep.displayStatus.surveyToken,
            )
        }
    }
}

@Composable
fun OnboardingLoadingError(onAction: (MainAction) -> Unit) {
    CenteredBoxContent {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(Spacings.large),
        ) {
            Text(
                text = "Oops",
                style = TextStyles.headlineLarge,
                textAlign = TextAlign.Center,
            )
            VerticalSpacer(height = Spacings.medium)
            Text(
                text = "An error occurred while loading your onboarding questionnaire",
                textAlign = TextAlign.Center,
            )
            VerticalSpacer(height = Spacings.medium)
            Button(
                onClick = {
                    onAction(MainAction.ReloadOnboarding)
                },
            ) {
                Text("Try again")
            }
            SignOutButton(onClick = { onAction(MainAction.SignOut) })
        }
    }
}

@Composable
private fun EmailVerification(
    uiState: MainUiState.Authenticated.RequiresEmailVerification,
    onAction: (MainAction) -> Unit,
) {
    if (uiState.showSignoutDialog) {
        AlertDialog(
            onDismissRequest = {
                onAction(MainAction.ShowSignOutDialog(false))
            },
            title = {
                Text("Sign Out")
            },
            text = {
                Text("Do you really want to sign out?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onAction(MainAction.SignOut)
                    }
                ) {
                    Text("Sign Out")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onAction(MainAction.ShowSignOutDialog(false))
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(Spacings.large),
    ) {
        Text(
            text = "Welcome",
            style = TextStyles.headlineLarge,
            textAlign = TextAlign.Center,
        )
        VerticalSpacer(height = Spacings.medium)
        Text(
            text = "Check your inbox and verify your email by clicking the provided link.",
        )
        VerticalSpacer(height = Spacings.medium)

        @Suppress("MaxLineLength")
        Text(text = "If you haven't received the email, you can resend it. After confirming, please press \"Reload\" to update your status.")
        VerticalSpacer(height = Spacings.medium)

        Button(
            onClick = {
                onAction(MainAction.ResendVerificationEmail)
            },
        ) {
            Text("Resend verification email")
        }

        TextButton(
            onClick = { onAction(MainAction.ReloadUser) }
        ) {
            Text("Reload")
        }

        Spacer(modifier = Modifier.weight(1f))
        SignOutButton(onClick = { onAction(MainAction.SignOut) })
    }
}

@Composable
private fun SignOutButton(
    onClick: () -> Unit,
) {
    TextButton(onClick = onClick) {
        Text("Sign Out", color = Colors.error)
    }
}

private class MainUiStatePreviewParameterProvider : PreviewParameterProvider<MainUiState> {
    override val values: Sequence<MainUiState>
        get() = sequenceOf(
            MainUiState.Loading,
            MainUiState.Authenticated.Survey.LoadingFailed,
            MainUiState.Authenticated.RequiresEmailVerification(false),
            MainUiState.Authenticated.RequiresEmailVerification(true)
        )
}

@ThemePreviews
@Composable
private fun Previews(@PreviewParameter(MainUiStatePreviewParameterProvider::class) state: MainUiState) {
    SpeziTheme {
        MainPage(
            uiState = state,
            onAction = {}
        )
    }
}

@ThemePreviews
@Composable
private fun OnboardingLoadingFailed() {
    SpeziTheme {
        EmailVerification(uiState = MainUiState.Authenticated.RequiresEmailVerification(false)) { }
    }
}
