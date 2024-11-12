package edu.stanford.spezi.module.account.account.service.configuration

import edu.stanford.spezi.module.account.account.value.AccountKey
import edu.stanford.spezi.module.account.account.value.configuration.AccountKeyConfiguration
import edu.stanford.spezi.module.account.account.value.configuration.AccountValueConfiguration

sealed interface SupportedAccountKeys {
    data object Arbitrary : SupportedAccountKeys
    data class Exactly(val keys: Collection<AccountKey<*>>) : SupportedAccountKeys

    fun canStore(value: AccountKeyConfiguration<*>): Boolean {
        return when (this) {
            is Arbitrary -> {
                true
            }
            is Exactly -> {
                val key = keys.first { it == value }

                TODO("Not implemented yet")
            }
        }
    }

    companion object {
        val key: AccountServiceConfigurationKey<SupportedAccountKeys> = SupportedAccountKeysKey
    }
}

private object SupportedAccountKeysKey : AccountServiceConfigurationKey<SupportedAccountKeys>

var AccountServiceConfiguration.supportedAccountKeys: SupportedAccountKeys
    get() = this.storage[SupportedAccountKeys.key] ?: error("Figure out how to translate preconditionFailure.")
    set(value) { this.storage[SupportedAccountKeys.key] = value }

fun AccountServiceConfiguration.unsupportedAccountKeys(configuration: AccountValueConfiguration): List<AccountKeyConfiguration<*>> {
    val supportedAccountKeys = supportedAccountKeys

    return configuration
        .filter { !supportedAccountKeys.canStore(it) }
}
