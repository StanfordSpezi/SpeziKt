package edu.stanford.bdh.engagehf.health.weight

import androidx.health.connect.client.records.WeightRecord
import edu.stanford.bdh.engagehf.health.TimeRange
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import java.util.Locale
import javax.inject.Inject

class WeightUiStateMapper @Inject constructor() {

    companion object {
        private const val DAILY_MAX_DAYS = 30L
        private const val WEEKLY_MAX_MONTHS = 3L
        private const val MONTHLY_MAX_MONTHS = 6L
    }

    fun mapToWeightUiState(
        weights: List<WeightRecord>,
        selectedTimeRange: TimeRange,
    ): WeightUiData {
        if (weights.isEmpty()) {
            return WeightUiData(
                selectedTimeRange = selectedTimeRange,
                weights = weights,
                chartWeights = emptyList(),
                tableWeights = emptyList(),
                newestWeight = null,
            )
        }
        return when (selectedTimeRange) {
            TimeRange.DAILY -> {
                mapWeightUiStateTimeRangeDaily(weights, selectedTimeRange)
            }

            TimeRange.WEEKLY -> {
                mapWeightUiStateTimeRangeWeekly(weights, selectedTimeRange)
            }

            TimeRange.MONTHLY -> {
                mapWeightUiStateTimeRangeMonthly(weights, selectedTimeRange)
            }
        }
    }

    private fun mapWeightUiStateTimeRangeMonthly(
        weights: List<WeightRecord>,
        selectedTimeRange: TimeRange,
    ): WeightUiData {
        val filteredWeights: List<WeightRecord> = weights.filter {
            it.time.atZone(it.zoneOffset)
                .isAfter(ZonedDateTime.now().minusMonths(MONTHLY_MAX_MONTHS))
        }
        var aggregatedWeights: List<WeightData> = filteredWeights.groupBy {
            Pair(it.time.atZone(it.zoneOffset).year, it.time.atZone(it.zoneOffset).month)
        }.mapValues { entry ->
            val averageWeight = entry.value.map { it.weight.inPounds }.average()
            val firstWeight = entry.value.first()
            WeightData(
                id = null,
                value = averageWeight.toFloat(),
                date = ZonedDateTime.of(
                    firstWeight.time.atZone(firstWeight.zoneOffset).year,
                    firstWeight.time.atZone(firstWeight.zoneOffset).month.value,
                    1,
                    0,
                    0,
                    0,
                    0,
                    firstWeight.time.atZone(firstWeight.zoneOffset).zone
                ),
                formattedDate = firstWeight.time.atZone(firstWeight.zoneOffset)
                    .format(DateTimeFormatter.ofPattern("MMM dd")),
                xAxis = firstWeight.time.atZone(firstWeight.zoneOffset).toEpochSecond()
                    .toFloat() / 60,
                trend = 0f,
                formattedTrend = "0.0",
                formattedValue = "0.0" // TODO
            )
        }.values.toList()

        aggregatedWeights = calculateTrend(aggregatedWeights)

        val newestWeight: WeightData? = getNewestWeight(filteredWeights)

        val tableWeights: List<WeightData> = mapTableWeights(filteredWeights)
        return WeightUiData(
            selectedTimeRange = selectedTimeRange,
            weights = weights,
            chartWeights = aggregatedWeights,
            tableWeights = tableWeights,
            newestWeight = newestWeight
        )
    }

    private fun calculateTrend(aggregatedWeights: List<WeightData>): List<WeightData> {
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

    private fun mapWeightUiStateTimeRangeWeekly(
        weights: List<WeightRecord>,
        selectedTimeRange: TimeRange,
    ): WeightUiData {
        val filteredWeights: List<WeightRecord> = weights.filter {
            it.time.atZone(it.zoneOffset)
                .isAfter(ZonedDateTime.now().minusMonths(WEEKLY_MAX_MONTHS))
        }
        var aggregatedWeights: List<WeightData> = filteredWeights.groupBy {
            it.time.atZone(it.zoneOffset).toLocalDate().with(
                ChronoField.ALIGNED_WEEK_OF_YEAR,
                it.time.atZone(it.zoneOffset).get(ChronoField.ALIGNED_WEEK_OF_YEAR).toLong()
            )
        }.mapValues { entry ->
            val averageWeight = entry.value.map { it.weight.inPounds }.average()
            val firstWeight = entry.value.first()
            WeightData(
                id = null,
                value = averageWeight.toFloat(),
                date = firstWeight.time.atZone(firstWeight.zoneOffset),
                formattedDate = firstWeight.time.atZone(firstWeight.zoneOffset)
                    .format(DateTimeFormatter.ofPattern("MMM dd")),
                xAxis = firstWeight.time.atZone(firstWeight.zoneOffset).toEpochSecond()
                    .toFloat() / 60,
                trend = 0f,
                formattedTrend = "0.0",
                formattedValue = "0.0" // TODO
            )
        }.values.toList()

        aggregatedWeights = calculateTrend(aggregatedWeights)

        val newestWeight: WeightData? = getNewestWeight(filteredWeights)

        val tableWeights: List<WeightData> = mapTableWeights(filteredWeights)

        return WeightUiData(
            selectedTimeRange = selectedTimeRange,
            weights = weights,
            chartWeights = aggregatedWeights,
            tableWeights = tableWeights,
            newestWeight = newestWeight
        )
    }

    private fun getNewestWeight(filteredWeights: List<WeightRecord>): WeightData? {
        val newestWeight: WeightData? = filteredWeights.maxByOrNull {
            it.time.atZone(it.zoneOffset)
        }?.let {
            WeightData(
                id = it.metadata.clientRecordId,
                value = it.weight.inPounds.toFloat(),
                date = it.time.atZone(it.zoneOffset),
                formattedDate = it.time.atZone(it.zoneOffset)
                    .format(DateTimeFormatter.ofPattern("MMM dd")),
                xAxis = it.time.atZone(it.zoneOffset).toEpochSecond().toFloat() / 60 / 60,
                trend = 0f,
                formattedTrend = "0.0",
                formattedValue = formatValue(it.weight.inPounds)
            )
        }
        return newestWeight
    }

    private fun mapTableWeights(filteredWeights: List<WeightRecord>): List<WeightData> {
        val tableWeights: List<WeightData> = filteredWeights.map {
            WeightData(
                id = it.metadata.clientRecordId,
                value = it.weight.inPounds.toFloat(),
                date = it.time.atZone(it.zoneOffset),
                formattedDate = it.time.atZone(it.zoneOffset)
                    .format(DateTimeFormatter.ofPattern("MMM dd")),
                xAxis = it.time.atZone(it.zoneOffset).toEpochSecond().toFloat() / 60 / 60,
                trend = 0f,
                formattedTrend = "0.0",
                formattedValue = formatValue(it.weight.inPounds)
            )
        }
        return tableWeights
    }

    private fun mapWeightUiStateTimeRangeDaily(
        weights: List<WeightRecord>,
        selectedTimeRange: TimeRange,
    ): WeightUiData {
        val filteredWeights: List<WeightRecord> = weights.filter {
            it.time.atZone(it.zoneOffset).isAfter(ZonedDateTime.now().minusDays(DAILY_MAX_DAYS))
        }
        var aggregatedWeights: List<WeightData> = filteredWeights.groupBy {
            it.time.atZone(it.zoneOffset).toLocalDate()
        }.mapValues { entry ->
            val averageWeight = entry.value.map { it.weight.inPounds }.average()
            val firstWeight = entry.value.first()
            WeightData(
                id = null,
                value = averageWeight.toFloat(),
                date = firstWeight.time.atZone(firstWeight.zoneOffset),
                formattedDate = firstWeight.time.atZone(firstWeight.zoneOffset)
                    .format(DateTimeFormatter.ofPattern("MMM dd")),
                xAxis = firstWeight.time.atZone(firstWeight.zoneOffset).toEpochSecond()
                    .toFloat(),
                trend = 0f,
                formattedTrend = "gets calculated in next step",
                formattedValue = formatValue(averageWeight)
            )
        }.values.toList()

        aggregatedWeights = calculateTrend(aggregatedWeights)

        val newestWeight: WeightData? = getNewestWeight(filteredWeights)

        val tableWeights: List<WeightData> = mapTableWeights(filteredWeights)

        return WeightUiData(
            selectedTimeRange = selectedTimeRange,
            weights = weights,
            chartWeights = aggregatedWeights,
            tableWeights = tableWeights,
            newestWeight = newestWeight
        )
    }

    private fun formatValue(averageWeight: Double) = String.format("%.1f", averageWeight) + "lbs"
}
