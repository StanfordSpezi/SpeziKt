package edu.stanford.spezi.module.account.views.validation

import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.module.account.views.validation.state.FailedValidationResult
import java.util.UUID

data class ValidationRule internal constructor(
    val id: UUID = UUID.randomUUID(),
    val rule: (String) -> Boolean,
    val message: StringResource,
    val effect: CascadingValidationEffect = CascadingValidationEffect.CONTINUE
) {
    companion object {
        operator fun invoke(regex: Regex, message: StringResource): ValidationRule =
            ValidationRule(rule = { regex.matchEntire(it) != null }, message = message)
        operator fun invoke(copy: ValidationRule, message: StringResource): ValidationRule =
            ValidationRule(rule = copy.rule, message = message)
    }

    val intercepting: ValidationRule
        get() = ValidationRule(id, rule, message, CascadingValidationEffect.INTERCEPT)

    override fun equals(other: Any?): Boolean =
        id == (other as? ValidationRule)?.id

    fun validate(input: String): FailedValidationResult? =
        if (rule(input)) null else FailedValidationResult(this)

    override fun hashCode(): Int = id.hashCode()
}
