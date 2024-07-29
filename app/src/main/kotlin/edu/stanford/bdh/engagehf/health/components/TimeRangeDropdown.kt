package edu.stanford.bdh.engagehf.health.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import edu.stanford.bdh.engagehf.health.TimeRange
import edu.stanford.bdh.engagehf.health.weight.WeightUiData
import edu.stanford.bdh.engagehf.health.weight.WeightViewModel
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.ThemePreviews

@Composable
fun TimeRangeDropdown(
    uiState: WeightUiData,
    onAction: (WeightViewModel.Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .wrapContentSize(Alignment.TopStart)
    ) {
        TextButton(onClick = {
            onAction(WeightViewModel.Action.ToggleTimeRangeDropdown(true))
        }) {
            Text(
                text = when (uiState.selectedTimeRange) {
                    TimeRange.DAILY -> "Daily"
                    TimeRange.WEEKLY -> "Weekly"
                    TimeRange.MONTHLY -> "Monthly"
                }
            )
            Icon(Icons.Default.ArrowDropDown, contentDescription = "ArrowDropDown")
        }
        DropdownMenu(expanded = uiState.isSelectedTimeRangeDropdownExpanded, onDismissRequest = {
            onAction(WeightViewModel.Action.ToggleTimeRangeDropdown(false))
        }) {
            TimeRange.entries.forEach { timeRange ->
                val isSelected = uiState.selectedTimeRange == timeRange
                DropdownMenuItem(
                    text = { Text(timeRange.name) },
                    onClick = {
                        onAction(WeightViewModel.Action.ToggleTimeRangeDropdown(false))
                        onAction(WeightViewModel.Action.UpdateTimeRange(timeRange))
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Check,
                            contentDescription = null,
                            tint = if (isSelected) Color.Black else Color.Transparent
                        )
                    }
                )
            }
        }
    }
}

@ThemePreviews
@Composable
fun MenuSamplePreview() {
    SpeziTheme(isPreview = true) {
        TimeRangeDropdown(
            modifier = Modifier.fillMaxWidth(),
            uiState = WeightUiData(
                isSelectedTimeRangeDropdownExpanded = true,
                weights = emptyList(),
                newestWeight = null
            ),
            onAction = {}
        )
    }
}
