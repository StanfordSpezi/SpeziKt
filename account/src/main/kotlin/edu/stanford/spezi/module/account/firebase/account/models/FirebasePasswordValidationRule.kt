package edu.stanford.spezi.module.account.firebase.account.models

import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.design.views.validation.ValidationRule

val ValidationRule.Companion.minimumFirebasePassword get() = ValidationRule(
    regex = Regex("(?=.*[0-9a-zA-Z]).{6,}"),
    message = StringResource("FIREBASE_ACCOUNT_DEFAULT_PASSWORD_RULE_ERROR 6")
)
