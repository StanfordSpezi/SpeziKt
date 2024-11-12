package edu.stanford.spezi.module.account.account.service.configuration

import edu.stanford.spezi.module.account.account.value.AccountKey
import edu.stanford.spezi.module.account.account.value.configuration.AccountKeyConfiguration
import edu.stanford.spezi.module.account.account.value.configuration.AccountKeyRequirement
import edu.stanford.spezi.module.account.account.value.configuration.AccountValueConfiguration
import edu.stanford.spezi.module.account.account.value.isRequired

sealed interface SupportedAccountKeys {
    data object Arbitrary : SupportedAccountKeys
    data class Exactly(val keys: List<AccountKey<*>>) : SupportedAccountKeys

    fun canStore(value: AccountKeyConfiguration<*>): Boolean {
        return when (this) {
            is Arbitrary -> {
                true
            }
            is Exactly -> {
                keys.firstOrNull { it === value.key }?.let { key ->
                    !key.isRequired || value.requirement == AccountKeyRequirement.REQUIRED
                } ?: false
            }
        }
    }

    companion object {
        val key = object : AccountServiceConfigurationKey<SupportedAccountKeys> {}
    }
}

var AccountServiceConfiguration.supportedAccountKeys: SupportedAccountKeys
    get() = this.storage[SupportedAccountKeys.key]
        ?: error("Reached illegal state where SupportedAccountKeys configuration was never supplied!")
    set(value) { this.storage[SupportedAccountKeys.key] = value }

fun AccountServiceConfiguration.unsupportedAccountKeys(configuration: AccountValueConfiguration): List<AccountKeyConfiguration<*>> {
    val supportedAccountKeys = supportedAccountKeys

    return configuration
        .filter { !supportedAccountKeys.canStore(it) }
}
