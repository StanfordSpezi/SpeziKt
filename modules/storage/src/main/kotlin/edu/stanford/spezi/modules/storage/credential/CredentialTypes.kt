package edu.stanford.spezi.modules.storage.credential

import edu.stanford.spezi.modules.storage.credential.CredentialType.NON_SERVER
import edu.stanford.spezi.modules.storage.credential.CredentialType.SERVER
import java.util.EnumSet

data class CredentialTypes(
    internal val set: EnumSet<CredentialType>,
) {
    companion object {
        val All = CredentialTypes(EnumSet.allOf(CredentialType::class.java))
        val Server = CredentialTypes(EnumSet.of(SERVER))
        val NonServer = CredentialTypes(EnumSet.of(NON_SERVER))
    }
}

enum class CredentialType {
    SERVER, NON_SERVER
}
