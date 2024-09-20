package edu.stanford.bdh.engagehf.health.bloodpressure

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import edu.stanford.bdh.engagehf.health.HealthPage
import edu.stanford.bdh.engagehf.health.RecordType
import edu.stanford.bdh.engagehf.health.healthRecordViewModel

@Composable
fun BloodPressurePage() {
    val viewModel = healthRecordViewModel(type = RecordType.BLOOD_PRESSURE)
    val uiState by viewModel.uiState.collectAsState()
    HealthPage(uiState = uiState, onAction = viewModel::onAction)
}
