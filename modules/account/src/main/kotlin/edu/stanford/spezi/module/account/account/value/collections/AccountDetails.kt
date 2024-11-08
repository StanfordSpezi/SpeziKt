package edu.stanford.spezi.module.account.account.value.collections

import edu.stanford.spezi.module.account.account.value.AccountKey

data class AccountDetails(
    internal val storage: AccountStorage = AccountStorage(),
) {
    fun isEmpty(): Boolean = !storage.any()

    val keys: List<AccountKey<*>>
        get() = storage.mapNotNull { it.anySource as? AccountKey<*> }.toList()

    fun contains(key: AccountKey<*>): Boolean {
        return storage.any { it.anySource.uuid == key.uuid }
    }

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

    fun remove(key: AccountKey<*>) {
        storage.remove(key)
    }

    fun addContentsOf(details: AccountDetails, filter: List<AccountKey<*>>, merge: Boolean = false) {
        for (key in details.keys) {
            if (filter.contains(key) && (merge || !contains(key))) {
                key.copy(details, this)
            }
        }
    }
}

private fun <Value : Any> AccountKey<Value>.copy(source: AccountDetails, destination: AccountDetails) {
    destination[this] = source[this]
}
