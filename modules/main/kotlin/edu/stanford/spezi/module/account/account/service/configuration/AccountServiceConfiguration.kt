package edu.stanford.spezi.module.account.account.service.configuration

data class AccountServiceConfigurationPair<Value : Any>(
    val key: AccountServiceConfigurationKey<Value>,
    val value: Value,
) {
    fun storeIn(storage: AccountServiceConfigurationStorage) {
        storage[key] = value
    }
}

data class AccountServiceConfiguration internal constructor(
    internal val storage: AccountServiceConfigurationStorage = AccountServiceConfigurationStorage(),
) {
    companion object {
        operator fun invoke(
            supportedKeys: SupportedAccountKeys,
            configuration: List<AccountServiceConfigurationPair<*>> = emptyList(),
        ): AccountServiceConfiguration {
            val storage = AccountServiceConfigurationStorage()
            storage[SupportedAccountKeys.key] = supportedKeys
            for (entry in configuration) {
                entry.storeIn(storage)
            }
            return AccountServiceConfiguration(storage)
        }
    }
}
