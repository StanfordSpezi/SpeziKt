package edu.stanford.bdh.engagehf.health.heartrate.bottomsheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.bdh.engagehf.R
import edu.stanford.bdh.engagehf.health.components.AddDataHeader
import edu.stanford.bdh.engagehf.health.components.NumberPicker
import edu.stanford.bdh.engagehf.health.components.TimePicker
import edu.stanford.spezi.core.design.component.VerticalSpacer
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.TextStyles
import edu.stanford.spezi.core.design.theme.ThemePreviews

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
                modifier = Modifier.size(height = 110.dp, width = 70.dp),
                value = uiState.heartRate,
                onValueChange = {
                    onAction(AddHeartRateBottomSheetViewModel.Action.UpdateHeartRate(it))
                },
                range = uiState.minHeartRate..uiState.maxHeartRate
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
            heartRate = 60,
        ),
        AddHeartRateBottomSheetUiState(
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
