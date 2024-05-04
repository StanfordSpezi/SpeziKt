package edu.stanford.spezikt.core.bluetooth.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import edu.stanford.spezikt.core.bluetooth.BluetoothViewModel
import edu.stanford.spezikt.core.bluetooth.model.BloodPressureMeasurement
import edu.stanford.spezikt.core.bluetooth.model.WeightMeasurement

@Composable
fun MeasurementAlertDialog(viewModel: BluetoothViewModel) {
    if (viewModel.showDialog.value) {
        AlertDialog(
            onDismissRequest = { viewModel.showDialog.value = false },
            title = { Text("New Measurement") },
            text = {
                when (val measurement = viewModel.currentMeasurement.value) {
                    is BloodPressureMeasurement -> {
                        BloodPressureData(measurement)
                    }

                    is WeightMeasurement -> {
                        WeightData(measurement)
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.showDialog.value = false
                    when (val measurement = viewModel.currentMeasurement.value) {
                        is BloodPressureMeasurement -> {
                            viewModel.bloodPressureData.value = measurement
                        }

                        is WeightMeasurement -> {
                            viewModel.weightData.value = measurement
                        }
                    }
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(onClick = { viewModel.showDialog.value = false }) {
                    Text("Dismiss")
                }
            }
        )
    }
}