package edu.stanford.bdh.engagehf.health

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import javax.inject.Inject

class HealthUiStateMapper @Inject constructor() {

    fun mapToHealthScreenState(
        weights: List<HealthRecord>,
        selectedTimeRange: TimeRange,
    ): HealthUiState {
        return HealthUiState(
            selectedTimeRange = selectedTimeRange,
            aggregatedRecord = aggregateWeights(weights, selectedTimeRange),
            filteredRecords = filterWeights(weights, selectedTimeRange),
            healthRecords = weights
        )
    }

    private fun filterWeights(
        weights: List<HealthRecord>,
        timeRange: TimeRange,
    ): List<HealthRecord> {
        val now = ZonedDateTime.now()
        return when (timeRange) {
            TimeRange.DAILY -> {
                weights.filter {
                    it.zonedDateTime.isAfter(now.minusDays(3))
                }
            }

            TimeRange.WEEKLY -> {
                weights.filter {
                    it.zonedDateTime.isAfter(now.minusMonths(3))
                }
            }

            TimeRange.MONTHLY -> {
                weights.filter {
                    it.zonedDateTime.isAfter(now.minusMonths(6))
                }
            }
        }
    }

    private fun aggregateWeights(
        weights: List<HealthRecord>,
        timeRange: TimeRange,
    ): List<FilteredHealthData> {
        val now = ZonedDateTime.now()
        return when (timeRange) {
            TimeRange.DAILY -> {
                val dailyAverages = weights.filter {
                    it.zonedDateTime.isAfter(now.minusDays(3))
                }.groupBy {
                    it.zonedDateTime.toLocalDate()
                }.mapValues { entry ->
                    entry.value.map { it.value }.average()
                }.map {
                    FilteredHealthData(
                        averageValue = it.value.toFloat(),
                        zonedDateTime = it.key.atStartOfDay(now.zone)
                    )
                }
                dailyAverages
            }

            TimeRange.WEEKLY -> {
                val weeklyAverages = weights.filter {
                    it.zonedDateTime.isAfter(now.minusMonths(3))
                }.groupBy {
                    it.zonedDateTime.toLocalDate().with(ChronoField.ALIGNED_WEEK_OF_YEAR, 1L)
                }.mapValues { entry ->
                    entry.value.map { it.value }.average()
                }.map {
                    FilteredHealthData(
                        averageValue = it.value.toFloat(),
                        zonedDateTime = it.key.atStartOfDay(now.zone)
                    )
                }
                weeklyAverages
            }

            TimeRange.MONTHLY -> {
                val monthlyAverages = weights.filter {
                    it.zonedDateTime.isAfter(now.minusMonths(6))
                }.groupBy {
                    Pair(it.zonedDateTime.year, it.zonedDateTime.month)
                }.mapValues { entry ->
                    entry.value.map { it.value }.average()
                }.map {
                    FilteredHealthData(
                        averageValue = it.value.toFloat(),
                        zonedDateTime = ZonedDateTime.of(
                            it.key.first,
                            it.key.second.value,
                            1,
                            0,
                            0,
                            0,
                            0,
                            now.zone
                        )
                    )
                }
                monthlyAverages
            }
        }
    }
}

data class FilteredHealthData(
    val averageValue: Float,
    val zonedDateTime: ZonedDateTime,
) {
    val formattedValue: String
        get() = "%.1f".format(averageValue)

    val formattedDate: String
        get() = zonedDateTime.format(DateTimeFormatter.ofPattern("MMM dd"))
}
