package edu.stanford.spezi.modules.storage.secure

import edu.stanford.spezi.modules.storage.key.EncryptedKeyValueStorage
import javax.inject.Inject

class SecureStorage @Inject constructor(
    storageFactory: EncryptedKeyValueStorage.Factory,
    private val androidKeyStore: AndroidKeyStore,
) {

    private val encryptedKeyValueStorage = storageFactory.create(
        fileName = "spezi_credentials_secure_storage"
    )

    suspend fun store(
        credentials: Credentials,
        server: String? = null,
    ) {
        encryptedKeyValueStorage.putString(
            key = storageKey(server, credentials.username),
            value = credentials.password
        )
    }

    suspend fun deleteCredentials(
        username: String,
        server: String? = null,
    ) {
        encryptedKeyValueStorage.deleteString(key = storageKey(server, username))
    }

    suspend fun deleteAllCredentials(itemTypes: SecureStorageItemTypes) {
        val containsServerCredentials = itemTypes.types.contains(SecureStorageItemType.SERVER_CREDENTIALS)
        val containsNonServerCredentials = itemTypes.types.contains(SecureStorageItemType.NON_SERVER_CREDENTIALS)
        if (containsServerCredentials || containsNonServerCredentials) {
            encryptedKeyValueStorage.allKeys().forEach { key ->
                if (key.startsWith(" ") && containsNonServerCredentials) { // non-server credential
                    encryptedKeyValueStorage.deleteString(key)
                } else if (key.startsWith(" ").not() && containsServerCredentials) { // server credential
                    encryptedKeyValueStorage.deleteString(key)
                }
            }
        }

        if (itemTypes.types.contains(SecureStorageItemType.KEYS)) {
            androidKeyStore.aliases().forEach { androidKeyStore.deleteEntry(tag = it) }
        }
    }

    suspend fun updateCredentials(
        username: String,
        server: String? = null,
        newCredentials: Credentials,
        newServer: String? = null,
    ) {
        deleteCredentials(username, server)
        store(newCredentials, newServer)
    }

    suspend fun retrieveCredentials(
        username: String,
        server: String? = null,
    ): Credentials? {
        val key = storageKey(server, username)
        return encryptedKeyValueStorage.getString(key)?.let { Credentials(username, it) }
    }

    suspend fun retrieveAllCredentials(
        server: String? = null,
    ): List<Credentials> {
        return encryptedKeyValueStorage.allKeys().mapNotNull { key ->
            val password = server?.let {
                if (key.startsWith("$it ")) {
                    encryptedKeyValueStorage.getString(key)
                } else {
                    null
                }
            } ?: encryptedKeyValueStorage.getString(key)

            password?.let {
                val separatorIndex = key.indexOf(" ")
                Credentials(key.drop(separatorIndex + 1), password)
            }
        }
    }

    // TODO: Check for potential key collisions
    private fun storageKey(server: String?, username: String): String =
        "${server ?: ""} $username"
}
