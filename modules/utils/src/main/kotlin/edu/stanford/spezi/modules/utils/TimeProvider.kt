package edu.stanford.spezi.modules.utils

import java.time.Instant
import java.time.LocalTime
import javax.inject.Inject

class TimeProvider @Inject constructor() {
    fun currentTimeMillis(): Long = nowInstant().toEpochMilli()
    fun nowInstant() = Instant.now()
    fun nowLocalTime() = LocalTime.now()
}
