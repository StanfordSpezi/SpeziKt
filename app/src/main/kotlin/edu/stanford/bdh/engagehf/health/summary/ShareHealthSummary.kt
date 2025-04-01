package edu.stanford.bdh.engagehf.health.summary

import androidx.compose.ui.graphics.ImageBitmap
import java.time.Instant

data class ShareHealthSummary(
    val qrCodeBitmap: ImageBitmap,
    val oneTimeCode: String,
    val expiresAt: Instant,
)
