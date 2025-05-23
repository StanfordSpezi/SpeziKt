package edu.stanford.spezi.ui.validation

import edu.stanford.spezi.ui.StringResource
import java.util.UUID

data class FailedValidationResult(
    val id: UUID,
    val message: StringResource,
) {
    // Constructors

    constructor(rule: ValidationRule) : this(
        id = rule.id,
        message = rule.message
    )

    // Overrides

    override fun equals(other: Any?) =
        (other as? FailedValidationResult)?.id == id

    override fun hashCode() =
        id.hashCode()
}
