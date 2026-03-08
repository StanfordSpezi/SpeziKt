package edu.stanford.spezi.account

import edu.stanford.spezi.account.internal.AccountDetailsCodec
import edu.stanford.spezi.account.internal.StoredAccountDetails
import edu.stanford.spezi.core.Module
import edu.stanford.spezi.core.dependency
import edu.stanford.spezi.storage.local.LocalStorage
import edu.stanford.spezi.storage.local.LocalStorageSetting
import java.util.concurrent.ConcurrentHashMap

/**
 * A local cache for [AccountDetails].
 *
 * This component maintains a **two-layer cache**:
 *
 * 1. **In-memory cache** for fast access during runtime
 * 2. **Encrypted persistent storage** using [LocalStorage] for restoring account details
 *    across application restarts
 *
 * The cache is used by [AccountStorageProvider] implementations (such as
 * `FirestoreAccountStorage`) to avoid unnecessary remote requests while still
 * ensuring that account data remains synchronized with external sources.
 *
 * Data written to persistent storage is encrypted using
 * [LocalStorageSetting.EncryptedUsingKeyStore].
 *
 * @see AccountStorageProvider
 * @see LocalStorage
 */
class AccountDetailsCache : Module {
    private val codec by dependency<AccountDetailsCodec>()
    private val memoryCache = ConcurrentHashMap<String, AccountDetails>()
    private val localStorage by dependency<LocalStorage>()

    /**
     * Loads cached account details for the given [accountId].
     *
     * The lookup order is:
     *
     * 1. In-memory cache
     * 2. Encrypted local storage
     *
     * If details are restored from persistent storage, they are also
     * inserted into the in-memory cache.
     *
     * Only the requested [keys] are returned. If [keys] is empty,
     * all cached details are returned.
     *
     * @param accountId The identifier of the account.
     * @param keys The set of account keys that should be loaded.
     * @return The cached [AccountDetails], or `null` if none exist.
     */
    suspend fun load(
        accountId: String,
        keys: Set<AnyAccountKey>,
    ): AccountDetails? {
        val cached = memoryCache[accountId]
        return if (cached != null) {
            filterDetails(cached, keys)
        } else {
            val stored = localStorage.read(
                key = storageKey(accountId),
                settings = LocalStorageSetting.EncryptedUsingKeyStore,
                serializer = StoredAccountDetails.serializer(),
            )
            stored?.let { stored ->
                runCatching {
                    codec.decode(stored = stored, keys = keys)
                }.getOrNull()
            }
        }
    }

    /**
     * Clears all cached data for the given [accountId].
     *
     * This removes:
     * - the in-memory cache entry
     * - the encrypted local storage entry
     *
     * Typically used when a user logs out or the account is deleted.
     *
     * @param accountId The identifier of the account.
     */
    suspend fun clear(accountId: String) {
        memoryCache.remove(accountId)
        localStorage.delete(storageKey(accountId))
    }

    /**
     * Removes cached account details from memory only.
     *
     * Persistent storage remains untouched.
     *
     * This is useful when forcing a reload from disk or a remote source
     * without losing the stored account state.
     *
     * @param accountId The identifier of the account.
     */
    fun purgeMemoryCache(accountId: String) {
        memoryCache.remove(accountId)
    }

    /**
     * Updates the cache with account details received from a remote storage provider.
     *
     * This method is typically called when external storage (e.g. Firestore)
     * notifies the system about updated account details.
     *
     * The updated details are written to both:
     * - the in-memory cache
     * - encrypted local storage
     *
     * @param accountId The identifier of the account.
     * @param details The updated account details.
     */
    suspend fun communicateRemoteChanges(
        accountId: String,
        details: AccountDetails,
    ) {
        memoryCache[accountId] = details.copy()
        localStorage.store(
            key = storageKey(accountId),
            value = codec.encode(accountId = accountId, details = details),
            settings = LocalStorageSetting.EncryptedUsingKeyStore,
            serializer = StoredAccountDetails.serializer(),
        )
    }

    /**
     * Applies local [AccountModifications] to the cached account details.
     *
     * The modifications are merged into the current cache state and
     * persisted to encrypted local storage.
     *
     * This method is typically invoked when account details are updated
     * through the application itself.
     *
     * @param accountId The identifier of the account.
     * @param modifications The modifications to apply.
     */
    suspend fun communicateModifications(
        accountId: String,
        modifications: AccountModifications,
    ) {
        val current = memoryCache[accountId] ?: AccountDetails()
        current.addContents(modifications.modifiedDetails)
        current.removeAll(modifications.removedAccountKeys)
        memoryCache[accountId] = current

        val existing = localStorage.read(
            key = storageKey(accountId),
            settings = LocalStorageSetting.EncryptedUsingKeyStore,
            serializer = StoredAccountDetails.serializer(),
        )
        val newValue = if (existing != null) {
            val encodedModifications = codec.encode(accountId = accountId, details = modifications.modifiedDetails)
            val removedIdentifiers = modifications.removedAccountDetails.accountKeyTypes.keys().map { it.identifier }.toSet()
            val patchedMap = existing.accountKeyValues.toMutableMap()
            patchedMap.putAll(encodedModifications.accountKeyValues)
            removedIdentifiers.forEach { patchedMap.remove(it) }
            StoredAccountDetails(accountId = accountId, accountKeyValues = patchedMap)
        } else {
            codec.encode(accountId = accountId, details = current)
        }

        localStorage.store(
            key = storageKey(accountId),
            value = newValue,
            settings = LocalStorageSetting.EncryptedUsingKeyStore,
            serializer = StoredAccountDetails.serializer(),
        )
    }

    /**
     * Filters [details] so that only values matching the requested [keys] remain.
     *
     * If [keys] is empty, the original details are returned unchanged.
     */
    private fun filterDetails(
        details: AccountDetails,
        keys: Set<AnyAccountKey>,
    ): AccountDetails {
        if (keys.isEmpty()) return details
        val filtered = AccountDetails()
        keys.forEach { key ->
            val value = details.getAnyOrNull(key::class) ?: return@forEach
            filtered.setAny(key::class, value)
        }
        return filtered
    }

    /**
     * Generates the local storage key used for persisting account details.
     *
     * @param accountId The identifier of the account.
     */
    private fun storageKey(accountId: String): String {
        return "edu.stanford.spezi.account.firebase.storage.$accountId"
    }
}
