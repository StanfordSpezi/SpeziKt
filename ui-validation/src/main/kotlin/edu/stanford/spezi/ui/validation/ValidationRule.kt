package edu.stanford.spezi.ui.validation

import edu.stanford.spezi.foundation.UUID
import edu.stanford.spezi.ui.StringResource
import java.util.UUID

data class ValidationRule(
    val id: UUID = UUID(),
    val message: StringResource,
    val effect: CascadingValidationEffect = CascadingValidationEffect.CONTINUE,
    private val rule: (String) -> Boolean,
) {

    override fun equals(other: Any?): Boolean {
        return other is ValidationRule && id == other.id
    }

    constructor(
        regex: Regex,
        message: StringResource,
        effect: CascadingValidationEffect = CascadingValidationEffect.CONTINUE,
    ) : this(
        message = message,
        effect = effect,
        rule = { input -> regex.matches(input) },
    )

    constructor(
        copy: ValidationRule,
        message: StringResource,
    ) : this(
        message = message,
        effect = copy.effect,
        rule = copy.rule,
    )

    constructor(
        pattern: String,
        message: StringResource,
        options: Set<RegexOption> = emptySet(),
        effect: CascadingValidationEffect = CascadingValidationEffect.CONTINUE,
    ) : this(
        message = message,
        effect = effect,
        rule = { input -> Regex(pattern, options).matches(input) },
    )

    override fun hashCode(): Int {
        return id.hashCode()
    }

    fun validate(input: String): FailedValidationResult? {
        return if (rule(input)) null else FailedValidationResult(this)
    }

    companion object
}

val ValidationRule.intercepting: ValidationRule
    get() = copy(effect = CascadingValidationEffect.INTERCEPT)

enum class CascadingValidationEffect {
    CONTINUE,
    INTERCEPT,
}
