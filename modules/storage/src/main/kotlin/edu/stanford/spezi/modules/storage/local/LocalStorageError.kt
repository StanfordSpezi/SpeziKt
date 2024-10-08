package edu.stanford.spezi.modules.storage.local

sealed class LocalStorageError : Exception() {
    data object FileNameCouldNotBeIdentified : LocalStorageError() {
        @Suppress("detekt:UnusedPrivateMember")
        private fun readResolve(): Any = FileNameCouldNotBeIdentified
    }
}
