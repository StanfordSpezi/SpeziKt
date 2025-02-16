package edu.stanford.bdh.heartbeat.app.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.ThemePreviews

@Composable
fun OnboardingPage() {
    val viewModel = hiltViewModel<OnboardingViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    OnboardingPage(uiState, viewModel::onAction)
}

@Composable
private fun OnboardingPage(
    uiState: OnboardingUiState,
    onAction: (OnboardingAction) -> Unit,
) {
    LaunchedEffect(Unit) {
        onAction(OnboardingAction.Reload)
    }

    Column {
        Text("${uiState.step?.question?.title1}")
    }
}

@ThemePreviews
@Composable
private fun OnboardingLoadingFailed() {
    SpeziTheme(isPreview = true) {
        Text("State")
    }
}