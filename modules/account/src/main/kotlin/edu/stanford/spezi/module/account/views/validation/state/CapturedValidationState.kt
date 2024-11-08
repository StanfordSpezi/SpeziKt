package edu.stanford.spezi.module.account.views.validation.state

import androidx.compose.runtime.MutableState
import edu.stanford.spezi.module.account.views.validation.ValidationEngine

data class CapturedValidationState internal constructor(
    internal val engine: ValidationEngine,
    private val input: String,
    private val isFocused: MutableState<Boolean>,
) {
    internal fun moveFocus() {
        isFocused.value = true
    }

    fun runValidation() {
        engine.runValidation(input)
    }

    // TODO: Find out whether we can dynamically expose members of ValidationEngine

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun equals(other: Any?): Boolean =
        (other as? CapturedValidationState)?.let {
            it.engine === engine && it.input == input
        } ?: false
}
