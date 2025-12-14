package edu.stanford.spezi.module.account.account.service.configuration

interface AccountServiceConfigurationValue {
    fun storeIn(storage: AccountServiceConfigurationStorage)
}

data class AccountServiceConfiguration internal constructor(
    internal val storage: AccountServiceConfigurationStorage = AccountServiceConfigurationStorage(),
) {
    constructor(
        supportedKeys: SupportedAccountKeys,
        configuration: List<AccountServiceConfigurationValue> = emptyList(),
    ) : this(
        AccountServiceConfigurationStorage().apply {
            supportedKeys.storeIn(this)
            for (entry in configuration) {
                entry.storeIn(this)
            }
        }
    )
}
