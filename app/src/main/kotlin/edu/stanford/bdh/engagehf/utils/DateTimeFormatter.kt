package edu.stanford.bdh.engagehf.utils

import edu.stanford.spezi.core.utils.DateFormat
import edu.stanford.spezi.core.utils.DateFormatter
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

class DateTimeFormatter @Inject constructor(
    private val formatter: DateFormatter,
) {
    fun format(time: LocalTime) = formatter.format(time, DateFormat.HH_MM)
    fun format(date: LocalDate) = formatter.format(date, DateFormat.MM_DD_YYYY)
}
