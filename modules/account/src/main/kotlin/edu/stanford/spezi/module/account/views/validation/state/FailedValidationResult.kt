package edu.stanford.spezi.module.account.views.validation.state

import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.module.account.views.validation.FailedValidationResult
import edu.stanford.spezi.module.account.views.validation.ValidationRule
import java.util.UUID

data class FailedValidationResult(
    val id: UUID,
    val message: StringResource
) {
    companion object {
        operator fun invoke(rule: ValidationRule) =
            FailedValidationResult(rule.id, rule.message)
    }
}