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
import androidx.compose.ui.res.stringResource
import edu.stanford.bdh.engagehf.R
import edu.stanford.bdh.engagehf.health.TimeRange
import edu.stanford.spezi.spezi.ui.helpers.theme.SpeziTheme

@Composable
fun TimeRangeDropdown(
    isSelectedTimeRangeDropdownExpanded: Boolean,
    selectedTimeRange: TimeRange,
    onToggleExpanded: (Boolean) -> Unit,
    updateTimeRange: (TimeRange) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .wrapContentSize(Alignment.TopStart)
    ) {
        TextButton(onClick = {
            onToggleExpanded(true)
        }) {
            Text(
                text = when (selectedTimeRange) {
                    TimeRange.DAILY -> stringResource(R.string.time_range_daily)
                    TimeRange.WEEKLY -> stringResource(R.string.time_range_weekly)
                    TimeRange.MONTHLY -> stringResource(R.string.time_range_monthly)
                }
            )
            Icon(Icons.Default.ArrowDropDown, contentDescription = "ArrowDropDown")
        }
        DropdownMenu(expanded = isSelectedTimeRangeDropdownExpanded,
            onDismissRequest = {
                onToggleExpanded(false)
            }) {
            TimeRange.entries.forEach { timeRange ->
                val isSelected = selectedTimeRange == timeRange
                DropdownMenuItem(
                    text = { Text(timeRange.name) },
                    onClick = {
                        onToggleExpanded(false)
                        updateTimeRange(timeRange)
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
            isSelectedTimeRangeDropdownExpanded = true,
            selectedTimeRange = TimeRange.DAILY,
            onToggleExpanded = {},
            updateTimeRange = {}
        )
    }
}
