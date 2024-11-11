package edu.stanford.spezi.core.design.views.validation.configuration

import androidx.compose.runtime.compositionLocalOf
import edu.stanford.spezi.core.design.validation.validation.ValidationEngine

val LocalValidationEngine = compositionLocalOf<ValidationEngine?> { null }
