package edu.stanford.spezi.module.account.account.service.configuration

import androidx.compose.ui.text.input.KeyboardType
import edu.stanford.spezi.module.account.account.value.LocalizedStringResource

data class UserIdConfiguration(
    val type: UserIdType,
    val autofillHint: AutofillHint,
    // val contentType: TextContentType, // TODO: What is the equivalent in Android?
    val keyboardType: KeyboardType
): AccountServiceConfigurationKey<UserIdConfiguration> {
}

sealed class UserIdType(val stringResource: LocalizedStringResource) {
    data object EmailAddress: UserIdType("USER_ID_EMAIL")
    data object Username: UserIdType("USER_ID_USERNAME")
    class Other(stringResource: LocalizedStringResource): UserIdType(stringResource)
}