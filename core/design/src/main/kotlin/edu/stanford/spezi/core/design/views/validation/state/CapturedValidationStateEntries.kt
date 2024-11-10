package edu.stanford.spezi.core.design.views.validation.state

import androidx.compose.runtime.compositionLocalOf

internal val LocalCapturedValidationStateEntries = compositionLocalOf { CapturedValidationStateEntries() }

internal data class CapturedValidationStateEntries(
    private var _entries: MutableList<CapturedValidationState> = mutableListOf()
) {
    val entries: List<CapturedValidationState> get() = _entries

    fun add(state: CapturedValidationState) {
        _entries.add(state)
    }
}
