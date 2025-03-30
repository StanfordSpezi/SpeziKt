package edu.stanford.bdh.engagehf.health.summary

import androidx.compose.ui.graphics.ImageBitmap

data class ShareHealthSummary(
    val qrCodeBitmap: ImageBitmap,
    val oneTimeCode: String,
)
