package edu.stanford.spezi.module.account.views.validation

val ValidationRule.Companion.nonEmpty: ValidationRule
    get() = ValidationRule(regex = Regex(".*\\S+.*"), message = "") // TODO: message

val ValidationRule.Companion.unicodeLettersOnly: ValidationRule
    get() = ValidationRule(rule = { string -> string.all { it.isLetter() } }, message = "") // TODO: message

val ValidationRule.Companion.asciiLettersOnly: ValidationRule
    get() = TODO("Not implemented yet")

val ValidationRule.Companion.minimalEmail: ValidationRule
    get() = ValidationRule(regex = Regex(".*@.+"), message = "") // TODO: message

val ValidationRule.Companion.minimalPassword: ValidationRule
    get() = ValidationRule(regex = Regex(".{8,}"), message = "") // TODO: message

val ValidationRule.Companion.mediumPassword: ValidationRule
    get() = ValidationRule(regex = Regex(".{10,}"), message = "") // TODO: message

val ValidationRule.Companion.strongPassword: ValidationRule
    get() = ValidationRule(regex = Regex(".{12,}"), message = "") // TODO: message
