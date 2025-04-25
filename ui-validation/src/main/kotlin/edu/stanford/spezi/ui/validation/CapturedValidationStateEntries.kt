package edu.stanford.spezi.ui.validation

import androidx.compose.runtime.compositionLocalOf

internal val LocalCapturedValidationStateEntries = compositionLocalOf { CapturedValidationStateEntries() }

internal data class CapturedValidationStateEntries(
    private var mutableEntries: MutableList<CapturedValidationState> = mutableListOf(),
) {
    val entries: List<CapturedValidationState> get() = mutableEntries

    fun add(state: CapturedValidationState) {
        mutableEntries.add(state)
    }
}
