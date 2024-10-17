package edu.stanford.spezi.module.account.account.model

import edu.stanford.spezi.module.account.views.validation.ValidationRule

val ValidationRule.Companion.acceptAll: ValidationRule  // TODO: Adapt message
    get() = ValidationRule(rule = { return@ValidationRule true }, message = "")