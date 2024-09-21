package edu.stanford.spezi.modules.storage.secure

import java.security.KeyStore
import javax.crypto.SecretKey

class SecureStorage {
    private val keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }

    fun createKey(
        tag: String,
        size: Int = 256,
        storageScope: SecureStorageScope = SecureStorageScope.secureEnclave
    ): SecretKey {
        // TODO: Implement
        throw NotImplementedError()
    }

    fun retrievePrivateKey(tag: String): SecretKey? {
        // TODO: Implement
        throw NotImplementedError()
    }

    fun retrievePublicKey(tag: String): SecretKey? {
        // TODO: Implement
        throw NotImplementedError()
    }

    fun deleteKeys(tag: String) {
        // TODO: Implement
        throw NotImplementedError()
    }

    fun store(
        credentials: Credentials,
        server: String? = null,
        removeDuplicate: Boolean = true,
        storageScope: SecureStorageScope
    ) {
        // TODO: Implement
        throw NotImplementedError()
    }

    fun deleteCredentials(
        username: String,
        server: String? = null,
        accessGroup: String? = null
    ) {
        // TODO: Implement
        throw NotImplementedError()
    }

    fun deleteAllCredentials(
        itemTypes: SecureStorageItemTypes = SecureStorageItemTypes.all,
        accessGroup: String? = null
    ) {
        // TODO: Implement
        throw NotImplementedError()
    }

    fun updateCredentials(
        username: String,
        server: String? = null,
        newCredentials: Credentials,
        newServer: String? = null,
        removeDuplicate: Boolean = true,
        storageScope: SecureStorageScope = SecureStorageScope.keychain
    ) {
        // TODO: Implement
        throw NotImplementedError()
    }

    fun retrieveCredentials(
        username: String,
        server: String? = null,
        accessGroup: String? = null
    ): Credentials? {
        // TODO: Implement
        throw NotImplementedError()
    }

    fun retrieveAllCredentials(
        server: String? = null,
        accessGroup: String? = null
    ): List<Credentials> {
        // TODO: Implement
        throw NotImplementedError()
    }
}