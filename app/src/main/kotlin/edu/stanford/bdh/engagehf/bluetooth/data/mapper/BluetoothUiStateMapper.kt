package edu.stanford.bdh.engagehf.bluetooth.data.mapper

import edu.stanford.bdh.engagehf.bluetooth.data.models.BluetoothUiState
import edu.stanford.bdh.engagehf.bluetooth.data.models.DeviceUiModel
import edu.stanford.spezi.core.bluetooth.data.model.BLEServiceState
import edu.stanford.spezi.core.bluetooth.data.model.Measurement
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

    fun formatWeightForLocale(weight: Double): String {
        val weightInPounds = weight * KG_TO_LBS_CONVERSION_FACTOR
        return String.format(
            Locale.getDefault(), "%.2f", when (Locale.getDefault().country) {
                "US", "LR", "MM" -> weightInPounds
                else -> weight
            }
        ) + when (Locale.getDefault().country) {
            "US", "LR", "MM" -> " lbs"
            else -> " kg"
        }
    }

    fun formatSystolicForLocale(systolic: Float): String {
        return String.format(Locale.getDefault(), "%.0f mmHg", systolic)
    }

    fun formatDiastolicForLocale(diastolic: Float): String {
        return String.format(Locale.getDefault(), "%.0f mmHg", diastolic)
    }

    fun formatHeartRateForLocale(heartRate: Float): String {
        return String.format(Locale.getDefault(), "%.0f bpm", heartRate)
    }

    companion object {
        const val KG_TO_LBS_CONVERSION_FACTOR = 2.20462
    }

    private fun format(value: Number?): String = String.format(Locale.US, "%.2f", value)
}
