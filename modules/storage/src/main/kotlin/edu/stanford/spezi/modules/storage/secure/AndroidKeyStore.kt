package edu.stanford.spezi.modules.storage.secure

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import edu.stanford.spezi.core.logging.speziLogger
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey
import javax.crypto.Cipher
import javax.inject.Inject

interface AndroidKeyStore {
    fun createKey(tag: String, size: Int = DEFAULT_KEY_SIZE): Result<KeyPair>

    fun retrieveKeyPair(tag: String): KeyPair?
    fun retrievePrivateKey(tag: String): PrivateKey?
    fun retrievePublicKey(tag: String): PublicKey?

    fun deleteEntry(tag: String)

    fun clear()

    fun getCipher(): Cipher

    fun aliases(): List<String>

    companion object {
        const val DEFAULT_KEY_SIZE = 2048
        const val PROVIDER = "AndroidKeyStore"
        const val CIPHER_TRANSFORMATION = "RSA/ECB/OAEPWithSHA-1AndMGF1Padding"
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

    override fun deleteEntry(tag: String) {
        runCatching {
            keyStore.deleteEntry(tag)
        }.onFailure {
            logger.e(it) { "Failed to delete entry with $tag" }
        }
    }

    override fun clear() {
        aliases().forEach { deleteEntry(it) }
    }

    override fun aliases(): List<String> = keyStore.aliases().toList()

    override fun getCipher(): Cipher {
        return Cipher.getInstance(AndroidKeyStore.CIPHER_TRANSFORMATION)
    }
}
