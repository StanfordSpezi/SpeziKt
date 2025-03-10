package edu.stanford.bdh.engagehf.health.heartrate.bottomsheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.bdh.engagehf.R
import edu.stanford.bdh.engagehf.health.components.AddDataHeader
import edu.stanford.bdh.engagehf.health.components.NumberPicker
import edu.stanford.bdh.engagehf.health.components.TimePicker
import edu.stanford.bdh.engagehf.health.time.TimePickerState
import edu.stanford.spezi.modules.design.component.VerticalSpacer
import edu.stanford.spezi.ui.Spacings
import edu.stanford.spezi.ui.SpeziTheme
import edu.stanford.spezi.ui.TextStyles
import edu.stanford.spezi.ui.ThemePreviews
import java.time.Instant
import java.time.LocalTime

@Composable
fun AddHeartRateBottomSheet() {
    val viewModel = hiltViewModel<AddHeartRateBottomSheetViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    AddHeartRateBottomSheet(
        uiState = uiState, onAction = viewModel::onAction,
    )
}

@Composable
private fun AddHeartRateBottomSheet(
    uiState: AddHeartRateBottomSheetUiState,
    onAction: (AddHeartRateBottomSheetViewModel.Action) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacings.medium),
        horizontalAlignment = Alignment.Start,
    ) {
        AddDataHeader(
            onClose = { onAction(AddHeartRateBottomSheetViewModel.Action.CloseSheet) },
            onSave = { onAction(AddHeartRateBottomSheetViewModel.Action.SaveHeartRate) },
        )

        TimePicker(
            state = uiState.timePickerState,
            updateDate = { onAction(AddHeartRateBottomSheetViewModel.Action.UpdateDate(it)) },
            updateTime = { onAction(AddHeartRateBottomSheetViewModel.Action.UpdateTime(it)) },
        )
        HorizontalDivider()
        VerticalSpacer()
        Row {
            Text(text = stringResource(R.string.heart_rate), style = TextStyles.labelLarge)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = stringResource(R.string.bpm), style = TextStyles.labelLarge)
        }
        VerticalSpacer()
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            NumberPicker(
                value = uiState.heartRate,
                onValueChange = {
                    onAction(AddHeartRateBottomSheetViewModel.Action.UpdateHeartRate(it))
                },
                range = uiState.heartRateRange
            )
        }
        VerticalSpacer()
        HorizontalDivider()
    }
}

private class AddHeartRateBottomSheetUiStateProvider :
    PreviewParameterProvider<AddHeartRateBottomSheetUiState> {
    override val values = sequenceOf(
        AddHeartRateBottomSheetUiState(
            timePickerState = TimePickerState(
                selectedDate = Instant.now(),
                selectedTime = LocalTime.now(),
                initialHour = 12,
                initialMinute = 0,
                selectedDateFormatted = "Today",
                selectedTimeFormatted = "12:00 PM"
            ),
            heartRate = 60,
        ),
        AddHeartRateBottomSheetUiState(
            timePickerState = TimePickerState(
                selectedDate = Instant.now(),
                selectedTime = LocalTime.now(),
                initialHour = 12,
                initialMinute = 0,
                selectedDateFormatted = "Today",
                selectedTimeFormatted = "12:00 PM"
            ),
            heartRate = 80,
        )
    )
}

@Composable
@ThemePreviews
private fun AddHeartRateBottomSheetPreview(
    @PreviewParameter(AddHeartRateBottomSheetUiStateProvider::class) uiState: AddHeartRateBottomSheetUiState,
) {
    SpeziTheme {
        AddHeartRateBottomSheet(
            uiState = uiState,
            onAction = {}
        )
    }
}
