package edu.stanford.spezi.core.utils

import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor
import java.util.Date
import javax.inject.Inject

class DateFormatter @Inject constructor() {

    fun <T : TemporalAccessor> format(date: T, format: DateFormat): String {
        return DateTimeFormatter.ofPattern(format.pattern).format(date)
    }

    fun format(date: Date, format: DateFormat) = format(date.toInstant(), format)
}

sealed class DateFormat(open val pattern: String) {
    data object MM_DD_YYYY : DateFormat(pattern = "MM/dd/yyyy")
    data object HH_MM : DateFormat(pattern = "hh:mm")
    data class Custom(override val pattern: String) : DateFormat(pattern)
}
