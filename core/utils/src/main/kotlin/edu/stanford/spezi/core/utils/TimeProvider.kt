package edu.stanford.spezi.core.utils

import javax.inject.Inject

class TimeProvider @Inject constructor() {
    fun currentTimeMillis(): Long = System.currentTimeMillis()
}
