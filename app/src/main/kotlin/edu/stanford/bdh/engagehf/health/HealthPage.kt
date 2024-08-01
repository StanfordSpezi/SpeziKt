package edu.stanford.bdh.engagehf.health

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.units.Mass
import edu.stanford.bdh.engagehf.health.components.HealthChart
import edu.stanford.bdh.engagehf.health.components.HealthHeader
import edu.stanford.bdh.engagehf.health.components.HealthHeaderData
import edu.stanford.spezi.core.design.component.VerticalSpacer
import edu.stanford.spezi.core.design.theme.Colors
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.TextStyles
import edu.stanford.spezi.core.design.theme.ThemePreviews
import edu.stanford.spezi.core.utils.extensions.testIdentifier
import java.time.ZonedDateTime

@Composable
fun HealthPage(
    uiState: HealthUiState,
    onAction: (HealthAction) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .testIdentifier(HealthPageTestIdentifier.ROOT)
    ) {
        when (uiState) {
            is HealthUiState.Error -> {
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
                Text(
                    text = uiState.message,
                    style = TextStyles.headlineMedium,
                    modifier = Modifier.testIdentifier(HealthPageTestIdentifier.ERROR_MESSAGE)
                )
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }

            HealthUiState.Loading -> {
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )
                    CircularProgressIndicator(
                        color = Colors.primary,
                        modifier = Modifier.testIdentifier(HealthPageTestIdentifier.PROGRESS_INDICATOR)
                    )
                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )
                }
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }

            is HealthUiState.Success -> {
                if (uiState.data.records.isEmpty()) {
                    Text(
                        text = "No data available",
                        style = TextStyles.headlineMedium
                    )
                } else {
                    HealthHeader(
                        uiState.data.headerData,
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
                        modifier = Modifier.testIdentifier(HealthPageTestIdentifier.HEALTH_CHART)
                    )
                    VerticalSpacer()
                    Row(
                        modifier = Modifier.padding(horizontal = Spacings.medium),
                        verticalAlignment = Alignment.Bottom,
                    ) {
                        Text(
                            text = "History",
                            style = TextStyles.headlineMedium,
                            modifier = Modifier.testIdentifier(HealthPageTestIdentifier.HEALTH_HISTORY_TEXT)
                        )
                    }
                    HealthTable(
                        healthEntries = uiState.data.tableData,
                        onAction = onAction,
                        modifier = Modifier.testIdentifier(HealthPageTestIdentifier.HEALTH_HISTORY_TABLE)
                    )
                }
            }
        }
    }
}

enum class HealthPageTestIdentifier {
    ROOT,
    ERROR_MESSAGE,
    PROGRESS_INDICATOR,
    HEALTH_HEADER,
    HEALTH_CHART,
    HEALTH_HISTORY_TABLE,
    HEALTH_HISTORY_TEXT,
}

private class HealthPagePreviewProvider : PreviewParameterProvider<HealthUiState> {
    override val values: Sequence<HealthUiState>
        get() = sequenceOf(
            HealthUiState.Loading,
            HealthUiState.Error("An error occurred"),
            HealthUiState.Success(
                data = HealthUiData(
                    headerData = HealthHeaderData(
                        selectedTimeRange = TimeRange.MONTHLY,
                        formattedValue = "70.0 kg",
                        formattedDate = "Jan 2022",
                        isSelectedTimeRangeDropdownExpanded = false
                    ),
                    records = listOf(
                        WeightRecord(
                            time = ZonedDateTime.now().toInstant(),
                            zoneOffset = ZonedDateTime.now().offset,
                            weight = Mass.pounds(154.0)
                        )
                    ),
                    tableData = listOf(
                        TableEntryData(
                            value = 70.0f,
                            formattedValues = "70.0 kg",
                            date = ZonedDateTime.now(),
                            formattedDate = "Jan 2022",
                            xAxis = 0f,
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
