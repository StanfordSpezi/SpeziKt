package edu.stanford.spezi.module.account.account

data class AccountServiceConfiguration internal constructor(
    private val storage: AccountServiceConfigurationStorage
) {
    companion object {
        operator fun invoke(supportedKeys: Set<AccountKey<*>>): AccountServiceConfiguration {
            val storage = AccountServiceConfigurationStorage()
            storage.supportedKeys = supportedKeys
            return AccountServiceConfiguration(storage)
        }
    }
}