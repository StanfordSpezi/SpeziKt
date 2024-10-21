package edu.stanford.spezi.modules.storage.secure

import edu.stanford.spezi.core.logging.speziLogger
import java.security.KeyPair
import java.security.PrivateKey
import java.security.PublicKey
import java.util.EnumSet
import javax.inject.Inject

interface SecureStorage {
    fun createKey(tag: String, size: Int): Result<KeyPair>
    fun retrievePrivateKey(tag: String): PrivateKey?
    fun retrievePublicKey(tag: String): PublicKey?
    fun deleteKeyPair(tag: String)

    fun storeCredential(credential: Credential)
    fun updateCredential(
        username: String,
        server: String? = null,
        newCredential: Credential,
    )
    fun retrieveCredential(username: String, server: String? = null): Credential?
    fun retrieveAllCredentials(server: String? = null): List<Credential>
    fun deleteCredential(username: String, server: String? = null)
    fun deleteAll(
        includingKeys: Boolean = true,
        credentialTypes: EnumSet<CredentialType> = CredentialType.ALL,
    )
}

internal class SecureStorageImpl @Inject constructor(
    private val credentialStorage: CredentialStorage,
    private val keyStorage: KeyStorage,
) : SecureStorage {
    private val logger by speziLogger()

    override fun createKey(tag: String, size: Int) = keyStorage.create(tag, size)
    override fun retrievePrivateKey(tag: String) = keyStorage.retrievePrivateKey(tag)
    override fun retrievePublicKey(tag: String) = keyStorage.retrievePublicKey(tag)
    override fun deleteKeyPair(tag: String) = keyStorage.delete(tag)

    override fun storeCredential(credential: Credential) =
        credentialStorage.store(credential)

    override fun updateCredential(
        username: String,
        server: String?,
        newCredential: Credential,
    ) = credentialStorage.update(username, server, newCredential)

    override fun retrieveCredential(username: String, server: String?) =
        credentialStorage.retrieve(username, server)
    override fun retrieveAllCredentials(server: String?) =
        credentialStorage.retrieveAll(server)

    override fun deleteCredential(username: String, server: String?) =
        credentialStorage.delete(username, server)

    override fun deleteAll(
        includingKeys: Boolean,
        credentialTypes: EnumSet<CredentialType>,
    ) {
        if (includingKeys) {
            keyStorage.deleteAll()
        }
        credentialStorage.deleteAll(credentialTypes)
    }
}
