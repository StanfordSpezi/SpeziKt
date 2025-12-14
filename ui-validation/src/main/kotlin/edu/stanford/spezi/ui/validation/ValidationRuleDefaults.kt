package edu.stanford.spezi.ui.validation

import edu.stanford.spezi.ui.StringResource
import java.nio.charset.StandardCharsets

val ValidationRule.Companion.nonEmpty: ValidationRule
    get() = ValidationRule(
        regex = Regex(".*\\S+.*"),
        message = StringResource(R.string.ui_validation_empty_not_allowed)
    )

val ValidationRule.Companion.unicodeLettersOnly: ValidationRule
    get() = ValidationRule(
        rule = { string -> string.all { it.isLetter() } },
        message = StringResource(R.string.ui_validation_letters_only)
    )

val ValidationRule.Companion.asciiLettersOnly: ValidationRule
    get() = ValidationRule(
        rule = { string -> StandardCharsets.US_ASCII.newEncoder().canEncode(string) },
        message = StringResource(R.string.ui_validation_ascii_letters_only)
    )

val ValidationRule.Companion.minimalEmail: ValidationRule
    get() = ValidationRule(
        regex = Regex(".*@.+"),
        message = StringResource(R.string.ui_validation_minimal_email)
    )

val ValidationRule.Companion.minimalPassword: ValidationRule
    get() = ValidationRule(
        regex = Regex(".{8,}"),
        message = StringResource(R.string.ui_validation_password_min_length)
    )

val ValidationRule.Companion.mediumPassword: ValidationRule
    get() = ValidationRule(
        regex = Regex(".{10,}"),
        message = StringResource(R.string.ui_validation_password_med_length)
    )

val ValidationRule.Companion.strongPassword: ValidationRule
    get() = ValidationRule(
        regex = Regex(".{12,}"),
        message = StringResource(R.string.ui_validation_password_strong_length)
    )
