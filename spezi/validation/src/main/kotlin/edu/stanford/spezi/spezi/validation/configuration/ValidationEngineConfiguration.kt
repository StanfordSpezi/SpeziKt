package edu.stanford.spezi.spezi.validation.configuration

import androidx.compose.runtime.compositionLocalOf
import edu.stanford.spezi.spezi.validation.ValidationEngine
import edu.stanford.spezi.spezi.validation.ValidationEngineConfiguration
import kotlin.time.Duration.Companion.milliseconds

internal val DEFAULT_VALIDATION_DEBOUNCE_DURATION = 150.milliseconds

val LocalValidationEngineConfiguration = compositionLocalOf {
    ValidationEngineConfiguration.noneOf(ValidationEngine.ConfigurationOption::class.java)
}
