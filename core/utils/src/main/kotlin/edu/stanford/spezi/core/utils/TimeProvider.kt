package edu.stanford.spezi.core.utils

import java.time.Instant
import javax.inject.Inject

class TimeProvider @Inject constructor() {
    fun currentTimeMillis(): Long = Instant.now().toEpochMilli()
}
