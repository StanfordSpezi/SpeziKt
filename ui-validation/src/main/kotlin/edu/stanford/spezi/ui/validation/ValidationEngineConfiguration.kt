package edu.stanford.spezi.ui.validation

import androidx.compose.runtime.compositionLocalOf
import kotlin.time.Duration.Companion.milliseconds

internal val DEFAULT_VALIDATION_DEBOUNCE_DURATION = 150.milliseconds

val LocalValidationEngineConfiguration = compositionLocalOf {
    ValidationEngineConfiguration.noneOf(ValidationEngine.ConfigurationOption::class.java)
}
