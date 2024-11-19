package edu.stanford.spezi.core.design.views.validation.configuration

import androidx.compose.runtime.compositionLocalOf
import edu.stanford.spezi.core.design.views.validation.ValidationEngine
import edu.stanford.spezi.core.design.views.validation.ValidationEngineConfiguration
import kotlin.time.Duration.Companion.milliseconds

internal val DEFAULT_VALIDATION_DEBOUNCE_DURATION = 150.milliseconds

val LocalValidationEngineConfiguration = compositionLocalOf {
    ValidationEngineConfiguration.noneOf(ValidationEngine.ConfigurationOption::class.java)
}
