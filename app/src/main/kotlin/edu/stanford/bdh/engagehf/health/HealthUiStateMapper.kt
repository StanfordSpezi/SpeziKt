@file:Suppress("MagicNumber", "TooManyFunctions")

package edu.stanford.bdh.engagehf.health

import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.WeightRecord
import edu.stanford.bdh.engagehf.health.components.HealthHeaderData
import java.time.DayOfWeek
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import javax.inject.Inject

class HealthUiStateMapper<T : Record> @Inject constructor(
    private val recordClass: Class<T>,
) {
    companion object {
        private const val DAILY_MAX_DAYS = 30L
        private const val WEEKLY_MAX_MONTHS = 3L
        private const val MONTHLY_MAX_MONTHS = 6L

        const val EPOCH_SECONDS_DIVISOR = 60.0f
        const val ADAPTIVE_Y_VALUES_FRACTION = 1.05f

        private const val MONTH_DAY_TIME_PATTERN = "MMM dd HH:mm"
        private const val MONTH_YEAR_PATTERN = "MMM yy"
    }

    private val monthDayTimeFormatter = DateTimeFormatter.ofPattern(MONTH_DAY_TIME_PATTERN)

    private val monthYearFormatter = DateTimeFormatter.ofPattern(MONTH_YEAR_PATTERN)

    fun mapToHealthData(
        records: List<T>,
        selectedTimeRange: TimeRange,
    ): HealthUiData {
        if (records.isEmpty()) {
            return HealthUiData(
                records = records,
                chartData = emptyList(),
                tableData = emptyList(),
                newestData = null,
                headerData = HealthHeaderData(
                    selectedTimeRange = selectedTimeRange,
                    formattedValue = "",
                    formattedDate = "",
                    isSelectedTimeRangeDropdownExpanded = false
                )
            )
        }
        return mapUiStateTimeRange(records, selectedTimeRange)
    }

    private fun mapUiStateTimeRange(
        records: List<T>,
        selectedTimeRange: TimeRange,
    ): HealthUiData {
        val filteredRecords: List<T> = filterRecordsByTimeRange(records, selectedTimeRange)
        val pairs: List<Pair<Float, Float>> = groupAndMapRecords(filteredRecords, selectedTimeRange)
        val chartData = createAggregatedHealthData(pairs)

        val chartDataList = mutableListOf(chartData)
        if (recordClass == BloodPressureRecord::class.java) {
            val diastolicPairs: List<Pair<Float, Float>> =
                groupAndMapRecords(filteredRecords, selectedTimeRange, true)
            val diastolicChartData = createAggregatedHealthData(diastolicPairs)
            chartDataList.add(diastolicChartData)
        }

        val newestData: NewestHealthData? = getNewestRecord(filteredRecords)
        val tableData: List<TableEntryData> = mapTableData(filteredRecords)

        return HealthUiData(
            records = records,
            chartData = chartDataList,
            tableData = tableData,
            newestData = newestData,
            averageData = getAverageData(tableData),
            headerData = generateHealthHeaderData(selectedTimeRange, newestData)
        )
    }

    private fun generateHealthHeaderData(
        selectedTimeRange: TimeRange,
        newestData: NewestHealthData?,
    ): HealthHeaderData {
        return HealthHeaderData(
            selectedTimeRange = selectedTimeRange,
            formattedValue = newestData?.formattedValue ?: "",
            formattedDate = newestData?.formattedDate ?: "",
            isSelectedTimeRangeDropdownExpanded = false
        )
    }

    private fun groupAndMapRecords(
        records: List<T>,
        selectedTimeRange: TimeRange,
        diastolic: Boolean = false,
    ): List<Pair<Float, Float>> {
        val groupedRecords = groupRecordsByTimeRange(records, selectedTimeRange)
        return groupedRecords.mapValues { entry ->
            val averageValue = entry.value.map { getValue(it, diastolic).first }.average()
            val xValue: Float = getXValue(entry.value.first())
            Pair(averageValue.toFloat(), xValue)
        }.values.toList()
    }

    private fun groupRecordsByTimeRange(
        records: List<T>,
        selectedTimeRange: TimeRange,
    ): Map<Any, List<T>> {
        return records.groupBy {
            when (selectedTimeRange) {
                TimeRange.DAILY -> getDailyGroupKey(it)
                TimeRange.WEEKLY -> getWeeklyGroupKey(it)
                TimeRange.MONTHLY -> getMonthlyGroupKey(it)
            }
        }
    }

    private fun getDailyGroupKey(record: T): Any {
        return when (record) {
            is WeightRecord -> record.time.atZone(record.zoneOffset).toLocalDate()
            is BloodPressureRecord -> record.time.atZone(record.zoneOffset).toLocalDate()
            is HeartRateRecord -> record.startTime.atZone(record.startZoneOffset).toLocalDate()
            else -> ""
        }
    }

    private fun getWeeklyGroupKey(record: T): Any {
        return when (record) {
            is WeightRecord -> record.time.atZone(record.zoneOffset).toLocalDate()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

            is BloodPressureRecord -> record.time.atZone(record.zoneOffset).toLocalDate()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

            is HeartRateRecord -> record.startTime.atZone(record.startZoneOffset).toLocalDate()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

            else -> ""
        }
    }

    private fun getMonthlyGroupKey(record: T): Any {
        return when (record) {
            is WeightRecord -> record.time.atZone(record.zoneOffset)
                .format(monthYearFormatter)

            is BloodPressureRecord -> record.time.atZone(record.zoneOffset)
                .format(monthYearFormatter)

            is HeartRateRecord -> record.startTime.atZone(record.startZoneOffset)
                .format(monthYearFormatter)

            else -> ""
        }
    }

    private fun getValue(record: Record, diastolic: Boolean = false): Pair<Double, String> {
        return when (record) {
            is WeightRecord -> Pair(record.weight.inPounds, "lbs")
            is BloodPressureRecord -> if (diastolic) {
                Pair(
                    record.diastolic.inMillimetersOfMercury,
                    "mmHg"
                )
            } else {
                Pair(record.systolic.inMillimetersOfMercury, "mmHg")
            }

            is HeartRateRecord -> Pair(record.samples.first().beatsPerMinute.toDouble(), "bpm")
            else -> Pair(0.0, "")
        }
    }

    private fun getBloodPressureFormatRecord(record: BloodPressureRecord): String {
        return String.format(
            Locale.US,
            "%.0f",
            record.systolic.inMillimetersOfMercury
        ) + "/" + String.format(
            Locale.US,
            "%.0f",
            record.diastolic.inMillimetersOfMercury
        ) + " mmHg"
    }

    private fun formatValue(value: Double, unit: String = ""): String {
        return String.format(Locale.US, "%.1f", value) + " " + unit
    }

    private fun filterRecordsByTimeRange(records: List<T>, timeRange: TimeRange): List<T> {
        return records.filter {
            when (it) {
                is WeightRecord -> it.time.atZone(it.zoneOffset)
                    .isAfter(ZonedDateTime.now().minusMonths(getMaxMonths(timeRange)))

                is BloodPressureRecord -> it.time.atZone(it.zoneOffset)
                    .isAfter(ZonedDateTime.now().minusMonths(getMaxMonths(timeRange)))

                is HeartRateRecord -> it.startTime.atZone(it.startZoneOffset)
                    .isAfter(ZonedDateTime.now().minusMonths(getMaxMonths(timeRange)))

                else -> false
            }
        }
    }

    private fun getMaxMonths(selectedTimeRange: TimeRange): Long {
        return when (selectedTimeRange) {
            TimeRange.DAILY -> DAILY_MAX_DAYS
            TimeRange.WEEKLY -> WEEKLY_MAX_MONTHS
            TimeRange.MONTHLY -> MONTHLY_MAX_MONTHS
        }
    }

    private fun createAggregatedHealthData(pairs: List<Pair<Float, Float>>): AggregatedHealthData {
        val seriesName = when (recordClass) {
            WeightRecord::class.java -> "Weight"
            BloodPressureRecord::class.java -> "Blood Pressure"
            HeartRateRecord::class.java -> "Heart Rate"
            else -> ""
        }
        return AggregatedHealthData(
            yValues = pairs.map { it.first },
            xValues = pairs.map { it.second },
            seriesName = seriesName
        )
    }

    private fun getXValue(record: T): Float {
        return when (record) {
            is WeightRecord -> record.time.atZone(record.zoneOffset).toEpochSecond()
                .toFloat() / EPOCH_SECONDS_DIVISOR

            is BloodPressureRecord -> record.time.atZone(record.zoneOffset).toEpochSecond()
                .toFloat() / EPOCH_SECONDS_DIVISOR

            is HeartRateRecord -> record.startTime.atZone(record.startZoneOffset).toEpochSecond()
                .toFloat() / EPOCH_SECONDS_DIVISOR

            else -> 0.0F
        }
    }

    private fun mapTableData(records: List<T>): List<TableEntryData> {
        if (records.isEmpty()) return emptyList()

        val tableData = mutableListOf<TableEntryData>()
        var previousRecord: T? = null

        records.forEach { currentRecord ->
            val trend = previousRecord?.let {
                getValue(it).first - getValue(currentRecord).first
            } ?: 0.0

            val formattedTrend = when {
                trend > 0 -> "▲${String.format(Locale.US, "%.0f", trend)}"
                trend < 0 -> "▼${String.format(Locale.US, "%.0f", trend)}"
                else -> "▶${String.format(Locale.US, "%.0f", trend)}"
            } + getValue(currentRecord).second

            val tableEntryData = TableEntryData(
                id = currentRecord.metadata.clientRecordId,
                value = getValue(currentRecord).first.toFloat(),
                secondValue = if (currentRecord is BloodPressureRecord) {
                    getValue(currentRecord, true).first.toFloat()
                } else {
                    null
                },
                date = getDate(currentRecord),
                formattedDate = getDate(currentRecord).format(monthDayTimeFormatter),
                trend = trend.toFloat(),
                formattedTrend = formattedTrend,
                formattedValues = if (currentRecord is BloodPressureRecord) {
                    getBloodPressureFormatRecord(currentRecord)
                } else {
                    formatValue(getValue(currentRecord).first, getValue(currentRecord).second)
                },
            )

            tableData.add(tableEntryData)
            previousRecord = currentRecord
        }

        return tableData
    }

    private fun getAverageData(tableData: List<TableEntryData>) =
        AverageHealthData(
            value = tableData.map { it.value }.average().toFloat(),
            formattedValue = "Average " + formatValue(tableData.map { it.value }.average()),
        )

    private fun getNewestRecord(records: List<T>): NewestHealthData? {
        val newestRecord = records.maxByOrNull {
            when (it) {
                is WeightRecord -> it.time.atZone(it.zoneOffset).toEpochSecond()
                is BloodPressureRecord -> it.time.atZone(it.zoneOffset).toEpochSecond()
                is HeartRateRecord -> it.startTime.atZone(it.startZoneOffset).toEpochSecond()
                else -> Long.MIN_VALUE
            }
        }

        return newestRecord?.let {
            NewestHealthData(
                formattedDate = when (it) {
                    is WeightRecord -> it.time.atZone(it.zoneOffset)
                    is BloodPressureRecord -> it.time.atZone(it.zoneOffset)
                    is HeartRateRecord -> it.startTime.atZone(it.startZoneOffset)
                    else -> null
                }?.format(monthDayTimeFormatter) ?: "",
                formattedValue = if (it is BloodPressureRecord) {
                    getBloodPressureFormatRecord(it)
                } else {
                    formatValue(getValue(it).first, getValue(it).second)
                }
            )
        }
    }

    private fun getDate(record: T): ZonedDateTime {
        return when (record) {
            is WeightRecord -> record.time.atZone(record.zoneOffset)
            is BloodPressureRecord -> record.time.atZone(record.zoneOffset)
            is HeartRateRecord -> record.startTime.atZone(record.startZoneOffset)
            else -> ZonedDateTime.now()
        }
    }

    fun updateTimeRange(
        currentUiState: HealthUiState,
        newTimeRange: TimeRange,
    ): HealthUiState {
        return when (currentUiState) {
            is HealthUiState.Loading -> currentUiState
            is HealthUiState.Error -> currentUiState
            is HealthUiState.Success -> {
                HealthUiState.Success(
                    mapToHealthData(
                        records = currentUiState.data.records as List<T>,
                        selectedTimeRange = newTimeRange
                    )
                )
            }
        }
    }

    fun mapToggleTimeRange(
        healthAction: HealthAction.ToggleTimeRangeDropdown,
        uiState: HealthUiState,
    ) =
        when (uiState) {
            is HealthUiState.Loading -> uiState
            is HealthUiState.Error -> uiState
            is HealthUiState.Success -> {
                uiState.copy(
                    data = uiState.data.copy(
                        headerData = uiState.data.headerData.copy(
                            isSelectedTimeRangeDropdownExpanded = healthAction.expanded
                        )
                    )
                )
            }
        }
}
