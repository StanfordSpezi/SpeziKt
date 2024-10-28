package edu.stanford.spezi.module.account.views.validation.configuration

import androidx.compose.runtime.compositionLocalOf
import edu.stanford.spezi.module.account.views.validation.ValidationEngine
import edu.stanford.spezi.module.account.views.validation.ValidationEngineConfiguration

val LocalValidationEngineConfiguration = compositionLocalOf {
    ValidationEngineConfiguration.noneOf(ValidationEngine.ConfigurationOption::class.java)
}
