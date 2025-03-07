package edu.stanford.spezi.spezi.validation

import edu.stanford.spezi.spezi.ui.resources.StringResource
import java.nio.charset.StandardCharsets

val ValidationRule.Companion.nonEmpty: ValidationRule
    get() = ValidationRule(
        regex = Regex(".*\\S+.*"),
        message = StringResource("This field cannot be empty.")
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
        message = StringResource("Your password must be at least 8 characters long.")
    )

val ValidationRule.Companion.mediumPassword: ValidationRule
    get() = ValidationRule(
        regex = Regex(".{10,}"),
        message = StringResource("Your password must be at least 10 characters long.")
    )

val ValidationRule.Companion.strongPassword: ValidationRule
    get() = ValidationRule(
        regex = Regex(".{12,}"),
        message = StringResource("Your password must be at least 12 characters long.")
    )
