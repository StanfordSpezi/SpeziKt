package edu.stanford.bdh.engagehf.health.weight

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import edu.stanford.bdh.engagehf.health.HealthPage
import edu.stanford.bdh.engagehf.health.RecordType
import edu.stanford.bdh.engagehf.health.healthRecordViewModel

@Composable
fun WeightPage() {
    val viewModel = healthRecordViewModel(type = RecordType.WEIGHT)
    val uiState by viewModel.uiState.collectAsState()
    HealthPage(uiState = uiState, onAction = viewModel::onAction)
}
