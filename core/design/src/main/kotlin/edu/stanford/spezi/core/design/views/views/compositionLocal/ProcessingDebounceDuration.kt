package edu.stanford.spezi.core.design.views.views.compositionLocal

import androidx.compose.runtime.compositionLocalOf
import kotlin.time.Duration.Companion.milliseconds

val LocalProcessingDebounceDuration = compositionLocalOf { 150.milliseconds }
