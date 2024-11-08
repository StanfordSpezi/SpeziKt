package edu.stanford.spezi.module.account.account.model

import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.module.account.views.validation.ValidationRule

val ValidationRule.Companion.acceptAll: ValidationRule
    get() = ValidationRule(rule = { return@ValidationRule true }, message = StringResource("This is never shown to the user."))
