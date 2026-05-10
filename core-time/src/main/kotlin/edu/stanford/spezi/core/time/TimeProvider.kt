package edu.stanford.spezi.core.time

import edu.stanford.spezi.core.Module
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.ZonedDateTime

class TimeProvider : Module {
    fun currentTimeMillis(): Long = nowInstant().toEpochMilli()
    fun nowInstant(): Instant = Instant.now()
    fun nowLocalTime(): LocalTime = LocalTime.now()
    fun nowZonedDateTime(): ZonedDateTime = ZonedDateTime.now()
    fun currentOffset(): ZoneOffset = ZonedDateTime.now().offset
}
