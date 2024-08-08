@file:Suppress("MagicNumber", "TooManyFunctions")

package edu.stanford.bdh.engagehf.health

import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.WeightRecord
import edu.stanford.spezi.core.utils.LocaleProvider
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
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
        val filteredRecords: List<EngageRecord> =
            filterRecordsByTimeRange(records, selectedTimeRange)
        val pairs: List<Pair<Float, Float>> = groupAndMapRecords(filteredRecords, selectedTimeRange)
        val title: String = when (records.first()) {
            is EngageRecord.HeartRate -> "Heart Rate"
            is EngageRecord.BloodPressure -> "Blood Pressure"
            is EngageRecord.Weight -> "Weight"
        }
        val chartData = createAggregatedHealthData(title, pairs)

        val chartDataList = mutableListOf(chartData)
        if (records.any { it is EngageRecord.BloodPressure }) {
            val diastolicPairs: List<Pair<Float, Float>> =
                groupAndMapRecords(filteredRecords, selectedTimeRange, true)
            val diastolicChartData = createAggregatedHealthData(title, diastolicPairs)
            chartDataList.add(diastolicChartData)
        }

        val newestData: NewestHealthData? = getNewestRecord(filteredRecords)
        val tableData: List<TableEntryData> = mapTableData(filteredRecords)

        return HealthUiData(
            records = records.map { it.record },
            chartData = chartDataList,
            tableData = tableData,
            newestData = newestData,
            averageData = getAverageData(tableData),
            infoRowData = generateHealthHeaderData(selectedTimeRange, newestData)
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
        selectedTimeRange: TimeRange,
        diastolic: Boolean = false,
    ): List<Pair<Float, Float>> {
        val groupedRecords = groupRecordsByTimeRange(records, selectedTimeRange)
        return groupedRecords.values.map { entries ->
            val averageValue = entries.map { getValue(it, diastolic).value }.average()
            val xValue = entries.first().zonedDateTime.toEpochSecond().toFloat() / EPOCH_SECONDS_DIVISOR
            Pair(averageValue.toFloat(), xValue)
        }
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

    private fun filterRecordsByTimeRange(
        records: List<EngageRecord>,
        timeRange: TimeRange,
    ): List<EngageRecord> {
        val maxTime = ZonedDateTime.now().minusMonths(getMaxMonths(timeRange))
        return records.filter { it.zonedDateTime.isAfter(maxTime) }
    }

    private fun getMaxMonths(selectedTimeRange: TimeRange): Long {
        return when (selectedTimeRange) {
            TimeRange.DAILY -> DAILY_MAX_DAYS
            TimeRange.WEEKLY -> WEEKLY_MAX_MONTHS
            TimeRange.MONTHLY -> MONTHLY_MAX_MONTHS
        }
    }

    private fun createAggregatedHealthData(
        title: String,
        pairs: List<Pair<Float, Float>>,
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
                value = currentRecordValue.value.toFloat(),
                secondValue = if (currentRecord is EngageRecord.BloodPressure) {
                    getValue(currentRecord, true).value.toFloat()
                } else {
                    null
                },
                date = currentRecord.zonedDateTime,
                formattedDate = currentRecord.zonedDateTime.format(monthDayTimeFormatter),
                trend = trend.toFloat(),
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
            value = tableData.map { it.value }.average().toFloat(),
            formattedValue = "Average " + formatValue(tableData.map { it.value }.average()),
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

    private fun getDefaultLocale() = localeProvider.getDefaultLocale()

    companion object {
        private const val DAILY_MAX_DAYS = 30L
        private const val WEEKLY_MAX_MONTHS = 3L
        private const val MONTHLY_MAX_MONTHS = 6L

        const val EPOCH_SECONDS_DIVISOR = 60.0f
        const val ADAPTIVE_Y_VALUES_FRACTION = 1.05f

        private const val MONTH_DAY_TIME_PATTERN = "MMM dd HH:mm"
        private const val MONTH_YEAR_PATTERN = "MMM yy"
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
}
