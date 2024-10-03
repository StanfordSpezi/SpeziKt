package edu.stanford.spezi.modules.storage.local

import edu.stanford.spezi.modules.storage.secure.SecureStorage
import edu.stanford.spezi.modules.storage.secure.SecureStorageScope
import java.security.KeyPair

sealed class LocalStorageSetting {
    data object Unencrypted : LocalStorageSetting()

    data class Encrypted(val keyPair: KeyPair) : LocalStorageSetting()

    data object EncryptedUsingKeyStore : LocalStorageSetting()

    @Suppress("detekt:ReturnCount")
    fun keys(secureStorage: SecureStorage): KeyPair? {
        val secureStorageScope = when (this) {
            is Unencrypted -> return null
            is Encrypted -> return keyPair
            is EncryptedUsingKeyStore ->
                SecureStorageScope.KeyStore
        }

        val tag = "LocalStorage.${secureStorageScope.identifier}"
        try {
            val privateKey = secureStorage.retrievePrivateKey(tag)
            val publicKey = secureStorage.retrievePublicKey(tag)
            if (privateKey != null && publicKey != null) {
                return KeyPair(publicKey, privateKey)
            }
        } catch (_: Throwable) {}

        return secureStorage.createKey(tag)
    }
}
