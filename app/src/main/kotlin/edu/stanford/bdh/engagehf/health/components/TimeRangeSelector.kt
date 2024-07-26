package edu.stanford.bdh.engagehf.health.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import edu.stanford.bdh.engagehf.health.TimeRange
import edu.stanford.spezi.core.design.theme.Colors.onPrimary
import edu.stanford.spezi.core.design.theme.Colors.primary
import edu.stanford.spezi.core.design.theme.ThemePreviews

@Composable
fun TimeRangeSelector(
    selectedTimeRange: TimeRange,
    onTimeRangeSelected: (TimeRange) -> Unit,
) {
    Row {
        TimeRange.entries.forEach { range ->
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = when (range) {
                    TimeRange.DAILY -> "Daily"
                    TimeRange.WEEKLY -> "Weekly"
                    TimeRange.MONTHLY -> "Monthly"
                },
                modifier = Modifier
                    .clickable { onTimeRangeSelected(range) }
                    .background(
                        if (selectedTimeRange == range) primary else Color.Transparent,
                        shape = CircleShape
                    )
                    .padding(8.dp),
                color = if (selectedTimeRange == range) onPrimary else primary
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@ThemePreviews
@Composable
fun TimeRangeSelectorPreview() {
    TimeRangeSelector(TimeRange.DAILY) {}
}
