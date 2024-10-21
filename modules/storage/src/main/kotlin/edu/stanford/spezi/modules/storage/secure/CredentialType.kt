package edu.stanford.spezi.modules.storage.secure

import java.util.EnumSet

enum class CredentialType {
    SERVER, NON_SERVER;

    companion object {
        val ALL: EnumSet<CredentialType> = EnumSet.allOf(CredentialType::class.java)
    }
}
