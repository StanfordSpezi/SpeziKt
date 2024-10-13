package edu.stanford.spezi.modules.storage.secure

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import edu.stanford.spezi.core.logging.speziLogger
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey
import javax.inject.Inject

interface AndroidKeyStore {
    fun createKey(tag: String, size: Int = DEFAULT_KEY_SIZE): Result<KeyPair>
    fun retrievePrivateKey(tag: String): PrivateKey?
    fun retrievePublicKey(tag: String): PublicKey?
    fun deleteEntry(tag: String)
    fun aliases(): List<String>

    companion object {
        const val DEFAULT_KEY_SIZE = 2048
        const val PROVIDER = "AndroidKeyStore"
    }
}

internal class AndroidKeyStoreImpl @Inject constructor() : AndroidKeyStore {
    private val logger by speziLogger()

    private val keyStore: KeyStore by lazy {
        KeyStore.getInstance(AndroidKeyStore.PROVIDER).apply { load(null) }
    }

    override fun createKey(tag: String, size: Int): Result<KeyPair> = runCatching {
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            tag,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setKeySize(size)
            .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
            .build()
        val keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA)
        keyPairGenerator.initialize(keyGenParameterSpec)
        keyPairGenerator.genKeyPair()
    }

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

    override fun deleteEntry(tag: String) {
        runCatching {
            keyStore.deleteEntry(tag)
        }.onFailure {
            logger.e(it) { "Failed to delete entry with $tag" }
        }
    }

    override fun aliases(): List<String> = keyStore.aliases().toList()
}
