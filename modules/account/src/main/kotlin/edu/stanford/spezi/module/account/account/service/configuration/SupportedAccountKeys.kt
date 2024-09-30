package edu.stanford.spezi.module.account.account.service.configuration

import edu.stanford.spezi.module.account.account.value.collections.AccountKey
import edu.stanford.spezi.module.account.account.value.configuration.AccountKeyConfiguration

sealed interface SupportedAccountKeys: AccountServiceConfigurationKey<SupportedAccountKeys> {
    data object Arbitrary: SupportedAccountKeys
    data class Exactly(val collection: Collection<AccountKey<*>>): SupportedAccountKeys

    fun canStore(value: AccountKeyConfiguration<*>): Boolean {
        return when (this) {
            is Arbitrary -> {
                true
            }
            is Exactly -> {
                val key = collection.first { it == value }

                TODO("Not implemented yet")
            }
        }
    }
}

var AccountServiceConfiguration.supportedAccountKeys: SupportedAccountKeys
    get() = this.storage[SupportedAccountKeys::class] ?: TODO("Figure out how to translate preconditionFailure.")
    set(value) { this.storage[SupportedAccountKeys::class] = value }

fun AccountServiceConfiguration.unsupportedAccountKeys(configuration: AccountValueConfiguration): List<AccountKeyConfiguration<*>> {
    val supportedAccountKeys = supportedAccountKeys

    return configuration
        .filter { !supportedAccountKeys.canStore(it) }
}
