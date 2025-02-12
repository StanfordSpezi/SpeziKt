package edu.stanford.spezi.module.account.account

import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.module.account.account.value.AccountKey
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.value.collections.AccountDetailsSerializer
import edu.stanford.spezi.module.account.account.value.collections.AccountModifications
import edu.stanford.spezi.module.account.account.value.collections.serializer
import edu.stanford.spezi.modules.storage.local.LocalStorage
import edu.stanford.spezi.modules.storage.local.LocalStorageSetting
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

        return localStorage.read(
            key = storageKey(accountId),
            settings = storageSettings,
            serializer = AccountDetailsSerializer(keys = keys)
        )?.also {
            localCache[accountId] = it
        }
    }

    fun clearEntry(accountId: String) {
        localCache.remove(accountId)

        @SuppressWarnings("detekt:TooGenericExceptionCaught")
        try {
            localStorage.delete(storageKey(accountId))
        } catch (error: Throwable) {
            logger.e(error) { "Failed to clear cached account details from disk." }
        }
    }

    internal fun purgeMemoryCache(accountId: String) {
        localCache.remove(accountId)
    }

    fun communicateModifications(accountId: String, modifications: AccountModifications) {
        val details = AccountDetails()
        localCache[accountId]?.let {
            details.addContentsOf(it)
        }
        details.addContentsOf(modifications.modifiedDetails, merge = true)
        details.removeAll(modifications.removedAccountKeys)

        communicateRemoteChanges(accountId, details)
    }

    fun communicateRemoteChanges(accountId: String, details: AccountDetails) {
        localCache[accountId] = details

        @SuppressWarnings("detekt:TooGenericExceptionCaught")
        try {
            localStorage.store(
                storageKey(accountId),
                details,
                storageSettings,
                details.serializer()
            )
        } catch (error: Throwable) {
            logger.e(error) { "Failed to clear cached account details from disk." }
        }
    }

    private fun storageKey(accountId: String): String {
        return "edu.stanford.spezi.account.details-cache.$accountId"
    }
}
