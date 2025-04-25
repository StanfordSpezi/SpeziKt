package edu.stanford.spezi.storage.credential

import javax.inject.Inject

interface CredentialStorage {
    fun store(credential: Credential)

    fun update(
        username: String,
        server: String? = null,
        newCredential: Credential,
    )

    fun retrieve(username: String, server: String? = null): Credential?
    fun retrieveAll(server: String): List<Credential>

    fun delete(username: String, server: String? = null)
    fun deleteAll(types: CredentialTypes)
}

internal class CredentialStorageImpl @Inject constructor(
    storageFactory: KeyValueStorageFactory,
) : CredentialStorage {

    private val storage = storageFactory.create(
        fileName = SECURE_STORAGE_FILE_NAME,
        type = KeyValueStorageType.ENCRYPTED,
    )

    override fun store(credential: Credential) {
        storage.putSerializable(
            key = storageKey(credential.server, credential.username),
            value = credential
        )
    }

    override fun retrieve(
        username: String,
        server: String?,
    ): Credential? {
        return storage.getSerializable(storageKey(server, username))
    }

    override fun retrieveAll(server: String): List<Credential> {
        val serverKey = storageKey(server, "")
        return storage.allKeys().mapNotNull { key ->
            if (key.startsWith(serverKey)) {
                storage.getSerializable(key)
            } else {
                null
            }
        }
    }

    override fun delete(
        username: String,
        server: String?,
    ) {
        storage.delete(key = storageKey(server, username))
    }

    override fun deleteAll(types: CredentialTypes) {
        if (types.set.isEmpty()) return
        if (types.set == CredentialTypes.All.set) return storage.clear()
        val deleteServer = types.set.contains(CredentialType.SERVER)
        val deleteNonServer = types.set.contains(CredentialType.NON_SERVER)
        storage.allKeys().forEach { key ->
            val isServerKey = key.substringBefore(SERVER_USERNAME_SEPARATOR).isNotEmpty()
            when {
                isServerKey && deleteServer -> storage.delete(key)
                !isServerKey && deleteNonServer -> storage.delete(key)
            }
        }
    }

    override fun update(
        username: String,
        server: String?,
        newCredential: Credential,
    ) {
        delete(username, server)
        store(newCredential)
    }

    private fun storageKey(server: String?, username: String): String =
        "${server ?: ""}$SERVER_USERNAME_SEPARATOR$username"

    private companion object {
        const val SECURE_STORAGE_FILE_NAME = "${Storage.STORAGE_FILE_PREFIX}CredentialStorage"
        const val SERVER_USERNAME_SEPARATOR = "__@__"
    }
}
