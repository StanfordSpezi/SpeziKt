package edu.stanford.spezi.modules.storage.secure

sealed class SecureStorageScope {
    data object KeyStore : SecureStorageScope()

    val identifier: String get() =
        when (this) {
            is KeyStore ->
                "keyStore"
        }

    val userPresence: Boolean get() =
        when (this) {
            is KeyStore -> false
        }
}
