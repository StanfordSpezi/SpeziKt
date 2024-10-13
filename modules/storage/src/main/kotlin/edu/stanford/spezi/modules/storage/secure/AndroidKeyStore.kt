package edu.stanford.spezi.modules.storage.secure

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey
import javax.inject.Inject

class AndroidKeyStore @Inject constructor() {
    private val keyStore: KeyStore by lazy {
        KeyStore.getInstance(PROVIDER).apply { load(null) }
    }

    fun deleteEntry(tag: String) {
        keyStore.deleteEntry(tag)
    }

    fun retrievePrivateKey(tag: String): PrivateKey? {
        return keyStore.getKey(tag, null) as? PrivateKey
    }

    fun retrievePublicKey(tag: String): PublicKey? {
        return keyStore.getCertificate(tag)?.publicKey
    }

    fun aliases(): List<String> = keyStore.aliases().toList()

    fun createKey(
        tag: String,
        size: Int = 2048, // TODO: Should we just use RSA here instead of what iOS uses?
    ): KeyPair {
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
        return keyPairGenerator.genKeyPair()
    }

    private companion object {
        const val PROVIDER = "AndroidKeyStore"
    }
}
