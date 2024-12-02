package edu.stanford.spezi.core.design.views.validation.configuration

import androidx.compose.runtime.compositionLocalOf
import edu.stanford.spezi.core.design.views.validation.ValidationEngine

internal val LocalValidationEngine = compositionLocalOf<ValidationEngine?> { null }
