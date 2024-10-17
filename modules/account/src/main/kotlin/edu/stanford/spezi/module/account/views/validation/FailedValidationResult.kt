package edu.stanford.spezi.module.account.views.validation

import java.util.UUID

data class FailedValidationResult internal constructor(
    val id: UUID,
    val message: String
) {
    companion object {
        operator fun invoke(rule: ValidationRule): FailedValidationResult =
            FailedValidationResult(rule.id, rule.message)
    }
}