package edu.stanford.spezi.modules.storage.local

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.modules.storage.local.LocalStorageSetting.Encrypted
import edu.stanford.spezi.modules.storage.local.LocalStorageSetting.EncryptedUsingKeyStore
import edu.stanford.spezi.modules.storage.local.LocalStorageSetting.Unencrypted
import edu.stanford.spezi.modules.storage.secure.AndroidKeyStore
import edu.stanford.spezi.modules.storage.secure.SecureStorageScope
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.charset.StandardCharsets
import java.security.Key
import java.security.KeyPair
import javax.crypto.Cipher
import javax.inject.Inject

class LocalStorage @Inject constructor(
    @ApplicationContext private val context: Context,
    private val androidKeyStore: AndroidKeyStore,
) {

    private val logger by speziLogger()

    fun <C : Any> read(
        key: String,
        settings: LocalStorageSetting,
        serializer: KSerializer<C>,
    ): C? = runCatching {
        val keys = keys(settings)
        val bytes = file(key).readBytes()
        val jsonString = if (keys == null) {
            bytes
        } else {
            createCipher(Cipher.DECRYPT_MODE, keys.private).doFinal(bytes)
        }.let { String(it, StandardCharsets.UTF_8) }
        Json.decodeFromString(serializer, jsonString)
    }.getOrNull()

    fun <C : Any> store(
        key: String,
        value: C,
        settings: LocalStorageSetting,
        serializer: KSerializer<C>,
    ) {
        runCatching {
            val jsonData = Json.encodeToString(serializer, value).toByteArray(StandardCharsets.UTF_8)
            val keys = keys(settings)
            val writeData = if (keys == null) {
                jsonData
            } else {
                createCipher(Cipher.ENCRYPT_MODE, keys.public).doFinal(jsonData)
            }
            file(key).writeBytes(writeData)
        }
    }

    fun delete(key: String) {
        val file = file(key)
        if (file.exists()) {
            file.delete()
        }
    }

    private fun file(key: String): File {
        val directory = File(context.filesDir, "edu.stanford.spezi/LocalStorage")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        return File(directory, "$key.localstorage")
    }

    private fun keys(settings: LocalStorageSetting): KeyPair? = runCatching {
        when (settings) {
            is Unencrypted -> null
            is Encrypted -> settings.keyPair
            is EncryptedUsingKeyStore -> {
                val identifier = SecureStorageScope.KeyStore.identifier
                val tag = "LocalStorage.$identifier"
                val privateKey = androidKeyStore.retrievePrivateKey(tag)
                val publicKey = androidKeyStore.retrievePublicKey(tag)
                if (privateKey != null && publicKey != null) {
                    KeyPair(publicKey, privateKey)
                } else {
                    androidKeyStore.createKey(tag)
                }
            }
        }
    }.onFailure {
        logger.e(it) { "Error retrieve public and private key" }
    }.getOrNull()

    private fun createCipher(mode: Int, key: Key): Cipher =
        Cipher.getInstance("RSA/ECB/PKCS1Padding").apply { init(mode, key) }
}
