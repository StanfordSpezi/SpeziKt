package edu.stanford.bdh.engagehf.health.weight.bottomsheet

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
fun AddWeightBottomSheet() {
    val viewModel = hiltViewModel<AddWeightBottomSheetViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    AddWeightBottomSheet(
        uiState = uiState, onAction = viewModel::onAction
    )
}

@Composable
fun AddWeightBottomSheet(
    uiState: AddWeightBottomSheetUiState,
    onAction: (AddWeightBottomSheetViewModel.Action) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacings.medium),
        horizontalAlignment = Alignment.Start,
    ) {
        AddDataHeader(
            onClose = {
                onAction(AddWeightBottomSheetViewModel.Action.CloseSheet)
            },
            onSave = {
                onAction(AddWeightBottomSheetViewModel.Action.SaveWeight)
            },
        )

        TimePicker(
            state = uiState.timePickerState,
            updateDate = {
                onAction(AddWeightBottomSheetViewModel.Action.UpdateDate(it))
            },
            updateTime = {
                onAction(AddWeightBottomSheetViewModel.Action.UpdateTime(it))
            },
        )
        HorizontalDivider()
        VerticalSpacer()
        Row {
            Text(text = stringResource(R.string.weight), style = TextStyles.labelLarge)
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = when (uiState.weightUnit) {
                    WeightUnit.KG -> stringResource(R.string.kg)
                    WeightUnit.LBS -> stringResource(R.string.lbs)
                },
                style = TextStyles.labelLarge
            )
        }
        VerticalSpacer()
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            NumberPicker(
                value = uiState.weight.toInt(),
                onValueChange = {
                    onAction(AddWeightBottomSheetViewModel.Action.UpdateWeight(it.toDouble()))
                },
                range = uiState.weightRange
            )
        }
        VerticalSpacer()
        HorizontalDivider()
    }
}

private class AddWeightBottomSheetStepProvider :
    PreviewParameterProvider<AddWeightBottomSheetUiState> {
    override val values: Sequence<AddWeightBottomSheetUiState> = sequenceOf(
        AddWeightBottomSheetUiState(
            weight = 70.0,
            weightUnit = WeightUnit.KG,
            timePickerState = TimePickerState(
                selectedDate = Instant.now(),
                selectedTime = LocalTime.now(),
                initialHour = LocalTime.now().hour,
                initialMinute = LocalTime.now().minute,
                selectedDateFormatted = "2024-05-13",
                selectedTimeFormatted = "14:30"
            )
        )
    )
}

@ThemePreviews
@Composable
fun AddWeightBottomSheetPreview(
    @PreviewParameter(AddWeightBottomSheetStepProvider::class) uiState: AddWeightBottomSheetUiState,
) {
    SpeziTheme(isPreview = true) {
        AddWeightBottomSheet(uiState = uiState, onAction = {})
    }
}
