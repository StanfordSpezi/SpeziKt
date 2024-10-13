package edu.stanford.spezi.modules.storage.secure

import java.util.EnumSet

enum class SecureStorageItemType {
    SERVER_CREDENTIALS,
    NON_SERVER_CREDENTIALS,
}

class SecureStorageItemTypes private constructor(
    private val types: EnumSet<SecureStorageItemType>,
) {
    fun contains(type: SecureStorageItemType) = types.contains(type)

    companion object {
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
        val all = SecureStorageItemTypes(
            EnumSet.of(
                SecureStorageItemType.SERVER_CREDENTIALS,
                SecureStorageItemType.NON_SERVER_CREDENTIALS
            )
        )
    }
}
