package edu.stanford.spezi.core.design.views.validation.configuration

import androidx.compose.runtime.compositionLocalOf
import edu.stanford.spezi.core.design.views.validation.ValidationEngine
import edu.stanford.spezi.core.design.views.validation.ValidationEngineConfiguration

val LocalValidationEngineConfiguration = compositionLocalOf {
    ValidationEngineConfiguration.noneOf(ValidationEngine.ConfigurationOption::class.java)
}
