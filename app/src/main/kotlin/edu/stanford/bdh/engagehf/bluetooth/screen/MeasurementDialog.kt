@file:Suppress("MagicNumber")

package edu.stanford.bdh.engagehf.bluetooth.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import edu.stanford.bdh.engagehf.bluetooth.data.models.Action
import edu.stanford.bdh.engagehf.bluetooth.data.models.MeasurementDialogUiState
import edu.stanford.spezi.core.bluetooth.data.model.Measurement
import edu.stanford.spezi.core.design.component.Button
import edu.stanford.spezi.core.design.theme.Sizes
import edu.stanford.spezi.core.utils.extensions.testIdentifier

@Composable
fun MeasurementDialog(
    uiState: MeasurementDialogUiState,
    onAction: (Action) -> Unit,
) {
    if (uiState.isVisible) {
        AlertDialog(
            modifier = Modifier.testIdentifier(MeasurementDialogTestIdentifier.ROOT),
            onDismissRequest = {
                onAction(Action.DismissDialog)
            },
            title = {
                Text(
                    text = "New Measurement",
                    modifier = Modifier.testIdentifier(MeasurementDialogTestIdentifier.TITLE)
                )
            },
            text = {
                Column {
                    uiState.measurement?.let {
                        if (it is Measurement.Weight) {
                            Text(
                                text = "Weight: ${it.weight}",
                                modifier = Modifier.testIdentifier(MeasurementDialogTestIdentifier.WEIGHT)
                            )
                        }
                        if (it is Measurement.BloodPressure) {
                            Text(text = "Systolic: ${it.systolic}")
                            Text(text = "Diastolic: ${it.diastolic}")
                            Text(text = "Pulse rate: ${it.pulseRate}")
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        uiState.measurement?.let {
                            onAction(Action.ConfirmMeasurement(it))
                        }
                    },
                    enabled = uiState.isProcessing.not()
                ) {
                    if (uiState.isProcessing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(Sizes.Icon.small)
                        )
                    } else {
                        Text("Confirm")
                    }
                }
            },
            dismissButton = {
                FilledTonalButton(
                    onClick = {
                        onAction(Action.DismissDialog)
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

private class MeasurementPreviewProvider : PreviewParameterProvider<Measurement> {
    override val values: Sequence<Measurement> = sequenceOf(
        MeasurementFactory.createDefaultWeight(),
        MeasurementFactory.createDefaultBloodPressure()
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
    WEIGHT,
}
