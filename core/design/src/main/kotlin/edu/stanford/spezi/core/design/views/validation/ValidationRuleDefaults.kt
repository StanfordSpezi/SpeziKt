package edu.stanford.spezi.core.design.views.validation

import edu.stanford.spezi.core.design.component.StringResource
import java.nio.charset.StandardCharsets

val ValidationRule.Companion.nonEmpty: ValidationRule
    get() = ValidationRule(
        regex = Regex(".*\\S+.*"),
        message = StringResource("VALIDATION_RULE_NON_EMPTY")
    )

val ValidationRule.Companion.unicodeLettersOnly: ValidationRule
    get() = ValidationRule(
        rule = { string -> string.all { it.isLetter() } },
        message = StringResource("VALIDATION_RULE_UNICODE_LETTERS")
    )

val ValidationRule.Companion.asciiLettersOnly: ValidationRule
    get() = ValidationRule(
        rule = { string -> StandardCharsets.US_ASCII.newEncoder().canEncode(string) },
        message = StringResource("VALIDATION_RULE_UNICODE_LETTERS_ASCII")
    )

val ValidationRule.Companion.minimalEmail: ValidationRule
    get() = ValidationRule(
        regex = Regex(".*@.+"),
        message = StringResource("VALIDATION_RULE_MINIMAL_EMAIL")
    )

val ValidationRule.Companion.minimalPassword: ValidationRule
    get() = ValidationRule(
        regex = Regex(".{8,}"),
        message = StringResource("VALIDATION_RULE_PASSWORD_LENGTH 8")
    )

val ValidationRule.Companion.mediumPassword: ValidationRule
    get() = ValidationRule(
        regex = Regex(".{10,}"),
        message = StringResource("VALIDATION_RULE_PASSWORD_LENGTH 10")
    )

val ValidationRule.Companion.strongPassword: ValidationRule
    get() = ValidationRule(
        regex = Regex(".{12,}"),
        message = StringResource("VALIDATION_RULE_PASSWORD_LENGTH 12")
    )
