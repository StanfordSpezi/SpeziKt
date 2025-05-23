package edu.stanford.spezi.ui.validation

data class ValidationContext(
    private val entries: List<CapturedValidationState> = emptyList(),
) : Iterable<CapturedValidationState> {
    // Properties

    val allInputValid: Boolean get() =
        entries.all { it.engine.inputValid }

    val allValidationResults: List<FailedValidationResult> get() =
        entries.fold(emptyList()) { acc, entry -> acc + entry.engine.validationResults }

    val allDisplayedValidationResults: List<FailedValidationResult> get() =
        entries.fold(emptyList()) { acc, entry -> acc + entry.engine.displayedValidationResults }

    val isDisplayingValidationErrors: Boolean get() =
        entries.any { it.engine.isDisplayingValidationErrors }

    val isEmpty: Boolean
        get() = entries.isEmpty()

    // Overrides

    override fun iterator() =
        entries.iterator()

    override fun equals(other: Any?) =
        (other as? ValidationContext)?.entries == entries

    override fun hashCode() =
        super.hashCode()

    // Methods

    fun validateHierarchy(switchFocus: Boolean = true): Boolean =
        collectFailedValidations().firstOrNull()?.let {
            if (switchFocus) {
                it.requestFocus()
            }
            false
        } != false

    // Helpers

    private fun collectFailedValidations() = mapNotNull { state ->
        state.runValidation()
        if (!state.engine.inputValid) state else null
    }
}
