package edu.stanford.spezi.core.design.validation.validation.configuration

import androidx.compose.runtime.compositionLocalOf
import edu.stanford.spezi.core.design.validation.validation.ValidationEngine
import edu.stanford.spezi.core.design.validation.validation.ValidationEngineConfiguration

val LocalValidationEngineConfiguration = compositionLocalOf {
    ValidationEngineConfiguration.noneOf(ValidationEngine.ConfigurationOption::class.java)
}
