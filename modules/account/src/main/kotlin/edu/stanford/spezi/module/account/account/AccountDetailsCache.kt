package edu.stanford.spezi.module.account.account

import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.value.collections.AccountKey
import edu.stanford.spezi.module.account.account.value.collections.AccountModifications
import edu.stanford.spezi.module.account.spezi.Module
import kotlin.reflect.KClass

class AccountDetailsCache: Module {
    private val logger by speziLogger()

    private val localCache = mutableMapOf<String, AccountDetails>()

    fun loadEntry(accountId: String, keys: Set<KClass<AccountKey<*>>>): AccountDetails? {
        localCache[accountId]?.let {
            return it
        }

        return null // TODO: lead from persistency as well
    }

    fun clearEntry(accountId: String) {
        localCache.remove(accountId)
        // TODO: Delete persistence as well
    }

    internal fun purgeMemoryCache(accountId: String) {
        localCache.remove(accountId)
    }

    fun communicateModifications(accountId: String, modifications: AccountModifications) {
        val details = AccountDetails()
        localCache[accountId]?.let {
            // TODO("AccountDetails.add(contentsOf:) missing!")
        }
        // TODO("AccountDetails.add(contentsOf:merge:) missing!")
        // TODO("AccountDetails.removeAll() missing!")

        communicateRemoteChanges(accountId, details)
    }

    fun communicateRemoteChanges(accountId: String, details: AccountDetails) {
        localCache[accountId] = details

        // TODO: Persistent store missing
    }

}