package edu.stanford.spezi.ui.validation

import edu.stanford.spezi.foundation.UUID
import edu.stanford.spezi.ui.StringResource
import java.util.UUID

enum class CascadingValidationEffect {
    CONTINUE, INTERCEPT
}

data class ValidationRule(
    val rule: (String) -> Boolean,
    val message: StringResource,
    val effect: CascadingValidationEffect = CascadingValidationEffect.CONTINUE,
) {
    companion object

    // Properties

    @Suppress("detekt:VariableMinLength")
    internal val id: UUID = UUID()

    // Constructor

    constructor(regex: Regex, message: StringResource) : this(
        rule = { regex.matchEntire(it) != null },
        message = message,
    )

    // Overrides

    override fun equals(other: Any?) =
        id == (other as? ValidationRule)?.id

    override fun hashCode() =
        id.hashCode()

    // Methods

    fun validate(input: String): FailedValidationResult? =
        if (rule(input)) null else FailedValidationResult(this)
}
