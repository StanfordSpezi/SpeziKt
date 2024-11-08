package edu.stanford.spezi.module.account.account.value.collections

import android.accounts.Account
import edu.stanford.spezi.module.account.account.value.AccountKey

data class AccountDetails(
    val storage: AccountStorage = AccountStorage()
) {
    fun isEmpty(): Boolean = !storage.any()

    operator fun <Value : Any> get(key: AccountKey<Value>): Value? {
        return storage[key]
    }

    operator fun <Value : Any> set(key: AccountKey<Value>, value: Value?) {
        storage[key] = value
    }

    fun update(modifications: AccountModifications) {
        for (entry in modifications.modifiedDetails.storage.storage) {
            storage.storage[entry.key] = entry.value
        }

        for (key in modifications.removedAccountDetails.storage.storage.keys) {
            storage.storage.remove(key)
        }
    }
}
