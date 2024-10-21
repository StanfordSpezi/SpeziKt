package edu.stanford.spezi.modules.storage.local

import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.modules.storage.secure.SecureStorage
import edu.stanford.spezi.modules.storage.secure.SecureStorageScope
import java.security.KeyPair

sealed class LocalStorageSetting {
    data object Unencrypted : LocalStorageSetting()

    data class Encrypted(val keyPair: KeyPair) : LocalStorageSetting()

    data object EncryptedUsingKeyStore : LocalStorageSetting() {
        val logger by speziLogger()
    }

    fun keys(secureStorage: SecureStorage): KeyPair? {
        return when (this) {
            is Unencrypted -> null
            is Encrypted -> keyPair
            is EncryptedUsingKeyStore -> {
                val identifier = SecureStorageScope.KeyStore.identifier
                val tag = "LocalStorage.$identifier"
                return runCatching {
                    val privateKey = secureStorage.retrievePrivateKey(tag)
                    val publicKey = secureStorage.retrievePublicKey(tag)
                    return if (privateKey != null && publicKey != null) {
                        KeyPair(publicKey, privateKey)
                    } else {
                        null
                    }
                }.onFailure { failure ->
                    logger.e(failure) { "Retrieving private or public key from SecureStorage failed" }
                }.getOrNull() ?: secureStorage.createKey(tag)
            }
        }
    }
}
