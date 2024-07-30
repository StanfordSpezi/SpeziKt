package edu.stanford.bdh.engagehf.health.heartrate

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.bdh.engagehf.health.components.WeightHeader
import edu.stanford.bdh.engagehf.health.weight.Action
import edu.stanford.bdh.engagehf.health.weight.WeightChart
import edu.stanford.bdh.engagehf.health.weight.WeightUiState
import edu.stanford.spezi.core.design.theme.Colors.primary
import edu.stanford.spezi.core.design.theme.TextStyles

@Composable
fun HeartRatePage() {
    val viewModel = hiltViewModel<HeartRateViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    HeartRatePage(
        uiState = uiState,
        onAction = viewModel::onAction
    )
}

@Composable
fun HeartRatePage(
    uiState: WeightUiState,
    onAction: (Action) -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        when (uiState) {
            is WeightUiState.Error -> Text(
                text = uiState.message,
                style = TextStyles.headlineMedium
            )

            WeightUiState.Loading -> CircularProgressIndicator(color = primary)
            is WeightUiState.Success -> {
                WeightHeader(uiState.data, onAction)
                WeightChart(uiState.data)
            }
        }
    }
}
