package edu.stanford.spezi.core.design.views.validation.configuration

import androidx.compose.runtime.compositionLocalOf
import kotlin.time.Duration.Companion.seconds

internal val DEFAULT_VALIDATION_DEBOUNCE_DURATION = 0.5.seconds

val LocalValidationDebounce = compositionLocalOf { DEFAULT_VALIDATION_DEBOUNCE_DURATION }
