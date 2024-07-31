package edu.stanford.bdh.engagehf.health.bloodpressure

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.bdh.engagehf.health.weight.HealthPage

@Composable
fun BloodPressurePage() {
    val viewModel = hiltViewModel<BloodPressureViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    HealthPage(uiState = uiState, onAction = viewModel::onAction)
}
