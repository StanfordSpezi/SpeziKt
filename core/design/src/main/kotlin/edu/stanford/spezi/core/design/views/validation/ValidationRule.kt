package edu.stanford.spezi.core.design.views.validation

import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.design.views.validation.state.FailedValidationResult
import edu.stanford.spezi.core.utils.UUID
import java.util.UUID

data class ValidationRule internal constructor(
    val id: UUID = UUID(),
    val rule: (String) -> Boolean,
    val message: StringResource,
    val effect: CascadingValidationEffect = CascadingValidationEffect.CONTINUE,
) {
    constructor(regex: Regex, message: StringResource) : this(
        rule = { regex.matchEntire(it) != null },
        message = message,
    )

    constructor(copy: ValidationRule, message: StringResource) : this(
        rule = copy.rule,
        message = message,
    )

    val intercepting: ValidationRule
        get() = ValidationRule(id, rule, message, CascadingValidationEffect.INTERCEPT)

    override fun equals(other: Any?): Boolean =
        id == (other as? ValidationRule)?.id

    fun validate(input: String): FailedValidationResult? =
        if (rule(input)) null else FailedValidationResult(this)

    override fun hashCode(): Int = id.hashCode()

    companion object
}