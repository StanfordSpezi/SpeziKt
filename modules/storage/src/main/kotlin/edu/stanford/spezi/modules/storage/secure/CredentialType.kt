package edu.stanford.spezi.modules.storage.secure

import java.util.EnumSet

enum class CredentialType {
    SERVER, NON_SERVER;

    companion object {
        val All: EnumSet<CredentialType> = EnumSet.allOf(CredentialType::class.java)
        val Server = EnumSet.of(CredentialType.SERVER)
        val NonServer = EnumSet.of(CredentialType.NON_SERVER)
    }
}
