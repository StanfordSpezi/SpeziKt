package edu.stanford.bdh.engagehf.bluetooth.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import edu.stanford.bdh.engagehf.R
import edu.stanford.bdh.engagehf.bluetooth.data.models.Action
import edu.stanford.bdh.engagehf.bluetooth.data.models.MeasurementDialogUiState
import edu.stanford.spezi.core.bluetooth.data.model.Measurement
import edu.stanford.spezi.core.design.component.AsyncTextButton
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.TextStyles
import edu.stanford.spezi.core.utils.extensions.testIdentifier

@Composable
fun MeasurementDialog(
    uiState: MeasurementDialogUiState,
    onAction: (Action) -> Unit,
) {
    if (uiState.isVisible) {
        AlertDialog(
            modifier = Modifier
                .testIdentifier(MeasurementDialogTestIdentifier.ROOT)
                .padding(Spacings.medium),
            onDismissRequest = {
                onAction(Action.DismissDialog)
            },
            title = {
                Text(
                    text = stringResource(R.string.new_measurement),
                    style = TextStyles.titleMedium,
                    modifier = Modifier.testIdentifier(MeasurementDialogTestIdentifier.TITLE)
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(Spacings.small),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    uiState.measurement?.let {
                        if (it is Measurement.Weight) {
                            MeasurementRow(
                                label = stringResource(R.string.weight) + ":",
                                value = uiState.formattedWeight,
                            )
                        }
                        if (it is Measurement.BloodPressure) {
                            MeasurementRow(
                                label = stringResource(R.string.systolic) + ":",
                                value = uiState.formattedSystolic
                            )
                            MeasurementRow(
                                label = stringResource(R.string.diastolic) + ":",
                                value = uiState.formattedDiastolic
                            )
                            MeasurementRow(
                                label = stringResource(R.string.pulse_rate) + ":",
                                value = uiState.formattedHeartRate
                            )
                        }
                    }
                }
            },
            confirmButton = {
                AsyncTextButton(
                    text = stringResource(R.string.confirm_button_text),
                    isLoading = uiState.isProcessing,
                    onClick = {
                        uiState.measurement?.let {
                            onAction(Action.ConfirmMeasurement(it))
                        }
                    }
                )
            },
            dismissButton = {
                FilledTonalButton(
                    onClick = {
                        onAction(Action.DismissDialog)
                    }
                ) {
                    Text(stringResource(R.string.cancel_button_text))
                }
            }
        )
    }
}

@Composable
fun MeasurementRow(modifier: Modifier = Modifier, label: String, value: String) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacings.small)
    ) {
        Text(
            text = label,
            style = TextStyles.bodyMedium,
            modifier = Modifier
                .width(80.dp)
                .testIdentifier(MeasurementDialogTestIdentifier.MEASUREMENT_LABEL)
        )
        Text(
            text = value,
            style = TextStyles.bodyMedium,
            modifier = Modifier.testIdentifier(MeasurementDialogTestIdentifier.MEASUREMENT_VALUE)
        )
    }
}

private class MeasurementPreviewProvider : PreviewParameterProvider<Measurement> {
    override val values: Sequence<Measurement> = sequenceOf(
        MeasurementFactory.createDefaultWeight(),
        MeasurementFactory.createDefaultBloodPressure(),
    )
}

@Preview(
    showBackground = true
)
@Composable
private fun MeasurementDialogPreview(@PreviewParameter(MeasurementPreviewProvider::class) measurement: Measurement) {
    MeasurementDialog(
        uiState = MeasurementDialogUiState(
            measurement = measurement,
            isVisible = true,
            isProcessing = true
        ),
        onAction = { }
    )
}

@Suppress("MagicNumber")
private object MeasurementFactory {

    fun createDefaultBloodPressure(): Measurement.BloodPressure {
        return Measurement.BloodPressure(
            flags = Measurement.BloodPressure.Flags(
                bloodPressureUnitsFlag = false,
                timeStampFlag = false,
                pulseRateFlag = false,
                userIdFlag = false,
                measurementStatusFlag = false
            ),
            systolic = 120f,
            diastolic = 80f,
            meanArterialPressure = 90f,
            timestampYear = 2022,
            timestampMonth = 1,
            timestampDay = 1,
            timeStampHour = 12,
            timeStampMinute = 0,
            timeStampSecond = 0,
            pulseRate = 70f,
            userId = 1,
            measurementStatus = Measurement.BloodPressure.Status(
                bodyMovementDetectionFlag = false,
                cuffFitDetectionFlag = false,
                irregularPulseDetectionFlag = false,
                pulseRateRangeDetectionFlags = 0,
                measurementPositionDetectionFlag = false
            )
        )
    }

    fun createDefaultWeight(): Measurement.Weight {
        return Measurement.Weight(
            weight = 70.0,
            zonedDateTime = null,
            userId = null,
            bmi = null,
            height = null
        )
    }
}

enum class MeasurementDialogTestIdentifier {
    ROOT,
    TITLE,
    MEASUREMENT_LABEL,
    MEASUREMENT_VALUE,
}
