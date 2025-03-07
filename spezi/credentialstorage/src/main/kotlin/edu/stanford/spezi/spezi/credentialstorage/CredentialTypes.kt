package edu.stanford.spezi.spezi.credentialstorage

import java.util.EnumSet

data class CredentialTypes(
    internal val set: EnumSet<CredentialType>,
) {
    companion object {
        val All = CredentialTypes(EnumSet.allOf(CredentialType::class.java))
        val Server = CredentialTypes(EnumSet.of(CredentialType.SERVER))
        val NonServer = CredentialTypes(EnumSet.of(CredentialType.NON_SERVER))
    }
}

enum class CredentialType {
    SERVER, NON_SERVER
}
