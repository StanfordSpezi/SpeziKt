package edu.stanford.spezi.modules.storage.secure

enum class SecureStorageItemType {
    KEYS,
    SERVER_CREDENTIALS,
    NON_SERVER_CREDENTIALS
}

data class SecureStorageItemTypes(val types: Set<SecureStorageItemType>) {
    companion object {
        val keys = SecureStorageItemTypes(
            setOf(
                SecureStorageItemType.KEYS
            )
        )
        val serverCredentials = SecureStorageItemTypes(
            setOf(
                SecureStorageItemType.SERVER_CREDENTIALS
            )
        )
        val nonServerCredentials = SecureStorageItemTypes(
            setOf(
                SecureStorageItemType.NON_SERVER_CREDENTIALS
            )
        )
        val credentials = SecureStorageItemTypes(
            setOf(
                SecureStorageItemType.SERVER_CREDENTIALS,
                SecureStorageItemType.NON_SERVER_CREDENTIALS
            )
        )
        val all = SecureStorageItemTypes(
            SecureStorageItemType.entries.toSet()
        )
    }
}