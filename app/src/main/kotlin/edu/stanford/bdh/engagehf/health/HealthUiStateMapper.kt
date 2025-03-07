@file:Suppress("MagicNumber", "TooManyFunctions")

package edu.stanford.bdh.engagehf.health

import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.WeightRecord
import edu.stanford.bdh.engagehf.R
import edu.stanford.spezi.core.utils.LocaleProvider
import edu.stanford.spezi.core.utils.extensions.roundToDecimalPlaces
import edu.stanford.spezi.spezi.ui.resources.StringResource
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

class HealthUiStateMapper @Inject constructor(
    private val localeProvider: LocaleProvider,
) {

    private val monthDayTimeFormatter = DateTimeFormatter.ofPattern(MONTH_DAY_TIME_PATTERN)

    private val monthYearFormatter = DateTimeFormatter.ofPattern(MONTH_YEAR_PATTERN)

    fun mapToHealthData(
        records: List<Record>,
        selectedTimeRange: TimeRange,
    ): HealthUiState {
        val engageRecords = records.map { EngageRecord.from(it) }
            .filter {
                it.zonedDateTime.isAfter(
                    ZonedDateTime.now().minusMonths(getMaxMonths(selectedTimeRange))
                )
            }
        return if (engageRecords.isEmpty()) {
            HealthUiState.NoData(message = "No data available")
        } else {
            HealthUiState.Success(
                data = mapUiStateTimeRange(engageRecords, selectedTimeRange)
            )
        }
    }

    private fun mapUiStateTimeRange(
        records: List<EngageRecord>,
        selectedTimeRange: TimeRange,
    ): HealthUiData {
        val oldestRecordDate = records.minBy { it.zonedDateTime }.zonedDateTime
        val pairs = groupAndMapRecords(
            records = records,
            oldestZonedDateTime = oldestRecordDate,
            selectedTimeRange = selectedTimeRange
        )
        val title: String = when (records.first()) {
            is EngageRecord.HeartRate -> "Heart Rate"
            is EngageRecord.BloodPressure -> "Systolic"
            is EngageRecord.Weight -> "Weight"
        }

        val chartData = createAggregatedHealthData(title, pairs)

        val chartDataList = mutableListOf(chartData)
        if (records.any { it is EngageRecord.BloodPressure }) {
            val diastolicPairs = groupAndMapRecords(
                records = records,
                oldestZonedDateTime = oldestRecordDate,
                selectedTimeRange = selectedTimeRange,
                diastolic = true
            )
            val diastolicChartData = createAggregatedHealthData("Diastolic", diastolicPairs)
            chartDataList.add(diastolicChartData)
        }

        val newestData: NewestHealthData? = getNewestRecord(records)
        val tableData: List<TableEntryData> = mapTableData(records)

        return HealthUiData(
            records = records.map { it.record },
            chartData = chartDataList,
            tableData = tableData,
            newestData = newestData,
            averageData = getAverageData(tableData),
            infoRowData = generateHealthHeaderData(selectedTimeRange, newestData),
            valueFormatter = { value ->
                valueFormatter(
                    value = value.toLong(),
                    oldestZonedDateTime = oldestRecordDate,
                    timeRange = selectedTimeRange,
                )
            }
        )
    }

    private fun generateHealthHeaderData(
        selectedTimeRange: TimeRange,
        newestData: NewestHealthData?,
    ): InfoRowData {
        return InfoRowData(
            selectedTimeRange = selectedTimeRange,
            formattedValue = newestData?.formattedValue ?: "",
            formattedDate = newestData?.formattedDate ?: "",
            isSelectedTimeRangeDropdownExpanded = false
        )
    }

    private fun groupAndMapRecords(
        records: List<EngageRecord>,
        oldestZonedDateTime: ZonedDateTime,
        selectedTimeRange: TimeRange,
        diastolic: Boolean = false,
    ): List<Pair<Double, Double>> {
        val groupedRecords = groupRecordsByTimeRange(records, selectedTimeRange)
        return groupedRecords.values.map { entries ->
            val yValue = entries
                .map { getValue(it, diastolic).value }
                .average()
                .roundToDecimalPlaces(2)
            val xValue = mapXValue(
                selectedTimeRange = selectedTimeRange,
                zonedDateTime = entries.first().zonedDateTime,
                oldestZonedDateTime = oldestZonedDateTime,
            )
            Pair(yValue, xValue)
        }
    }

    private fun mapXValue(
        selectedTimeRange: TimeRange,
        zonedDateTime: ZonedDateTime,
        oldestZonedDateTime: ZonedDateTime,
    ): Double {
        return when (selectedTimeRange) {
            TimeRange.DAILY -> ChronoUnit.DAYS.between(oldestZonedDateTime, zonedDateTime)
            TimeRange.WEEKLY -> ChronoUnit.WEEKS.between(oldestZonedDateTime, zonedDateTime)
            TimeRange.MONTHLY -> ChronoUnit.MONTHS.between(oldestZonedDateTime, zonedDateTime)
        }.toDouble()
    }

    private fun groupRecordsByTimeRange(
        records: List<EngageRecord>,
        selectedTimeRange: TimeRange,
    ): Map<LocalDate, List<EngageRecord>> {
        val temporalAdjuster = when (selectedTimeRange) {
            TimeRange.DAILY -> null
            TimeRange.WEEKLY -> TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)
            TimeRange.MONTHLY -> TemporalAdjusters.firstDayOfMonth()
        }
        return records.groupBy {
            it.zonedDateTime.toLocalDate().let { localDate ->
                if (temporalAdjuster != null) localDate.with(temporalAdjuster) else localDate
            }
        }
    }

    private fun getValue(
        engageRecord: EngageRecord,
        diastolic: Boolean = false,
    ): ValueUnit {
        return when (engageRecord) {
            is EngageRecord.Weight -> {
                val displayPounds = when (getDefaultLocale().country) {
                    "US", "LR", "MM" -> true
                    else -> false
                }
                val weightValue = with(engageRecord.record.weight) {
                    if (displayPounds) inPounds else inKilograms
                }
                ValueUnit(
                    value = weightValue,
                    unit = if (displayPounds) "lbs" else "kg"
                )
            }

            is EngageRecord.BloodPressure -> if (diastolic) {
                ValueUnit(
                    value = engageRecord.record.diastolic.inMillimetersOfMercury,
                    unit = "mmHg"
                )
            } else {
                ValueUnit(
                    value = engageRecord.record.systolic.inMillimetersOfMercury,
                    unit = "mmHg"
                )
            }

            is EngageRecord.HeartRate -> {
                ValueUnit(
                    value = engageRecord.record.samples.first().beatsPerMinute.toDouble(),
                    unit = "bpm"
                )
            }
        }
    }

    private fun getBloodPressureFormatRecord(record: BloodPressureRecord): String {
        return String.format(
            getDefaultLocale(),
            "%.0f",
            record.systolic.inMillimetersOfMercury
        ) + "/" + String.format(
            getDefaultLocale(),
            "%.0f",
            record.diastolic.inMillimetersOfMercury
        ) + " mmHg"
    }

    private fun formatValue(value: Double, unit: String = ""): String {
        return String.format(getDefaultLocale(), "%.1f", value) + " " + unit
    }

    private fun getMaxMonths(selectedTimeRange: TimeRange): Long {
        return when (selectedTimeRange) {
            TimeRange.DAILY -> DAILY_MAX_MONTHS
            TimeRange.WEEKLY -> WEEKLY_MAX_MONTHS
            TimeRange.MONTHLY -> MONTHLY_MAX_MONTHS
        }
    }

    private fun createAggregatedHealthData(
        title: String,
        pairs: List<Pair<Double, Double>>,
    ): AggregatedHealthData {
        return AggregatedHealthData(
            yValues = pairs.map { it.first },
            xValues = pairs.map { it.second },
            seriesName = title
        )
    }

    private fun mapTableData(records: List<EngageRecord>): List<TableEntryData> {
        if (records.isEmpty()) return emptyList()

        val tableData = mutableListOf<TableEntryData>()
        var previousRecord: EngageRecord? = null

        // reversing to calculate the trend starting from the first (oldest) record, and inserting
        // at 0-th index in tableData list
        records.reversed().forEach { currentRecord ->
            val currentRecordValue = getValue(currentRecord)
            val trend = previousRecord?.let {
                currentRecordValue.value - getValue(it).value
            } ?: 0.0
            val trendSign = when {
                trend > 0 -> "▲"
                trend < 0 -> "▼"
                else -> "▶"
            }
            val formattedTrend = buildString {
                append(trendSign)
                append(String.format(getDefaultLocale(), "%.0f", trend))
                append(currentRecordValue.unit)
            }

            val tableEntryData = TableEntryData(
                id = currentRecord.clientRecordId,
                value = currentRecordValue.value,
                secondValue = if (currentRecord is EngageRecord.BloodPressure) {
                    getValue(currentRecord, true).value.toFloat()
                } else {
                    null
                },
                date = currentRecord.zonedDateTime,
                formattedDate = currentRecord.zonedDateTime.format(monthDayTimeFormatter),
                trend = trend,
                formattedTrend = formattedTrend,
                formattedValues = if (currentRecord is EngageRecord.BloodPressure) {
                    getBloodPressureFormatRecord(currentRecord.record)
                } else {
                    formatValue(currentRecordValue.value, currentRecordValue.unit)
                },
            )

            tableData.add(0, tableEntryData)
            previousRecord = currentRecord
        }

        return tableData
    }

    private fun getAverageData(tableData: List<TableEntryData>) =
        AverageHealthData(
            value = tableData.mapNotNull { it.value }.average(),
            formattedValue = "Average " + formatValue(tableData.mapNotNull { it.value }.average()),
        )

    private fun getNewestRecord(records: List<EngageRecord>): NewestHealthData? {
        val newestRecord = records.maxByOrNull { it.zonedDateTime.toEpochSecond() }

        return newestRecord?.let {
            NewestHealthData(
                formattedDate = it.zonedDateTime.format(monthYearFormatter),
                formattedValue = if (it is EngageRecord.BloodPressure) {
                    getBloodPressureFormatRecord(it.record)
                } else {
                    val recordValue = getValue(it)
                    formatValue(recordValue.value, recordValue.unit)
                }
            )
        }
    }

    fun updateTimeRange(
        currentUiState: HealthUiState,
        newTimeRange: TimeRange,
    ): HealthUiState {
        return if (currentUiState is HealthUiState.Success) {
            mapToHealthData(
                records = currentUiState.data.records,
                selectedTimeRange = newTimeRange
            )
        } else {
            currentUiState
        }
    }

    fun mapToggleTimeRange(
        healthAction: HealthAction.ToggleTimeRangeDropdown,
        uiState: HealthUiState,
    ) = if (uiState is HealthUiState.Success) {
        uiState.copy(
            data = uiState.data.copy(
                infoRowData = uiState.data.infoRowData.copy(
                    isSelectedTimeRangeDropdownExpanded = healthAction.expanded
                )
            )
        )
    } else {
        uiState
    }

    fun mapDeleteRecordAlertData(
        action: HealthAction.RequestDeleteRecord,
    ) = DeleteRecordAlertData(
        recordId = action.recordId,
        title = StringResource(R.string.delete_health_record),
        description = StringResource(R.string.health_record_deletion_description),
        dismissButton = StringResource(R.string.dismiss_button_text),
        confirmButton = StringResource(R.string.confirm_button_text),
    )

    private fun getDefaultLocale() = localeProvider.getDefaultLocale()

    private fun valueFormatter(
        value: Long,
        oldestZonedDateTime: ZonedDateTime,
        timeRange: TimeRange,
    ): String {
        val date = when (timeRange) {
            TimeRange.DAILY -> oldestZonedDateTime.plusDays(value)
            TimeRange.WEEKLY -> oldestZonedDateTime.plusWeeks(value)
            TimeRange.MONTHLY -> oldestZonedDateTime.plusMonths(value)
        }
        val pattern = when (timeRange) {
            TimeRange.DAILY, TimeRange.WEEKLY -> "MMM dd"
            TimeRange.MONTHLY -> "MMM yy"
        }
        return date.format(DateTimeFormatter.ofPattern(pattern))
    }

    private data class ValueUnit(val value: Double, val unit: String)

    private sealed interface EngageRecord {
        val record: Record

        data class Weight(override val record: WeightRecord) : EngageRecord
        data class BloodPressure(override val record: BloodPressureRecord) : EngageRecord
        data class HeartRate(override val record: HeartRateRecord) : EngageRecord

        val zonedDateTime: ZonedDateTime
            get() = when (this) {
                is Weight -> record.time.atZone(record.zoneOffset)
                is BloodPressure -> record.time.atZone(record.zoneOffset)
                is HeartRate -> record.startTime.atZone(record.startZoneOffset)
            }

        val clientRecordId get() = record.metadata.clientRecordId

        companion object {
            fun from(record: Record) = when (record) {
                is WeightRecord -> Weight(record = record)
                is BloodPressureRecord -> BloodPressure(record = record)
                is HeartRateRecord -> HeartRate(record = record)
                else -> error("Unsupported record type ${record::javaClass.name}")
            }
        }
    }

    companion object {
        private const val DAILY_MAX_MONTHS = 1L
        private const val WEEKLY_MAX_MONTHS = 3L
        private const val MONTHLY_MAX_MONTHS = 6L

        const val ADAPTIVE_Y_VALUES_FRACTION = 1.05f

        private const val MONTH_DAY_TIME_PATTERN = "MMM dd HH:mm"
        private const val MONTH_YEAR_PATTERN = "MMM yy"
    }
}
