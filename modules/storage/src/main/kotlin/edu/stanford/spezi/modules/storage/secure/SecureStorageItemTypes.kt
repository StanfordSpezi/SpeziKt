package edu.stanford.spezi.modules.storage.secure

import java.util.EnumSet

enum class SecureStorageItemType {
    KEYS,
    SERVER_CREDENTIALS,
    NON_SERVER_CREDENTIALS,
}

data class SecureStorageItemTypes(val types: EnumSet<SecureStorageItemType>) {
    companion object {
        val keys = SecureStorageItemTypes(
            EnumSet.of(
                SecureStorageItemType.KEYS
            )
        )
        val serverCredentials = SecureStorageItemTypes(
            EnumSet.of(
                SecureStorageItemType.SERVER_CREDENTIALS
            )
        )
        val nonServerCredentials = SecureStorageItemTypes(
            EnumSet.of(
                SecureStorageItemType.NON_SERVER_CREDENTIALS
            )
        )
        val credentials = SecureStorageItemTypes(
            EnumSet.of(
                SecureStorageItemType.SERVER_CREDENTIALS,
                SecureStorageItemType.NON_SERVER_CREDENTIALS
            )
        )
        val all = SecureStorageItemTypes(
            EnumSet.allOf(SecureStorageItemType::class.java)
        )
    }
}
