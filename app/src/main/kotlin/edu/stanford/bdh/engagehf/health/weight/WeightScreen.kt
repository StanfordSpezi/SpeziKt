package edu.stanford.bdh.engagehf.health.weight

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.bdh.engagehf.health.HealthPage

@Composable
fun WeightPage() {
    val viewModel = hiltViewModel<WeightViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    HealthPage(uiState = uiState, onAction = viewModel::onAction)
}
