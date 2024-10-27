package edu.stanford.spezi.modules.storage.local

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import edu.stanford.spezi.core.logging.speziLogger
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey
import javax.inject.Inject

interface KeyStorage {
    fun create(tag: String, size: Int = DEFAULT_KEY_SIZE): Result<KeyPair>

    fun retrieveKeyPair(tag: String): KeyPair?
    fun retrievePrivateKey(tag: String): PrivateKey?
    fun retrievePublicKey(tag: String): PublicKey?

    fun delete(tag: String)
    fun deleteAll()

    companion object {
        internal const val DEFAULT_KEY_SIZE = 2048
        internal const val PROVIDER = "AndroidKeyStore"
        const val CIPHER_TRANSFORMATION = "RSA/ECB/OAEPWithSHA-1AndMGF1Padding"
    }
}

internal class KeyStorageImpl @Inject constructor() : KeyStorage {
    private val logger by speziLogger()

    private val keyStore: KeyStore by lazy {
        KeyStore.getInstance(KeyStorage.PROVIDER).apply { load(null) }
    }

    override fun create(tag: String, size: Int): Result<KeyPair> = runCatching {
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            tag,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_ECB)
            .setKeySize(size)
            .setDigests(KeyProperties.DIGEST_SHA1)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
            .build()
        val keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA)
        keyPairGenerator.initialize(keyGenParameterSpec)
        keyPairGenerator.genKeyPair()
    }

    override fun retrieveKeyPair(tag: String): KeyPair? = runCatching {
        val publicKey = retrievePublicKey(tag)
        val privateKey = retrievePrivateKey(tag)
        if (publicKey != null && privateKey != null) {
            KeyPair(publicKey, privateKey)
        } else {
            null
        }
    }.getOrNull()

    override fun retrievePrivateKey(tag: String): PrivateKey? = runCatching {
        keyStore.getKey(tag, null) as? PrivateKey
    }.onFailure {
        logger.e(it) { "Failure during retrieval of private key with $tag" }
    }.getOrNull()

    override fun retrievePublicKey(tag: String): PublicKey? = runCatching {
        keyStore.getCertificate(tag)?.publicKey
    }.onFailure {
        logger.e(it) { "Failure during retrieval of public key with $tag" }
    }.getOrNull()

    override fun delete(tag: String) = runCatching {
        keyStore.deleteEntry(tag)
    }.onFailure {
        logger.e(it) { "Failed to delete entry with $tag" }
    }.getOrDefault(Unit)

    override fun deleteAll() {
        for (tag in keyStore.aliases()) {
            delete(tag)
        }
    }
}
