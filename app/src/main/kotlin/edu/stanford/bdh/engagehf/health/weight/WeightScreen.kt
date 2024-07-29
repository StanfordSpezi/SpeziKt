package edu.stanford.bdh.engagehf.health.weight

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.bdh.engagehf.health.WeightHeader
import edu.stanford.bdh.engagehf.health.WeightList
import edu.stanford.spezi.core.design.component.VerticalSpacer
import edu.stanford.spezi.core.design.theme.Colors
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.TextStyles

@Composable
fun WeightPage() {
    val viewModel = hiltViewModel<WeightViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    WeightPage(uiState = uiState, onAction = viewModel::onAction)
}

@Composable
fun WeightPage(
    uiState: WeightUiState,
    onAction: (WeightViewModel.Action) -> Unit,
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

            WeightUiState.Loading -> CircularProgressIndicator(color = Colors.primary)
            is WeightUiState.Success -> {
                if (uiState.data.weights.isEmpty()) {
                    Text(text = "No weight data available", style = TextStyles.headlineMedium)
                } else {
                    WeightHeader(uiState.data, onAction)
                    WeightChart(uiState = uiState.data)
                    VerticalSpacer()
                    Row(
                        modifier = Modifier.padding(horizontal = Spacings.medium),
                        verticalAlignment = Alignment.Bottom,
                    ) {
                        Text(text = "Weight history", style = TextStyles.headlineMedium)
                    }
                    WeightList(weights = uiState.data.tableWeights, onAction = onAction)
                }
            }
        }
    }
}
