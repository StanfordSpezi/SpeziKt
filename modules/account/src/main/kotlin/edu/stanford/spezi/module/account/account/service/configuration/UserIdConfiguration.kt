package edu.stanford.spezi.module.account.account.service.configuration

import androidx.compose.ui.text.input.KeyboardType

data class UserIdConfiguration(
    val idType: UserIdType,
    // val autofillHint: AutofillHint, // TODO: What is the equivalent in Android?
    // val contentType: TextContentType, // TODO: What is the equivalent in Android?
    val keyboardType: KeyboardType,
) : AccountServiceConfigurationValue {
    override fun storeIn(storage: AccountServiceConfigurationStorage) {
        storage[key] = this
    }

    companion object {
        val key = object : DefaultProvidingAccountServiceConfigurationKey<UserIdConfiguration> {
            override val defaultValue get() = UserIdConfiguration.emailAddress
        }

        val emailAddress = UserIdConfiguration(UserIdType.EmailAddress, KeyboardType.Email)
        val username = UserIdConfiguration(UserIdType.Username, KeyboardType.Text)
    }
}

val AccountServiceConfiguration.userIdConfiguration: UserIdConfiguration
    get() = this.storage[UserIdConfiguration.key]
