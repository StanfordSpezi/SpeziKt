package edu.stanford.bdh.engagehf.health.heartrate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.bdh.engagehf.health.weight.HealthPage

@Composable
fun HeartRatePage() {
    val viewModel = hiltViewModel<HeartRateViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    HealthPage(uiState = uiState, onAction = viewModel::onAction)
}
