package edu.stanford.spezi.ui.validation

import androidx.compose.runtime.MutableState

data class CapturedValidationState internal constructor(
    private val engine: ValidationEngine,
    private val input: String,
    private val isFocused: MutableState<Boolean>,
) : ValidationEngine by engine {
    internal fun moveFocus() {
        isFocused.value = true
    }

    fun runValidation() {
        engine.runValidation(input)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun equals(other: Any?): Boolean =
        (other as? CapturedValidationState)?.let {
            it.engine === engine && it.input == input
        } ?: false
}
