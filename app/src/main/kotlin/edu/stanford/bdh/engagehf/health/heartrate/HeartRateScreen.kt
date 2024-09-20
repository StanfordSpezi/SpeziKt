package edu.stanford.bdh.engagehf.health.heartrate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import edu.stanford.bdh.engagehf.health.HealthPage
import edu.stanford.bdh.engagehf.health.RecordType
import edu.stanford.bdh.engagehf.health.healthRecordViewModel

@Composable
fun HeartRatePage() {
    val viewModel = healthRecordViewModel(type = RecordType.HEART_RATE)
    val uiState by viewModel.uiState.collectAsState()
    HealthPage(uiState = uiState, onAction = viewModel::onAction)
}
