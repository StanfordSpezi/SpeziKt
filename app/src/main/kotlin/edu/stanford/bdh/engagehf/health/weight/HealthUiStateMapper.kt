@file:Suppress("MagicNumber")

package edu.stanford.bdh.engagehf.health.weight

import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.WeightRecord
import edu.stanford.bdh.engagehf.health.TimeRange
import edu.stanford.bdh.engagehf.health.components.HealthHeaderData
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import java.util.Locale
import javax.inject.Inject

class HealthUiStateMapper @Inject constructor() {

    companion object {
        private const val DAILY_MAX_DAYS = 30L
        private const val WEEKLY_MAX_MONTHS = 3L
        private const val MONTHLY_MAX_MONTHS = 6L
    }

    fun mapToHealthData(
        records: List<Record>,
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
        return when (selectedTimeRange) {
            TimeRange.DAILY -> {
                mapUiStateTimeRangeDaily(records, selectedTimeRange)
            }

            TimeRange.WEEKLY -> {
                mapUiStateTimeRangeWeekly(records, selectedTimeRange)
            }

            TimeRange.MONTHLY -> {
                mapUiStateTimeRangeMonthly(records, selectedTimeRange)
            }
        }
    }

    private fun mapUiStateTimeRangeMonthly(
        records: List<Record>,
        selectedTimeRange: TimeRange,
    ): HealthUiData {
        val filteredRecords: List<Record> = records.filter {
            when (it) {
                is WeightRecord -> it.time.atZone(it.zoneOffset)
                    .isAfter(ZonedDateTime.now().minusMonths(MONTHLY_MAX_MONTHS))

                is BloodPressureRecord -> it.time.atZone(it.zoneOffset)
                    .isAfter(ZonedDateTime.now().minusMonths(MONTHLY_MAX_MONTHS))

                is HeartRateRecord -> it.startTime.atZone(it.startZoneOffset)
                    .isAfter(ZonedDateTime.now().minusMonths(MONTHLY_MAX_MONTHS))

                else -> false
            }
        }
        val pairs: List<Pair<Float, Float>> = filteredRecords.groupBy {
            when (it) {
                is WeightRecord -> it.time.atZone(it.zoneOffset)
                    .format(DateTimeFormatter.ofPattern("MMM yy"))

                is BloodPressureRecord -> it.time.atZone(it.zoneOffset)
                    .format(DateTimeFormatter.ofPattern("MMM yy"))

                is HeartRateRecord -> it.startTime.atZone(it.startZoneOffset)
                    .format(DateTimeFormatter.ofPattern("MMM yy"))

                else -> ""
            }
        }.mapValues { entry ->
            val averageValue = entry.value.map { getValue(it).first }.average()
            val xValue: Float = when (val firstRecord = entry.value.first()) {
                is WeightRecord -> firstRecord.time.atZone(firstRecord.zoneOffset).toEpochSecond()
                    .toFloat() / 60

                is BloodPressureRecord -> firstRecord.time.atZone(firstRecord.zoneOffset)
                    .toEpochSecond()
                    .toFloat() / 60

                is HeartRateRecord -> firstRecord.startTime.atZone(firstRecord.startZoneOffset)
                    .toEpochSecond()
                    .toFloat() / 60

                else -> 0.0F
            }
            Pair(averageValue.toFloat(), xValue)
        }.values.toList()
        val aggregatedWeights = AggregatedHealthData(
            yValues = pairs.map { pair -> pair.first }.toList(),
            xValues = pairs.map { pair -> pair.second }.toList(),
            seriesName = "" // TODO
        )

        val newestWeight: NewestHealthData? = getNewestRecord(filteredRecords)

        var tableWeights: List<TableEntryData> = mapTableWeights(filteredRecords)
        tableWeights = calculateTrend(tableWeights)
        return HealthUiData(
            records = records,
            chartData = listOf(aggregatedWeights), // TODO in case of blood pressure 2
            tableData = tableWeights,
            newestData = newestWeight,
            averageData = getAverageWeight(tableWeights),
            headerData = HealthHeaderData(
                selectedTimeRange = selectedTimeRange,
                formattedValue = "",
                formattedDate = "",
                isSelectedTimeRangeDropdownExpanded = false
            )
        )
    }

    private fun calculateTrend(aggregatedWeights: List<TableEntryData>): List<TableEntryData> {
        // TODO we need to know which kind of record we work with!
        return aggregatedWeights.mapIndexed { index, weightData ->
            if (index > 0) {
                val previousWeight = aggregatedWeights[index - 1]
                val trend = weightData.value - previousWeight.value
                weightData.copy(
                    trend = trend,
                    formattedTrend = if (trend > 0) {
                        "▲${String.format(Locale.getDefault(), "%.1f", trend)}" + "lbs"
                    } else {
                        "▼" + String.format(Locale.getDefault(), "%.1f", trend) + "lbs"
                    }
                )
            } else {
                weightData
            }
        }
    }

    private fun mapUiStateTimeRangeWeekly(
        records: List<Record>,
        selectedTimeRange: TimeRange,
    ): HealthUiData {
        val filteredRecords: List<Record> = records.filter {
            when (it) {
                is WeightRecord -> it.time.atZone(it.zoneOffset)
                    .isAfter(ZonedDateTime.now().minusMonths(WEEKLY_MAX_MONTHS))

                is BloodPressureRecord -> it.time.atZone(it.zoneOffset)
                    .isAfter(ZonedDateTime.now().minusMonths(WEEKLY_MAX_MONTHS))

                is HeartRateRecord -> it.startTime.atZone(it.startZoneOffset)
                    .isAfter(ZonedDateTime.now().minusMonths(WEEKLY_MAX_MONTHS))

                else -> false
            }
        }
        val pairs: List<Pair<Float, Float>> = filteredRecords.groupBy {
            when (it) {
                is WeightRecord -> it.time.atZone(it.zoneOffset).toLocalDate().with(
                    ChronoField.ALIGNED_WEEK_OF_YEAR,
                    it.time.atZone(it.zoneOffset).get(ChronoField.ALIGNED_WEEK_OF_YEAR).toLong()
                )

                is BloodPressureRecord -> it.time.atZone(it.zoneOffset).toLocalDate().with(
                    ChronoField.ALIGNED_WEEK_OF_YEAR,
                    it.time.atZone(it.zoneOffset).get(ChronoField.ALIGNED_WEEK_OF_YEAR).toLong()
                )

                is HeartRateRecord -> it.startTime.atZone(it.startZoneOffset).toLocalDate().with(
                    ChronoField.ALIGNED_WEEK_OF_YEAR,
                    it.startTime.atZone(it.startZoneOffset).get(ChronoField.ALIGNED_WEEK_OF_YEAR)
                        .toLong()
                )

                else -> false
            }
        }.mapValues { entry ->
            val averageValue = entry.value.map { getValue(it).first }.average()
            val xValue: Float = when (val firstRecord = entry.value.first()) {
                is WeightRecord -> firstRecord.time.atZone(firstRecord.zoneOffset).toEpochSecond()
                    .toFloat() / 60

                is BloodPressureRecord -> firstRecord.time.atZone(firstRecord.zoneOffset)
                    .toEpochSecond()
                    .toFloat() / 60

                is HeartRateRecord -> firstRecord.startTime.atZone(firstRecord.startZoneOffset)
                    .toEpochSecond()
                    .toFloat() / 60

                else -> 0.0F
            }
            Pair(averageValue.toFloat(), xValue)
        }.values.toList()
        val aggregatedWeights = AggregatedHealthData(
            yValues = pairs.map { pair -> pair.first }.toList(),
            xValues = pairs.map { pair -> pair.second }.toList(),
            seriesName = "" // TODO
        )

        val newestWeight: NewestHealthData? = getNewestRecord(filteredRecords)

        var tableWeights: List<TableEntryData> = mapTableWeights(filteredRecords)
        tableWeights = calculateTrend(tableWeights)

        return HealthUiData(
            records = records,
            chartData = listOf(aggregatedWeights),
            tableData = tableWeights,
            newestData = newestWeight,
            averageData = getAverageWeight(tableWeights),
            headerData = HealthHeaderData(
                selectedTimeRange = selectedTimeRange,
                formattedValue = "",
                formattedDate = "",
                isSelectedTimeRangeDropdownExpanded = false
            )
        )
    }

    private fun getAverageWeight(aggregatedWeights: List<TableEntryData>) =
        AverageHealthData(
            value = aggregatedWeights.map { it.value }.average().toFloat(),
            formattedValue = "Average " + formatValue(aggregatedWeights.map { it.value }.average()),
        )

    private fun getNewestRecord(filteredRecords: List<Record>): NewestHealthData? {
        val newestRecord = filteredRecords.maxByOrNull {
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
                    formatValue(
                        getValue(it).first,
                        getValue(it).second
                    )
                }
            )
        }
    }

    private fun mapTableWeights(filteredRecords: List<Record>): List<TableEntryData> {
        if (filteredRecords.isEmpty()) return emptyList()

        val tableWeights = mutableListOf<TableEntryData>()
        var previousRecord: Record? = null

        filteredRecords.forEach { currentRecord ->
            val trend = previousRecord?.let {
                getValue(currentRecord).first - getValue(it).first
            } ?: 0.0

            val formattedTrend = when {
                trend > 0 -> "▲${String.format(Locale.US, "%.1f", trend)}"
                trend < 0 -> "▼${String.format(Locale.US, "%.1f", trend)}"
                else -> "▶${String.format(Locale.US, "%.1f", trend)}"
            }

            val tableEntryData = TableEntryData(
                id = currentRecord.metadata.clientRecordId,
                value = getValue(currentRecord).first.toFloat(),
                date = when (currentRecord) {
                    is WeightRecord -> currentRecord.time.atZone(currentRecord.zoneOffset)
                    is BloodPressureRecord -> currentRecord.time.atZone(currentRecord.zoneOffset)
                    is HeartRateRecord -> currentRecord.startTime.atZone(currentRecord.startZoneOffset)
                    else -> ZonedDateTime.now() // Default value, should not happen
                },
                formattedDate = when (currentRecord) {
                    is WeightRecord -> currentRecord.time.atZone(currentRecord.zoneOffset)
                    is BloodPressureRecord -> currentRecord.time.atZone(currentRecord.zoneOffset)
                    is HeartRateRecord -> currentRecord.startTime.atZone(currentRecord.startZoneOffset)
                    else -> ZonedDateTime.now() // Default value, should not happen
                }.format(DateTimeFormatter.ofPattern("MMM dd HH:mm")),
                xAxis = when (currentRecord) {
                    is WeightRecord -> currentRecord.time.atZone(currentRecord.zoneOffset)
                        .toEpochSecond().toFloat() / 60

                    is BloodPressureRecord -> currentRecord.time.atZone(currentRecord.zoneOffset)
                        .toEpochSecond().toFloat() / 60

                    is HeartRateRecord -> currentRecord.startTime.atZone(currentRecord.startZoneOffset)
                        .toEpochSecond().toFloat() / 60

                    else -> 0.0F // Default value, should not happen
                },
                trend = trend.toFloat(),
                formattedTrend = formattedTrend,
                formattedValue = if (currentRecord is BloodPressureRecord) {
                    getBloodPressureFormatRecord(
                        currentRecord
                    )
                } else {
                    formatValue(
                        getValue(currentRecord).first,
                        getValue(currentRecord).second
                    )
                },
            )

            tableWeights.add(tableEntryData)
            previousRecord = currentRecord
        }

        return tableWeights
    }

    private fun mapUiStateTimeRangeDaily(
        records: List<Record>,
        selectedTimeRange: TimeRange,
    ): HealthUiData {
        val filteredRecords: List<Record> = records.filter { record ->
            when (record) {
                is WeightRecord -> record.time.atZone(record.zoneOffset)
                    .isAfter(ZonedDateTime.now().minusDays(DAILY_MAX_DAYS))

                is BloodPressureRecord -> record.time.atZone(record.zoneOffset)
                    .isAfter(ZonedDateTime.now().minusDays(DAILY_MAX_DAYS))

                is HeartRateRecord -> record.startTime.atZone(record.startZoneOffset)
                    .isAfter(ZonedDateTime.now().minusDays(DAILY_MAX_DAYS))

                else -> false
            }
        }

        val pairs: List<Pair<Float, Float>> = filteredRecords.groupBy {
            when (it) {
                is WeightRecord -> it.time.atZone(it.zoneOffset).toLocalDate()
                is BloodPressureRecord -> it.time.atZone(it.zoneOffset).toLocalDate()
                is HeartRateRecord -> it.startTime.atZone(it.startZoneOffset).toLocalDate()
                else -> null
            }
        }.mapValues { entry ->
            val averageValue = entry.value.map { getValue(it).first }.average()
            val xValue: Float = when (val firstRecord = entry.value.first()) {
                is WeightRecord -> firstRecord.time.atZone(firstRecord.zoneOffset).toEpochSecond()
                    .toFloat() / 60

                is BloodPressureRecord -> firstRecord.time.atZone(firstRecord.zoneOffset)
                    .toEpochSecond().toFloat() / 60

                is HeartRateRecord -> firstRecord.startTime.atZone(firstRecord.startZoneOffset)
                    .toEpochSecond().toFloat() / 60

                else -> 0.0F
            }
            Pair(averageValue.toFloat(), xValue)
        }.values.toList()

        println("Size of Pairs: ${pairs.size}")
        val aggregatedWeights = AggregatedHealthData(
            yValues = pairs.map { pair -> pair.first }.toList(),
            xValues = pairs.map { pair -> pair.second }.toList(),
            seriesName = "" // TODO
        )

        val newestWeight: NewestHealthData? = getNewestRecord(filteredRecords)

        var tableWeights: List<TableEntryData> = mapTableWeights(filteredRecords)
        tableWeights = calculateTrend(tableWeights)

        return HealthUiData(
            records = records,
            chartData = listOf(aggregatedWeights),
            tableData = tableWeights,
            newestData = newestWeight,
            averageData = getAverageWeight(tableWeights),
            headerData = HealthHeaderData(
                selectedTimeRange = selectedTimeRange,
                formattedValue = "",
                formattedDate = "",
                isSelectedTimeRangeDropdownExpanded = false
            )
        )
    }

    private fun getValue(record: Record): Pair<Double, String> {
        return when (record) {
            is WeightRecord -> Pair(record.weight.inPounds, "lbs")
            is BloodPressureRecord -> Pair(record.systolic.inMillimetersOfMercury, "mmHg")
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
}
