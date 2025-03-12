package edu.stanford.spezi.ui.validation.internal

import androidx.compose.runtime.compositionLocalOf
import edu.stanford.spezi.ui.validation.CapturedValidationState

internal val LocalCapturedValidationStates =
    compositionLocalOf { mutableListOf<CapturedValidationState>() }
