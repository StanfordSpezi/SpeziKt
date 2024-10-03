package edu.stanford.spezi.modules.storage.local

import edu.stanford.spezi.modules.storage.secure.SecureStorage
import edu.stanford.spezi.modules.storage.secure.SecureStorageScope
import java.security.KeyPair

sealed class LocalStorageSetting {
    data object Unencrypted : LocalStorageSetting()

    data class Encrypted(val keyPair: KeyPair) : LocalStorageSetting()

    data object EncryptedUsingKeyStore : LocalStorageSetting()

    @Suppress("detekt:TooGenericExceptionCaught")
    fun keys(secureStorage: SecureStorage): KeyPair? {
        return when (this) {
            is Unencrypted -> null
            is Encrypted -> keyPair
            is EncryptedUsingKeyStore -> {
                val identifier = SecureStorageScope.KeyStore.identifier
                val tag = "LocalStorage.$identifier"
                try {
                    val privateKey = secureStorage.retrievePrivateKey(tag)
                    val publicKey = secureStorage.retrievePublicKey(tag)
                    if (privateKey != null && publicKey != null) {
                        return KeyPair(publicKey, privateKey)
                    }
                } catch (error: Throwable) {
                    println("Retrieving private or public key from SecureStorage failed due to `$error`.")
                }
                secureStorage.createKey(tag)
            }
        }
    }
}
