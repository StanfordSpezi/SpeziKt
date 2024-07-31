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
import edu.stanford.bdh.engagehf.health.HealthTable
import edu.stanford.bdh.engagehf.health.components.HealthHeader
import edu.stanford.spezi.core.design.component.VerticalSpacer
import edu.stanford.spezi.core.design.theme.Colors
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.TextStyles

@Composable
fun WeighPage() {
    val viewModel = hiltViewModel<WeightViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    HealthPage(uiState = uiState, onAction = viewModel::onAction)
}

@Composable
fun HealthPage(
    uiState: HealthUiState,
    onAction: (Action) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        when (uiState) {
            is HealthUiState.Error -> Text(
                text = uiState.message,
                style = TextStyles.headlineMedium
            )

            HealthUiState.Loading -> CircularProgressIndicator(color = Colors.primary)
            is HealthUiState.Success -> {
                if (uiState.data.records.isEmpty()) {
                    Text(text = "No weight data available", style = TextStyles.headlineMedium)
                } else {
                    HealthHeader(uiState.data.headerData,
                        onTimeRangeDropdownAction = { onAction(Action.ToggleTimeRangeDropdown(it)) },
                        updateTimeRange = { onAction(Action.UpdateTimeRange(it)) },
                        onInfoAction = { onAction(Action.DescriptionBottomSheet) }
                    )
                    HealthChart(uiState = uiState.data)
                    VerticalSpacer()
                    Row(
                        modifier = Modifier.padding(horizontal = Spacings.medium),
                        verticalAlignment = Alignment.Bottom,
                    ) {
                        Text(text = "Weight history", style = TextStyles.headlineMedium)
                    }
                    HealthTable(weights = uiState.data.tableData, onAction = onAction)
                }
            }
        }
    }
}
