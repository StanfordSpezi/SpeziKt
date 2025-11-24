package edu.stanford.spezi.health.internal

import edu.stanford.spezi.health.AnyRecordType
import edu.stanford.spezi.storage.local.LocalStorage
import edu.stanford.spezi.storage.local.LocalStorageSetting
import kotlinx.serialization.serializer

/**
 * Internal class for storing and retrieving health changes tokens.
 *
 * @property storage The [LocalStorage] instance used for storing tokens.
 */
internal class ChangesTokenStore(
    private val storage: LocalStorage,
) {
    /**
     * Stores a changes token for the specified [recordType].
     *
     * @param recordType The [AnyRecordType] for which the token is stored.
     * @param token The changes token to store.
     */
    suspend fun storeToken(recordType: AnyRecordType, token: String) {
        storage.store(
            key = keyFor(recordType),
            value = token,
            settings = LocalStorageSetting.Unencrypted,
            serializer = serializer(),
        )
    }

    /**
     * Retrieves the changes token for the specified [recordType].
     *
     * @param recordType The [AnyRecordType] for which the token is retrieved.
     * @return The changes token, or null if not found.
     */
    suspend fun getToken(recordType: AnyRecordType): String? {
        return storage.read(
            key = keyFor(recordType),
            settings = LocalStorageSetting.Unencrypted,
            serializer = serializer(),
        )
    }

    /**
     * Deletes the changes token for the specified [recordType].
     *
     * @param recordType The [AnyRecordType] for which the token is deleted.
     */
    suspend fun deleteToken(recordType: AnyRecordType) {
        storage.delete(keyFor(recordType))
    }

    private fun keyFor(type: AnyRecordType) = "health_changes_token_${type.identifier}"
}
