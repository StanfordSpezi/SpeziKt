package edu.stanford.spezi.core.design.views.validation.state

import androidx.compose.runtime.compositionLocalOf

internal val LocalCapturedValidationStateEntries = compositionLocalOf { CapturedValidationStateEntries() }

internal data class CapturedValidationStateEntries(
    internal var entries: MutableList<CapturedValidationState> = mutableListOf()
) {
    fun add(state: CapturedValidationState) {
        entries.add(state)
    }
}
