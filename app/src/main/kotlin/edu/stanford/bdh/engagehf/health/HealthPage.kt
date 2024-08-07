package edu.stanford.bdh.engagehf.health

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.units.Mass
import edu.stanford.bdh.engagehf.R
import edu.stanford.bdh.engagehf.health.components.HealthChart
import edu.stanford.bdh.engagehf.health.components.SwipeBox
import edu.stanford.bdh.engagehf.health.components.TimeRangeDropdown
import edu.stanford.spezi.core.design.theme.Colors.onPrimary
import edu.stanford.spezi.core.design.theme.Colors.primary
import edu.stanford.spezi.core.design.theme.Sizes
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.TextStyles
import edu.stanford.spezi.core.design.theme.ThemePreviews
import edu.stanford.spezi.core.utils.ComposableBlock
import edu.stanford.spezi.core.utils.extensions.testIdentifier
import java.time.ZonedDateTime

@Composable
fun HealthPage(
    uiState: HealthUiState,
    onAction: (HealthAction) -> Unit,
) {
    when (uiState) {
        is HealthUiState.Error -> {
            CenteredContent {
                Text(
                    text = uiState.message,
                    style = TextStyles.headlineMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testIdentifier(HealthPageTestIdentifier.ERROR_MESSAGE)
                )
            }
        }

        HealthUiState.Loading -> {
            CenteredContent {
                CircularProgressIndicator(
                    color = primary,
                    modifier = Modifier.testIdentifier(HealthPageTestIdentifier.PROGRESS_INDICATOR)
                )
            }
        }

        is HealthUiState.NoData -> {
            CenteredContent {
                Text(
                    text = uiState.message,
                    textAlign = TextAlign.Center,
                    style = TextStyles.headlineMedium,
                    modifier = Modifier.testIdentifier(HealthPageTestIdentifier.NO_DATA_MESSAGE)
                )
            }
        }

        is HealthUiState.Success -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testIdentifier(HealthPageTestIdentifier.ROOT)
            ) {
                listHeader(uiState = uiState, onAction = onAction)

                item {
                    Text(
                        text = stringResource(id = R.string.health_history),
                        style = TextStyles.headlineSmall,
                        modifier = Modifier.testIdentifier(HealthPageTestIdentifier.HEALTH_HISTORY_TEXT)
                    )
                }
                val data = uiState.data.tableData
                itemsIndexed(data) { index, entry ->
                    SwipeBox(
                        onDelete = {
                            entry.id?.let { onAction(HealthAction.DeleteRecord(it)) }
                        },
                        content = {
                            HealthTableItem(entry)
                        })
                    if (index != data.size - 1) HorizontalDivider()
                }
            }
        }
    }
}

private fun LazyListScope.listHeader(
    uiState: HealthUiState.Success,
    onAction: (HealthAction) -> Unit,
) {
    item {
        InfoRow(
            infoRowData = uiState.data.infoRowData,
            onTimeRangeDropdownAction = {
                onAction(
                    HealthAction.ToggleTimeRangeDropdown(
                        it
                    )
                )
            },
            updateTimeRange = { onAction(HealthAction.UpdateTimeRange(it)) },
            onInfoAction = { onAction(HealthAction.DescriptionBottomSheet) },
            modifier = Modifier.testIdentifier(HealthPageTestIdentifier.HEALTH_HEADER)
        )
        HealthChart(
            uiState = uiState.data,
            modifier = Modifier
                .padding(bottom = Spacings.medium)
                .testIdentifier(HealthPageTestIdentifier.HEALTH_CHART)
        )
    }
}

@Composable
private fun CenteredContent(content: ComposableBlock) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .testIdentifier(HealthPageTestIdentifier.CENTERED_CONTENT),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

@Composable
private fun InfoRow(
    infoRowData: InfoRowData,
    onTimeRangeDropdownAction: (Boolean) -> Unit,
    updateTimeRange: (TimeRange) -> Unit,
    onInfoAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(vertical = Spacings.medium)
    ) {
        Column {
            Text(
                text = infoRowData.formattedValue,
                style = TextStyles.headlineLarge.copy(color = primary),
                modifier = Modifier.padding(vertical = Spacings.small)
            )
            Text(
                text = infoRowData.formattedDate,
                style = TextStyles.bodyMedium
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        TimeRangeDropdown(
            isSelectedTimeRangeDropdownExpanded = infoRowData.isSelectedTimeRangeDropdownExpanded,
            selectedTimeRange = infoRowData.selectedTimeRange,
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

enum class HealthPageTestIdentifier {
    ROOT,
    CENTERED_CONTENT,
    ERROR_MESSAGE,
    NO_DATA_MESSAGE,
    PROGRESS_INDICATOR,
    HEALTH_HEADER,
    HEALTH_CHART,
    HEALTH_HISTORY_TABLE_ITEM,
    HEALTH_HISTORY_TEXT,
}

private class HealthPagePreviewProvider : PreviewParameterProvider<HealthUiState> {
    override val values: Sequence<HealthUiState>
        get() = sequenceOf(
            HealthUiState.Loading,
            HealthUiState.Error("An error occurred"),
            HealthUiState.NoData("No data available"),
            HealthUiState.Success(
                data = HealthUiData(
                    infoRowData = InfoRowData(
                        selectedTimeRange = TimeRange.MONTHLY,
                        formattedValue = "70.0 kg",
                        formattedDate = "Jan 2022",
                        isSelectedTimeRangeDropdownExpanded = false
                    ),
                    records = listOf(
                        WeightRecord(
                            time = ZonedDateTime.now().toInstant(),
                            zoneOffset = ZonedDateTime.now().offset,
                            weight = @Suppress("MagicNumber") Mass.pounds(154.0)
                        )
                    ),
                    tableData = listOf(
                        TableEntryData(
                            value = 70.0f,
                            formattedValues = "70.0 kg",
                            date = ZonedDateTime.now(),
                            formattedDate = "Jan 2022",
                            trend = 0f,
                            formattedTrend = "0.0 kg",
                            secondValue = null,
                            id = null
                        )
                    )
                )
            )
        )
}

@ThemePreviews
@Composable
private fun HealthPagePreview(@PreviewParameter(HealthPagePreviewProvider::class) uiState: HealthUiState) {
    HealthPage(
        uiState = uiState,
        onAction = {}
    )
}
