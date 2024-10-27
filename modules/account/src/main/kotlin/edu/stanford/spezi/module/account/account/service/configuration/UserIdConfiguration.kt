package edu.stanford.spezi.module.account.account.service.configuration

import androidx.compose.ui.text.input.KeyboardType
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.utils.UUID
import java.util.UUID

data class UserIdConfiguration(
    val idType: UserIdType,
    // val autofillHint: AutofillHint, // TODO: What is the equivalent in Android?
    // val contentType: TextContentType, // TODO: What is the equivalent in Android?
    val keyboardType: KeyboardType
) {
    companion object {
        val key: DefaultProvidingAccountServiceConfigurationKey<UserIdConfiguration> = UserIdConfigurationKey()

        val emailAddress = UserIdConfiguration(UserIdType.EmailAddress, KeyboardType.Email)
        val username = UserIdConfiguration(UserIdType.Username, KeyboardType.Text)
    }
}

private data class UserIdConfigurationKey(
    override val uuid: UUID = UUID(),
) : DefaultProvidingAccountServiceConfigurationKey<UserIdConfiguration> {
    override val defaultValue: UserIdConfiguration
        get() = UserIdConfiguration.emailAddress
}

val AccountServiceConfiguration.userIdConfiguration: UserIdConfiguration
    get() = this.storage[UserIdConfiguration.key]

sealed class UserIdType(val stringResource: StringResource) {
    data object EmailAddress : UserIdType(StringResource("USER_ID_EMAIL"))
    data object Username : UserIdType(StringResource("USER_ID_USERNAME"))
    class Other(stringResource: StringResource) : UserIdType(stringResource)
}
