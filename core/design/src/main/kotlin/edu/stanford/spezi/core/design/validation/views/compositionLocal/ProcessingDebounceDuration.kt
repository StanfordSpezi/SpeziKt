package edu.stanford.spezi.core.design.validation.views.compositionLocal

import androidx.compose.runtime.compositionLocalOf
import kotlin.time.Duration.Companion.milliseconds

val LocalProcessingDebounceDuration = compositionLocalOf { 150.milliseconds }
