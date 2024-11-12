package edu.stanford.spezi.module.account.account

import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.module.account.account.value.AccountKey
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.value.collections.AccountModifications
import javax.inject.Inject

class AccountDetailsCache(
    private val storageSettings: LocalStorageSetting = LocalStorageSetting.EncryptedUsingKeyStore,
) {
    private val logger by speziLogger()

    private val localCache = mutableMapOf<String, AccountDetails>()

    @Inject internal lateinit var localStorage: LocalStorage

    fun loadEntry(accountId: String, keys: List<AccountKey<*>>): AccountDetails? {
        localCache[accountId]?.let {
            return it
        }

        localStorage.read("edu.stanford.spezi.account.details-cache", storageSettings) {
            val details = AccountDetails()
            for (key in keys) {
                key.identifier
            }
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
