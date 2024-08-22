package edu.stanford.bdh.engagehf.bluetooth.data.mapper

import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.WeightRecord
import edu.stanford.bdh.engagehf.bluetooth.component.OperationStatus
import edu.stanford.bdh.engagehf.bluetooth.data.models.BluetoothUiState
import edu.stanford.bdh.engagehf.bluetooth.data.models.DeviceUiModel
import edu.stanford.bdh.engagehf.bluetooth.data.models.MeasurementDialogUiState
import edu.stanford.bdh.engagehf.bluetooth.data.models.VitalDisplayData
import edu.stanford.bdh.engagehf.messages.MessagesAction
import edu.stanford.spezi.core.bluetooth.data.model.BLEServiceState
import edu.stanford.spezi.core.bluetooth.data.model.Measurement
import edu.stanford.spezi.core.utils.LocaleProvider
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class BluetoothUiStateMapper @Inject constructor(
    private val localeProvider: LocaleProvider,
    private val messageActionMapper: MessageActionMapper,
) {

    private val dateFormatter by lazy {
        DateTimeFormatter.ofPattern(
            "dd.MM.yyyy, HH:mm", localeProvider.getDefaultLocale()
        )
    }

    fun mapBleServiceState(state: BLEServiceState.Scanning): BluetoothUiState.Ready {
        val devices = state.sessions.map {
            val summary = when (val lastMeasurement = it.measurements.lastOrNull()) {
                is Measurement.BloodPressure -> "Blood Pressure: ${
                    formatSystolicForLocale(
                        lastMeasurement.systolic
                    )
                } / ${
                    formatDiastolicForLocale(
                        lastMeasurement.diastolic
                    )
                }"

                is Measurement.Weight -> "Weight: ${formatWeightForLocale(lastMeasurement.weight)}"
                else -> "No measurement received yet"
            }
            DeviceUiModel(
                address = it.device.address,
                measurementsCount = it.measurements.size,
                summary = summary,
            )
        }
        val header =
            if (devices.isEmpty()) "No devices connected yet" else "Connected devices (${devices.size})"
        return BluetoothUiState.Ready(
            header = header,
            devices = devices
        )
    }

    fun mapToMeasurementDialogUiState(measurement: Measurement): MeasurementDialogUiState {
        return when (measurement) {
            is Measurement.Weight -> MeasurementDialogUiState(
                measurement = measurement,
                isVisible = true,
                formattedWeight = formatWeightForLocale(measurement.weight)
            )

            is Measurement.BloodPressure -> MeasurementDialogUiState(
                measurement = measurement,
                isVisible = true,
                formattedSystolic = formatSystolicForLocale(measurement.systolic),
                formattedDiastolic = formatDiastolicForLocale(measurement.diastolic),
                formattedHeartRate = formatHeartRateForLocale(measurement.pulseRate)
            )
        }
    }

    fun mapBloodPressure(result: Result<BloodPressureRecord?>): VitalDisplayData {
        val title = "Blood Pressure"
        return mapRecordResult(
            result = result,
            title = title,
            onSuccess = { record ->
                VitalDisplayData(
                    title = title,
                    status = OperationStatus.SUCCESS,
                    date = dateFormatter.format(record.time.atZone(record.zoneOffset)),
                    value = "${record.systolic.inMillimetersOfMercury}/${record.diastolic.inMillimetersOfMercury}",
                    unit = "mmHg"
                )
            }
        )
    }

    fun mapWeight(result: Result<WeightRecord?>): VitalDisplayData {
        val title = "Weight"
        val locale = getDefaultLocale()
        return mapRecordResult(
            result = result,
            title = title,
            onSuccess = { record ->
                VitalDisplayData(
                    title = title,
                    value = String.format(
                        locale,
                        "%.2f",
                        when (locale.country) {
                            "US", "LR", "MM" -> record.weight.inPounds
                            else -> record.weight.inKilograms
                        }
                    ),
                    unit = when (locale.country) {
                        "US", "LR", "MM" -> "lbs"
                        else -> "kg"
                    },
                    date = dateFormatter.format(record.time.atZone(record.zoneOffset)),
                    status = OperationStatus.SUCCESS,
                )
            }
        )
    }

    fun mapHeartRate(result: Result<HeartRateRecord?>): VitalDisplayData {
        val title = "Heart Rate"
        return mapRecordResult(
            result = result,
            title = title,
            onSuccess = { record ->
                VitalDisplayData(
                    title = title,
                    value = "${
                        record.samples.stream()
                            .mapToLong(HeartRateRecord.Sample::beatsPerMinute)
                            .average()
                            .orElse(0.0)
                    }",
                    unit = "bpm",
                    date = record.samples.firstOrNull()?.let {
                        dateFormatter.format(it.time.atZone(record.startZoneOffset))
                    },
                    status = OperationStatus.SUCCESS
                )
            }
        )
    }

    fun mapMessagesAction(action: String): Result<MessagesAction> {
        return messageActionMapper.map(action)
    }

    private fun <T : Record> mapRecordResult(
        result: Result<T?>,
        title: String,
        onSuccess: (T) -> VitalDisplayData,
    ): VitalDisplayData {
        val successResult = result.getOrNull()
        return when {
            result.isFailure -> VitalDisplayData(
                title = title,
                status = OperationStatus.FAILURE,
                date = null,
                value = null,
                unit = null
            )

            successResult != null -> onSuccess(successResult)
            else -> VitalDisplayData(
                title = title,
                value = "No data available",
                unit = null,
                date = null,
                status = OperationStatus.NO_DATA
            )
        }
    }

    private fun formatWeightForLocale(weight: Double): String {
        val locale = getDefaultLocale()
        return String.format(
            locale, "%.2f", when (locale.country) {
                "US", "LR", "MM" -> weight * KG_TO_LBS_CONVERSION_FACTOR
                else -> weight
            }
        ) + when (locale.country) {
            "US", "LR", "MM" -> " lbs"
            else -> " kg"
        }
    }

    private fun formatSystolicForLocale(systolic: Float): String {
        return String.format(getDefaultLocale(), "%.0f mmHg", systolic)
    }

    private fun formatDiastolicForLocale(diastolic: Float): String {
        return String.format(getDefaultLocale(), "%.0f mmHg", diastolic)
    }

    private fun formatHeartRateForLocale(heartRate: Float): String {
        return String.format(getDefaultLocale(), "%.0f bpm", heartRate)
    }

    private fun getDefaultLocale() = localeProvider.getDefaultLocale()

    companion object {
        const val KG_TO_LBS_CONVERSION_FACTOR = 2.20462
    }
}
