package edu.stanford.spezi.spezi.validation.state

data class ValidationContext internal constructor(
    private val entries: List<CapturedValidationState> = emptyList(),
) : Iterable<CapturedValidationState> {
    val allInputValid: Boolean get() =
        entries.all { it.inputValid }

    val allValidationResults: List<FailedValidationResult> get() =
        entries.fold(emptyList()) { acc, entry -> acc + entry.validationResults }

    val allDisplayedValidationResults: List<FailedValidationResult> get() =
        entries.fold(emptyList()) { acc, entry -> acc + entry.displayedValidationResults }

    val isDisplayingValidationErrors: Boolean get() =
        entries.any { it.isDisplayingValidationErrors }

    override fun iterator(): Iterator<CapturedValidationState> = entries.iterator()

    val isEmpty: Boolean
        get() = entries.isEmpty()

    private fun collectFailedValidations(): List<CapturedValidationState> {
        return mapNotNull { state ->
            state.runValidation()

            if (!state.inputValid) state else null
        }
    }

    fun validateHierarchy(switchFocus: Boolean = true): Boolean {
        val failedFields = collectFailedValidations()

        return failedFields.firstOrNull()?.let {
            if (switchFocus) {
                it.moveFocus()
            }

            false
        } ?: true
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun equals(other: Any?): Boolean =
        (other as? ValidationContext)?.entries == entries
}
