package edu.stanford.spezi.core.design.views.validation.state

import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.design.views.validation.ValidationRule
import java.util.UUID

data class FailedValidationResult(
    val id: UUID,
    val message: StringResource
) {
    companion object {
        operator fun invoke(rule: ValidationRule) =
            FailedValidationResult(rule.id, rule.message)
    }

    override fun equals(other: Any?) = (other as? FailedValidationResult)?.id == id
    override fun hashCode() = id.hashCode()
}
