package edu.stanford.bdh.engagehf.health.time

import java.time.Instant
import java.time.LocalTime

data class TimePickerState(
    val selectedDate: Instant,
    val selectedTime: LocalTime,
    val initialHour: Int,
    val initialMinute: Int,
    val selectedDateFormatted: String,
    val selectedTimeFormatted: String,
)
