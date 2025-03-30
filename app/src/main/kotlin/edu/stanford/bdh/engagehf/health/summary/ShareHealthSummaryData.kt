package edu.stanford.bdh.engagehf.health.summary

import java.time.Instant

data class ShareHealthSummaryData(
    val url: String,
    val code: String,
    val expiresAt: Instant,
)
