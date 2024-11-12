package edu.stanford.spezi.module.account.account.service.configuration

import edu.stanford.spezi.module.account.account.value.AccountKey
import edu.stanford.spezi.module.account.account.value.AccountKeys
import edu.stanford.spezi.module.account.account.value.keys.userId

data class RequiredAccountKeys(
    val keys: Collection<AccountKey<*>>,
) {
    companion object {
        val key = object : DefaultProvidingAccountServiceConfigurationKey<RequiredAccountKeys> {
            override val defaultValue: RequiredAccountKeys
                get() = RequiredAccountKeys(listOf(AccountKeys.userId))
        }
    }
}

val AccountServiceConfiguration.requiredAccountKeys: Collection<AccountKey<*>>
    get() = storage[RequiredAccountKeys.key].keys
