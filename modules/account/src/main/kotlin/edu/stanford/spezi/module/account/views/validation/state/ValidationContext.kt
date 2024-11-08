package edu.stanford.spezi.module.account.views.validation.state

data class ValidationContext internal constructor(
    private val entries: List<CapturedValidationState> = emptyList()
) : Iterable<CapturedValidationState> {
    val allInputValid: Boolean get() =
        entries.all { it.engine.inputValid }

    val allValidationResults: List<FailedValidationResult> get() =
        entries.fold(emptyList()) { acc, entry -> acc + entry.engine.validationResults }

    val allDisplayedValidationResults: List<FailedValidationResult> get() =
        entries.fold(emptyList()) { acc, entry -> acc + entry.engine.displayedValidationResults }

    val isDisplayingValidationErrors: Boolean get() =
        entries.any { it.engine.isDisplayingValidationErrors }

    override fun iterator(): Iterator<CapturedValidationState> = entries.iterator()

    val isEmpty: Boolean
        get() = entries.isEmpty()

    private fun collectFailedValidations(): List<CapturedValidationState> {
        return mapNotNull { state ->
            state.runValidation()

            if (state.engine.inputValid) state else null
        }
    }

    // TODO: Originally called validateSubviews, but renamed to avoid using "view" on Android
    fun validateHierarchy(switchFocus: Boolean = true): Boolean {
        val failedFields = collectFailedValidations()

        return failedFields.firstOrNull()?.let {
            if (switchFocus) {
                it.moveFocus()
            }

            false
        } ?: true
    }
}
