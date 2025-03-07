package edu.stanford.spezi.spezi.validation.state

import edu.stanford.spezi.spezi.ui.resources.StringResource
import edu.stanford.spezi.spezi.validation.ValidationRule
import java.util.UUID

data class FailedValidationResult(
    val id: UUID,
    val message: StringResource,
) {
    constructor(rule: ValidationRule) : this(
        id = rule.id,
        message = rule.message
    )

    override fun equals(other: Any?) = (other as? FailedValidationResult)?.id == id
    override fun hashCode() = id.hashCode()
}
