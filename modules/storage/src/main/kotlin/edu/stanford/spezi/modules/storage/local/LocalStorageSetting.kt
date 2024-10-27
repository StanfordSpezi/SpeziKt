package edu.stanford.spezi.modules.storage.local

import java.security.KeyPair

sealed interface LocalStorageSetting {
    data object Unencrypted : LocalStorageSetting
    data class Encrypted(val keyPair: KeyPair) : LocalStorageSetting
    data object EncryptedUsingKeyStore : LocalStorageSetting
}
