package edu.stanford.spezi.modules.storage.local

import edu.stanford.spezi.modules.storage.secure.SecureStorage
import edu.stanford.spezi.modules.storage.secure.SecureStorageScope
import javax.crypto.SecretKey

sealed class LocalStorageSetting { // TODO: Adopt android-specific names instead, as SecureEnclave and AccessGroup are iOS-specific
    data class Unencrypted(
        val excludedFromBackup: Boolean = true
    ): LocalStorageSetting()

    data class Encrypted(
        val privateKey: SecretKey,
        val publicKey: SecretKey,
        val excludedFromBackup: Boolean
    ): LocalStorageSetting()

    data class EncyptedUsingSecureEnclave(
        val userPresence: Boolean = false
    ): LocalStorageSetting()

    data class EncryptedUsingKeychain(
        val userPresence: Boolean,
        val excludedFromBackup: Boolean = true
    ): LocalStorageSetting()

    val excludedFromBackupValue: Boolean get() =
        when (this) {
            is Unencrypted -> excludedFromBackup
            is Encrypted -> excludedFromBackup
            is EncryptedUsingKeychain -> excludedFromBackup
            is EncyptedUsingSecureEnclave -> true
        }

    fun keys(secureStorage: SecureStorage): Pair<SecretKey, SecretKey>? {
        val secureStorageScope = when (this) {
            is Unencrypted -> return null
            is Encrypted -> return Pair(privateKey, publicKey)
            is EncyptedUsingSecureEnclave ->
                SecureStorageScope.SecureEnclave(userPresence)
            is EncryptedUsingKeychain ->
                SecureStorageScope.Keychain(userPresence)
        }

        val tag = "LocalStorage.${secureStorageScope.identifier}"
        try {
            val privateKey = secureStorage.retrievePrivateKey(tag)
            val publicKey = secureStorage.retrievePublicKey(tag)
            if (privateKey != null && publicKey !== null)
                return Pair(privateKey, publicKey)
        } catch (_: Throwable) {}

        val privateKey = secureStorage.createKey(tag)
        val publicKey = secureStorage.retrievePublicKey(tag)
            ?: throw LocalStorageError.EncryptionNotPossible
        return Pair(privateKey, publicKey)
    }
}