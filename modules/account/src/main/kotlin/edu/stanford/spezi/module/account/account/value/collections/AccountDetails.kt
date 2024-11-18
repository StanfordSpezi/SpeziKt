package edu.stanford.spezi.module.account.account.value.collections

import edu.stanford.spezi.core.utils.foundation.SharedRepository
import edu.stanford.spezi.core.utils.foundation.knowledgesource.KnowledgeSource
import edu.stanford.spezi.module.account.account.value.AccountKey
import edu.stanford.spezi.module.account.account.value.configuration.AccountKeyRequirement
import edu.stanford.spezi.module.account.account.value.configuration.AccountValueConfiguration

data class AccountDetails(
    internal val storage: AccountStorage = AccountStorage(),
) : SharedRepository<AccountAnchor> by storage, Iterable<Map.Entry<KnowledgeSource<AccountAnchor, *>, Any>> {
    fun isEmpty() =
        !storage.any()

    val keys
        get() = storage.mapNotNull { it.key as? AccountKey<*> }.toList()

    fun contains(key: AccountKey<*>) =
        storage.any { it.key === key }

    fun update(modifications: AccountModifications) {
        for (entry in modifications.modifiedDetails.keys) {
            entry.copy(modifications.modifiedDetails, this)
        }

        for (entry in modifications.removedAccountDetails.storage) {
            storage[entry.key] = null
        }
    }

    fun remove(key: AccountKey<*>) {
        storage[key] = null
    }

    fun removeAll(keys: List<AccountKey<*>>) {
        for (key in keys) {
            remove(key)
        }
    }

    fun addContentsOf(details: AccountDetails, filter: List<AccountKey<*>>? = null, merge: Boolean = false) {
        for (key in details.keys) {
            if ((filter?.contains(key) != false) && (merge || !contains(key))) {
                key.copy(details, this)
            }
        }
    }

    fun validateAgainstSignupRequirements(configuration: AccountValueConfiguration) {
        val missing = configuration.filter {
            it.requirement == AccountKeyRequirement.REQUIRED && !contains(it.key)
        }

        if (missing.isNotEmpty()) {
            val keyNames = missing.map { it.toString() } // TODO: KeyPathDescription

            // TODO: Create AccountOperationError
            error("AccountOperationError.missingAccountValue(${keyNames.joinToString(", ")})")
        }
    }

    override fun iterator() = storage.iterator()
}

private fun <Value : Any> AccountKey<Value>.copy(source: AccountDetails, destination: AccountDetails) {
    destination[this] = source[this]
}
