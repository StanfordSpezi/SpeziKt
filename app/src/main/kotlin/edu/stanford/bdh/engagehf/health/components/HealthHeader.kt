package edu.stanford.bdh.engagehf.health.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import edu.stanford.bdh.engagehf.R
import edu.stanford.bdh.engagehf.health.TimeRange
import edu.stanford.spezi.core.design.theme.Colors.onPrimary
import edu.stanford.spezi.core.design.theme.Colors.primary
import edu.stanford.spezi.core.design.theme.Sizes
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.TextStyles

data class HealthHeaderData(
    val formattedValue: String,
    val formattedDate: String,
    val isSelectedTimeRangeDropdownExpanded: Boolean,
    val selectedTimeRange: TimeRange = TimeRange.DAILY,
)

@Composable
fun HealthHeader(
    headerData: HealthHeaderData,
    onTimeRangeDropdownAction: (Boolean) -> Unit,
    updateTimeRange: (TimeRange) -> Unit,
    onInfoAction: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = Spacings.medium)
    ) {
        Column {
            Text(
                text = headerData.formattedValue,
                style = TextStyles.headlineLarge.copy(color = primary),
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Text(
                text = headerData.formattedDate,
                style = TextStyles.bodyMedium
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        TimeRangeDropdown(
            isSelectedTimeRangeDropdownExpanded = headerData.isSelectedTimeRangeDropdownExpanded,
            selectedTimeRange = headerData.selectedTimeRange,
            onToggleExpanded = onTimeRangeDropdownAction,
            updateTimeRange = updateTimeRange
        )
        IconButton(
            modifier = Modifier.size(Sizes.Icon.large),
            onClick = onInfoAction
        ) {
            Icon(
                painter = painterResource(id = edu.stanford.spezi.core.design.R.drawable.ic_info),
                contentDescription = stringResource(R.string.info_icon_content_description),
                modifier = Modifier
                    .size(Sizes.Icon.medium)
                    .background(primary, shape = CircleShape)
                    .shadow(Spacings.small, CircleShape)
                    .padding(Spacings.small),
                tint = onPrimary
            )
        }
    }
}
