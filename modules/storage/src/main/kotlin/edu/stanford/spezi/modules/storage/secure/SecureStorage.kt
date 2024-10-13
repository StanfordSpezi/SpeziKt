package edu.stanford.spezi.modules.storage.secure

import edu.stanford.spezi.modules.storage.di.Storage
import edu.stanford.spezi.modules.storage.key.KeyValueStorageFactory
import edu.stanford.spezi.modules.storage.key.KeyValueStorageType
import edu.stanford.spezi.modules.storage.key.getSerializable
import edu.stanford.spezi.modules.storage.key.putSerializable
import javax.inject.Inject

interface SecureStorage {
    fun store(credentials: Credentials)

    fun retrieveCredentials(username: String, server: String?): Credentials?
    fun retrieveUserCredentials(username: String): List<Credentials>
    fun retrieveServerCredentials(server: String): List<Credentials>

    fun updateCredentials(username: String, server: String?, newCredentials: Credentials)

    fun deleteCredentials(username: String, server: String?)
    fun deleteUserCredentials(username: String)
    fun deleteServerCredentials(server: String)
    fun deleteAllCredentials(itemTypes: SecureStorageItemTypes)
}

internal class SecureStorageImpl @Inject constructor(
    storageFactory: KeyValueStorageFactory,
) : SecureStorage {

    private val storage = storageFactory.create(
        fileName = SECURE_STORAGE_FILE_NAME,
        type = KeyValueStorageType.ENCRYPTED,
    )

    override fun store(credentials: Credentials) {
        storage.putSerializable(
            key = storageKey(credentials.server, credentials.username),
            value = credentials
        )
    }

    override fun retrieveCredentials(
        username: String,
        server: String?,
    ): Credentials? {
        return storage.getSerializable(storageKey(server, username))
    }

    override fun retrieveServerCredentials(server: String): List<Credentials> {
        return storage.allKeys().mapNotNull { key ->
            val credential = storage.getSerializable<Credentials>(key)
            credential.takeIf { it?.server == server }
        }
    }

    override fun retrieveUserCredentials(username: String): List<Credentials> {
        return storage.allKeys().mapNotNull { key ->
            if (key.substringAfter(SERVER_USERNAME_SEPARATOR) == username) {
                storage.getSerializable(key)
            } else {
                null
            }
        }
    }

    override fun deleteCredentials(
        username: String,
        server: String?,
    ) {
        storage.delete(key = storageKey(server, username))
    }

    override fun deleteUserCredentials(username: String) {
        storage.allKeys().forEach { key ->
            if (key.substringAfter(SERVER_USERNAME_SEPARATOR) == username) storage.delete(key)
        }
    }

    override fun deleteServerCredentials(server: String) {
        storage.allKeys().forEach { key ->
            if (key.substringBefore(SERVER_USERNAME_SEPARATOR) == server) storage.delete(key)
        }
    }

    override fun deleteAllCredentials(itemTypes: SecureStorageItemTypes) {
        when (itemTypes) {
            SecureStorageItemTypes.ALL -> storage.clear()
            else -> {
                val deleteServer = itemTypes == SecureStorageItemTypes.SERVER_CREDENTIALS
                val deleteNonServer = itemTypes == SecureStorageItemTypes.NON_SERVER_CREDENTIALS
                storage.allKeys().forEach { key ->
                    val isServerKey = key.substringBefore(SERVER_USERNAME_SEPARATOR).isNotEmpty()
                    when {
                        isServerKey && deleteServer -> storage.delete(key)
                        !isServerKey && deleteNonServer -> storage.delete(key)
                    }
                }
            }
        }
    }

    override fun updateCredentials(
        username: String,
        server: String?,
        newCredentials: Credentials,
    ) {
        deleteCredentials(username, server)
        store(newCredentials)
    }

    private fun storageKey(server: String?, username: String): String =
        "${server ?: ""}$SERVER_USERNAME_SEPARATOR$username"

    private companion object {
        const val SECURE_STORAGE_FILE_NAME = "${Storage.STORAGE_FILE_PREFIX}SecureStorage"
        const val SERVER_USERNAME_SEPARATOR = "__@__"
    }
}
