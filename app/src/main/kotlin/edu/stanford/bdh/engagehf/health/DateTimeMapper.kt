package edu.stanford.bdh.engagehf.health

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class DateTimeMapper @Inject constructor() {

    companion object {
        private const val DATE_FORMAT = "MM/dd/yyyy"
        private const val TIME_FORMAT = "hh:mm"
    }

    private val dateFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT)

    private val timeFormatter = DateTimeFormatter.ofPattern(TIME_FORMAT)

    fun formatDate(date: LocalDate): String {
        return date.format(dateFormatter)
    }

    fun formatTime(time: LocalTime): String {
        return time.format(timeFormatter)
    }
}
