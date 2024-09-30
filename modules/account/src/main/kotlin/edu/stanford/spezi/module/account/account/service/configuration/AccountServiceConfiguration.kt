package edu.stanford.spezi.module.account.account.service.configuration

import edu.stanford.spezi.module.account.account.value.collections.AccountKey

data class AccountServiceConfiguration internal constructor(
    internal val storage: AccountServiceConfigurationStorage
) {
    companion object {
        operator fun invoke(supportedKeys: Set<AccountKey<*>>): AccountServiceConfiguration {
            val storage = AccountServiceConfigurationStorage()
            storage.supportedKeys = supportedKeys
            return AccountServiceConfiguration(storage)
        }
    }
}