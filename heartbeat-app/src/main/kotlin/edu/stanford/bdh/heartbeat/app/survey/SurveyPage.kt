package edu.stanford.bdh.heartbeat.app.survey

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.bdh.heartbeat.app.main.MainUiState

@Composable
fun SurveyPage(onboardingState: MainUiState.Authenticated.Survey.Content) {
    val viewModel = hiltViewModel<SurveyViewModel, SurveyViewModel.Factory>(
        creationCallback = { factory -> factory.create(onboardingState) },
        key = onboardingState.onboarding.displayStatus.surveyToken
    )
    val uiState by viewModel.uiState.collectAsState()
    uiState.Body(modifier = Modifier.fillMaxSize())
}
