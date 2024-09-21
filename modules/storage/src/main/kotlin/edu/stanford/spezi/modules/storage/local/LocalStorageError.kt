package edu.stanford.spezi.modules.storage.local

sealed class LocalStorageError: Error() {
    data object EncryptionNotPossible: LocalStorageError() {
        private fun readResolve(): Any = EncryptionNotPossible
    }
    data object CouldNotExcludedFromBackup: LocalStorageError() {
        private fun readResolve(): Any = CouldNotExcludedFromBackup // TODO: Weird naming
    }
    data object DecryptionNotPossible: LocalStorageError() {
        private fun readResolve(): Any = DecryptionNotPossible
    }
}