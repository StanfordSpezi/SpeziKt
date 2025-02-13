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
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.bdh.heartbeat.app.account.LoginPage
import edu.stanford.bdh.heartbeat.app.home.HomePage
import edu.stanford.bdh.heartbeat.app.onboarding.OnboardingPage
import edu.stanford.spezi.core.design.component.Button
import edu.stanford.spezi.core.design.component.VerticalSpacer
import edu.stanford.spezi.core.design.theme.Colors
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.TextStyles

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
    if (uiState.showsSignOutDialog) {
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

    if (uiState.accountInfo == null) {
        LoginPage()
    } else if (!uiState.accountInfo.isEmailVerified) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(Spacings.large),
        ) {
            Text("Email has been sent.", style = TextStyles.headlineLarge)
            VerticalSpacer(height = Spacings.medium)
            Text("Check your inbox and verify your email by clicking the provided link.")
            VerticalSpacer(height = Spacings.medium)
            TextButton(
                onClick = {
                    onAction(MainAction.Reload)
                }
            ) {
                Text("Reload")
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    onAction(MainAction.ResendVerificationEmail)
                },
            ) {
                Text("Resend verification email")
            }

            TextButton(
                onClick = {
                    onAction(MainAction.ShowSignOutDialog(true))
                }
            ) {
                Text("Sign Out", color = Colors.error)
            }
        }
    } else if (uiState.isLoadingOnboarding) {
        CircularProgressIndicator()
    } else if (uiState.hasFinishedOnboarding) {
        HomePage()
    } else {
        OnboardingPage()
    }
}
