package edu.stanford.spezi.ui.validation

import androidx.compose.runtime.compositionLocalOf

val LocalValidationEngine =
    compositionLocalOf<ValidationEngine?> { null }

val LocalValidationEngineConfiguration =
    compositionLocalOf { ValidationEngineConfiguration.noneOf(ValidationEngine.ConfigurationOption::class.java) }
