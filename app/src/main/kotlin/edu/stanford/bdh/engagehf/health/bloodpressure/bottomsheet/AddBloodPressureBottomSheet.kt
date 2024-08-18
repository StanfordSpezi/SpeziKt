package edu.stanford.bdh.engagehf.health.bloodpressure.bottomsheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
fun AddBloodPressureBottomSheet() {
    val viewModel = hiltViewModel<AddBloodPressureBottomSheetViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    AddBloodPressureBottomSheet(
        uiState = uiState, onAction = viewModel::onAction
    )
}

@Composable
private fun AddBloodPressureBottomSheet(
    uiState: AddBloodPressureBottomSheetUiState,
    onAction: (AddBloodPressureBottomSheetViewModel.Action) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacings.medium),
        horizontalAlignment = Alignment.Start,
    ) {
        AddDataHeader(
            onClose = { onAction(AddBloodPressureBottomSheetViewModel.Action.CloseSheet) },
            onSave = { onAction(AddBloodPressureBottomSheetViewModel.Action.SaveBloodPressure) },
        )
        TimePicker(
            state = uiState.timePickerState,
            updateDate = { onAction(AddBloodPressureBottomSheetViewModel.Action.UpdateDate(it)) },
            updateTime = { onAction(AddBloodPressureBottomSheetViewModel.Action.UpdateTime(it)) },
        )
        HorizontalDivider()
        VerticalSpacer()
        Row {
            Text(text = stringResource(R.string.blood_pressure), style = TextStyles.labelLarge)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = stringResource(R.string.mmhg), style = TextStyles.labelLarge)
        }
        VerticalSpacer()
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            NumberPicker(
                modifier = Modifier
                    .height(110.dp)
                    .width(70.dp),
                value = uiState.systolic,
                onValueChange = {
                    onAction(AddBloodPressureBottomSheetViewModel.Action.UpdateSystolic(it))
                },
                range = uiState.minValueSystolic..uiState.maxValueSystolic
            )
            Text(text = " / ", style = TextStyles.bodyLarge)
            NumberPicker(
                modifier = Modifier
                    .height(110.dp)
                    .width(70.dp),
                value = uiState.diastolic,
                onValueChange = {
                    onAction(AddBloodPressureBottomSheetViewModel.Action.UpdateDiastolic(it))
                },
                range = uiState.minValueDiastolic..uiState.maxValueDiastolic
            )
        }
        VerticalSpacer()
        HorizontalDivider()
        VerticalSpacer()
        Text(text = stringResource(R.string.measurement_details), style = TextStyles.labelSmall)
        VerticalSpacer()
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(id = R.string.body_position), style = TextStyles.labelLarge)
            Spacer(modifier = Modifier.weight(1f))
            TextButton(onClick = {
                onAction(
                    AddBloodPressureBottomSheetViewModel.Action.ShowBodyPositionsDialog(
                        true
                    )
                )
            }) {
                BodyPositionText(uiState.bodyPosition)
            }
            if (uiState.isBodyPositionsDialogShown) {
                BodyPositionsDialog(onDismissRequest = {
                    onAction(
                        AddBloodPressureBottomSheetViewModel.Action.ShowBodyPositionsDialog(
                            false
                        )
                    )
                }, onOptionSelected = {
                    onAction(
                        AddBloodPressureBottomSheetViewModel.Action.UpdateBodyPosition(it)
                    )
                }, bodyPositions = uiState.bodyPositions
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(id = R.string.measurement_location),
                style = TextStyles.labelLarge
            )
            Spacer(modifier = Modifier.weight(1f))
            TextButton(onClick = {
                onAction(
                    AddBloodPressureBottomSheetViewModel.Action.ShowMeasurementLocationsDialog(
                        true
                    )
                )
            }) {
                MeasurementLocationText(uiState.measurementLocation)
            }
            if (uiState.isMeasurementLocationsDialogShown) {
                MeasurementLocationDialog(onDismissRequest = {
                    onAction(
                        AddBloodPressureBottomSheetViewModel.Action.ShowMeasurementLocationsDialog(
                            false
                        )
                    )
                }, onOptionSelected = {
                    onAction(
                        AddBloodPressureBottomSheetViewModel.Action.UpdateMeasurementLocation(it)
                    )
                }, measurementLocations = uiState.measurementLocations
                )
            }
        }
        VerticalSpacer()
        HorizontalDivider()
    }
}

@Composable
fun MeasurementLocationText(measurementLocation: MeasurementLocations) {
    val text = when (measurementLocation) {
        MeasurementLocations.MEASUREMENT_LOCATION_UNKNOWN -> stringResource(id = R.string.not_set)
        MeasurementLocations.MEASUREMENT_LOCATION_LEFT_WRIST -> stringResource(id = R.string.left_wrist)
        MeasurementLocations.MEASUREMENT_LOCATION_RIGHT_WRIST -> stringResource(id = R.string.right_wrist)
        MeasurementLocations.MEASUREMENT_LOCATION_LEFT_UPPER_ARM -> stringResource(id = R.string.left_upper_arm)
        MeasurementLocations.MEASUREMENT_LOCATION_RIGHT_UPPER_ARM -> stringResource(id = R.string.right_upper_arm)
    }
    Text(text = text, style = TextStyles.bodyMedium)
}

@Composable
fun BodyPositionText(bodyPosition: BodyPositions) {
    val text = when (bodyPosition) {
        BodyPositions.BODY_POSITION_UNKNOWN -> stringResource(id = R.string.not_set)
        BodyPositions.BODY_POSITION_STANDING_UP -> stringResource(id = R.string.standing_up)
        BodyPositions.BODY_POSITION_SITTING_DOWN -> stringResource(id = R.string.sitting_down)
        BodyPositions.BODY_POSITION_LYING_DOWN -> stringResource(id = R.string.lying_down)
        BodyPositions.BODY_POSITION_RECLINING -> stringResource(id = R.string.reclining)
    }
    Text(text = text, style = TextStyles.bodyMedium)
}

@ThemePreviews
@Composable
private fun AddDataViewPreview() {
    SpeziTheme {
        AddBloodPressureBottomSheet(
            uiState = AddBloodPressureBottomSheetUiState(),
            onAction = {}
        )
    }
}
