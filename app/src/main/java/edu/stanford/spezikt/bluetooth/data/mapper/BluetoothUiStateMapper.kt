package edu.stanford.spezikt.bluetooth.data.mapper

import edu.stanford.spezikt.bluetooth.data.models.BluetoothUiState
import edu.stanford.spezikt.bluetooth.data.models.DeviceUiModel
import edu.stanford.spezikt.core.bluetooth.data.model.BLEServiceState
import edu.stanford.spezikt.core.bluetooth.data.model.Measurement
import java.util.Locale
import javax.inject.Inject

class BluetoothUiStateMapper @Inject constructor() {

    fun map(state: BLEServiceState.Scanning): BluetoothUiState.Ready {
        val devices = state.sessions.map {
            val summary = when (val lastMeasurement = it.measurements.lastOrNull()) {
                is Measurement.BloodPressure -> "Blood Pressure: ${format(lastMeasurement.systolic)} / ${format(lastMeasurement.diastolic)}"
                is Measurement.Weight -> "Weight: ${format(lastMeasurement.weight)}"
                else -> "No measurement received yet"
            }
            DeviceUiModel(
                address = it.device.address,
                measurementsCount = it.measurements.size,
                summary = summary,
            )
        }
        val header = if (devices.isEmpty()) "No devices connected yet" else "Connected devices (${devices.size})"
        return BluetoothUiState.Ready(
            header = header,
            devices = devices
        )
    }

    private fun format(value: Number?): String = String.format(Locale.US, "%.2f", value)
}