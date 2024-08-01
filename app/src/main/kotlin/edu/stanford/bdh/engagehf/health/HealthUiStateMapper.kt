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
    }

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
        return records.groupBy { it ->
            when (selectedTimeRange) {
                TimeRange.DAILY -> when (it) {
                    is WeightRecord -> it.time.atZone(it.zoneOffset).toLocalDate()
                    is BloodPressureRecord -> it.time.atZone(it.zoneOffset).toLocalDate()
                    is HeartRateRecord -> it.startTime.atZone(it.startZoneOffset).toLocalDate()
                    else -> ""
                }

                TimeRange.WEEKLY -> when (it) {
                    is WeightRecord -> {
                        val with = it.time.atZone(it.zoneOffset).toLocalDate().with(
                            TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)
                        )
                        with
                    }

                    is BloodPressureRecord -> {
                        val with = it.time.atZone(it.zoneOffset).toLocalDate().with(
                            TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)
                        )
                        with
                    }

                    is HeartRateRecord -> {
                        val with = it.startTime.atZone(it.startZoneOffset).toLocalDate().with(
                            TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)
                        )
                        with
                    }

                    else -> ""
                }

                TimeRange.MONTHLY -> when (it) {
                    is WeightRecord -> it.time.atZone(it.zoneOffset)
                        .format(DateTimeFormatter.ofPattern("MMM yy"))

                    is BloodPressureRecord -> it.time.atZone(it.zoneOffset)
                        .format(DateTimeFormatter.ofPattern("MMM yy"))

                    is HeartRateRecord -> it.startTime.atZone(it.startZoneOffset)
                        .format(DateTimeFormatter.ofPattern("MMM yy"))

                    else -> ""
                }
            }
        }.mapValues { entry ->
            val averageValue = entry.value.map { getValue(it, diastolic).first }.average()
            val xValue: Float = getXValue(entry.value.first())
            Pair(averageValue.toFloat(), xValue)
        }.values.toList()
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
            is WeightRecord -> record.time.atZone(record.zoneOffset).toEpochSecond().toFloat() / 60
            is BloodPressureRecord -> record.time.atZone(record.zoneOffset).toEpochSecond()
                .toFloat() / 60

            is HeartRateRecord -> record.startTime.atZone(record.startZoneOffset).toEpochSecond()
                .toFloat() / 60

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
                formattedDate = getDate(currentRecord).format(DateTimeFormatter.ofPattern("MMM dd HH:mm")),
                xAxis = getXValue(currentRecord),
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
                }?.format(DateTimeFormatter.ofPattern("MMM dd HH:mm")) ?: "",
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
        currentUiState: HealthUiState.Success,
        newTimeRange: TimeRange,
    ): HealthUiState.Success {
        return HealthUiState.Success(
            mapToHealthData(
                records = currentUiState.data.records as List<T>,
                selectedTimeRange = newTimeRange
            )
        )
    }
}
