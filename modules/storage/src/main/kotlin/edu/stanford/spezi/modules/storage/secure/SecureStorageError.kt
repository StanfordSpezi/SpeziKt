package edu.stanford.spezi.modules.storage.secure

sealed class SecureStorageError : Error() {
    data object NotFound : SecureStorageError() {
        private fun readResolve(): Any = NotFound
    }

    data class CreateFailed(val error: Error? = null) : SecureStorageError()
    data object MissingEntitlement : SecureStorageError() {
        private fun readResolve(): Any = MissingEntitlement
    }
    // TODO: Missing cases for keychainError(status: OSStatus)
}
