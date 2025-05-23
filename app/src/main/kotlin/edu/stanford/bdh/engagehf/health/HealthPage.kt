package edu.stanford.bdh.engagehf.health

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.units.Mass
import edu.stanford.bdh.engagehf.R
import edu.stanford.bdh.engagehf.bluetooth.screen.MeasurementDialogTestIdentifier
import edu.stanford.bdh.engagehf.health.components.HealthChart
import edu.stanford.bdh.engagehf.health.components.SwipeBox
import edu.stanford.bdh.engagehf.health.components.TimeRangeDropdown
import edu.stanford.spezi.ui.AsyncTextButton
import edu.stanford.spezi.ui.CenteredBoxContent
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.testIdentifier
import edu.stanford.spezi.ui.theme.Colors.primary
import edu.stanford.spezi.ui.theme.Sizes
import edu.stanford.spezi.ui.theme.Spacings
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.TextStyles
import edu.stanford.spezi.ui.theme.ThemePreviews
import java.time.ZonedDateTime

@Composable
fun HealthPage(
    uiState: HealthUiState,
    onAction: (HealthAction) -> Unit,
) {
    when (uiState) {
        is HealthUiState.Error -> {
            CenteredBoxContent {
                Text(
                    text = uiState.message.text(),
                    style = TextStyles.headlineMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testIdentifier(HealthPageTestIdentifier.ERROR_MESSAGE)
                )
            }
        }

        HealthUiState.Loading -> {
            CenteredBoxContent {
                CircularProgressIndicator(
                    color = primary,
                    modifier = Modifier.testIdentifier(HealthPageTestIdentifier.PROGRESS_INDICATOR)
                )
            }
        }

        is HealthUiState.NoData -> {
            CenteredBoxContent {
                Text(
                    text = uiState.message.text(),
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
                            entry.id?.let { onAction(HealthAction.RequestDeleteRecord(it)) }
                        },
                        content = {
                            HealthTableItem(entry)
                        })
                    if (index != data.size - 1) HorizontalDivider()
                }
            }

            DeleteRecordConfirmationAlert(
                uiData = uiState.data,
                onAction = onAction
            )
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
                imageVector = Icons.Filled.Info,
                contentDescription = stringResource(R.string.info_icon_content_description),
                tint = primary
            )
        }
    }
}

@Composable
private fun DeleteRecordConfirmationAlert(
    uiData: HealthUiData,
    onAction: (HealthAction) -> Unit,
) {
    val dialogData = uiData.deleteRecordAlertData ?: return
    val deleteAction = remember(dialogData.recordId) {
        HealthAction.Async.DeleteRecord(dialogData.recordId)
    }
    AlertDialog(
        onDismissRequest = {
            onAction(HealthAction.DismissConfirmationAlert)
        },
        title = {
            Text(
                text = dialogData.title.text(),
                style = TextStyles.titleMedium,
                modifier = Modifier.testIdentifier(MeasurementDialogTestIdentifier.TITLE)
            )
        },
        text = {
            Text(
                text = dialogData.description.text(),
                style = TextStyles.titleSmall,
                modifier = Modifier.testIdentifier(MeasurementDialogTestIdentifier.TITLE)
            )
        },
        confirmButton = {
            AsyncTextButton(
                text = dialogData.confirmButton.text(),
                isLoading = uiData.pendingActions.contains(action = deleteAction),
                onClick = { onAction(deleteAction) }
            )
        },
        dismissButton = {
            FilledTonalButton(
                enabled = uiData.pendingActions.contains(action = deleteAction).not(),
                onClick = {
                    onAction(HealthAction.DismissConfirmationAlert)
                }
            ) {
                Text(dialogData.dismissButton.text())
            }
        }
    )
}

enum class HealthPageTestIdentifier {
    ROOT,
    ERROR_MESSAGE,
    NO_DATA_MESSAGE,
    PROGRESS_INDICATOR,
    HEALTH_HEADER,
    HEALTH_CHART,
    HEALTH_HISTORY_TABLE_ITEM,
    HEALTH_HISTORY_TEXT,
}

private class HealthPagePreviewProvider : PreviewParameterProvider<HealthUiState> {
    val success = HealthUiState.Success(
        data = HealthUiData(
            valueFormatter = { "Jan 24" },
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
                    value = 70.0,
                    formattedValues = "70.0 kg",
                    date = ZonedDateTime.now(),
                    formattedDate = "Jan 2022",
                    trend = 0.0,
                    formattedTrend = "0.0 kg",
                    secondValue = null,
                    id = "null"
                )
            )
        )
    )
    override val values: Sequence<HealthUiState>
        get() = sequenceOf(
            HealthUiState.Loading,
            HealthUiState.Error(StringResource(R.string.generic_error_description)),
            HealthUiState.NoData(StringResource(R.string.no_data_available)),
            success,
            success.copy(
                data = success.data.copy(
                    deleteRecordAlertData = DeleteRecordAlertData(
                        recordId = "",
                        title = StringResource(R.string.delete_health_record),
                        description = StringResource(R.string.health_record_deletion_description),
                        confirmButton = StringResource(R.string.confirm_button_text),
                        dismissButton = StringResource(R.string.dismiss_button_text),
                    )
                )
            )
        )
}

@ThemePreviews
@Composable
private fun HealthPagePreview(@PreviewParameter(HealthPagePreviewProvider::class) uiState: HealthUiState) {
    SpeziTheme {
        HealthPage(
            uiState = uiState,
            onAction = {}
        )
    }
}
