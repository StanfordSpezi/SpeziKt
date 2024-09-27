package edu.stanford.spezi.module.account.account.value.configuration

import edu.stanford.spezi.module.account.account.value.collections.AccountKey

sealed class SupportedAccountKeys {
    data object Arbitrary: SupportedAccountKeys()
    data class Exactly(val collection: Collection<AccountKey<*>>): SupportedAccountKeys()

    fun canStore(value: AccountKeyConfiguration): Boolean {
        return when (this) {
            is Arbitrary -> {
                true
            }
            is Exactly -> {
                TODO("Not implemented yet")
            }
        }
    }
}



