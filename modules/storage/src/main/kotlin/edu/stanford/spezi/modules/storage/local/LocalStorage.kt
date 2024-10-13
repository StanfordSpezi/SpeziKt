package edu.stanford.spezi.modules.storage.local

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.stanford.spezi.core.coroutines.di.Dispatching
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.modules.storage.di.Storage
import edu.stanford.spezi.modules.storage.local.LocalStorageSetting.Encrypted
import edu.stanford.spezi.modules.storage.local.LocalStorageSetting.EncryptedUsingKeyStore
import edu.stanford.spezi.modules.storage.local.LocalStorageSetting.Unencrypted
import edu.stanford.spezi.modules.storage.secure.AndroidKeyStore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.charset.StandardCharsets
import java.security.Key
import java.security.KeyPair
import javax.crypto.Cipher
import javax.inject.Inject

interface LocalStorage {
    suspend fun <T : Any> read(
        key: String,
        settings: LocalStorageSetting,
        serializer: DeserializationStrategy<T>,
    ): T?

    suspend fun <T : Any> read(
        key: String,
        settings: LocalStorageSetting,
        decoding: (ByteArray) -> T,
    ): T?

    suspend fun <T : Any> store(
        key: String,
        value: T,
        settings: LocalStorageSetting,
        serializer: SerializationStrategy<T>,
    )

    suspend fun <T : Any> store(
        key: String,
        value: T,
        settings: LocalStorageSetting,
        encoding: (T) -> ByteArray,
    )

    suspend fun delete(key: String)
}

internal class LocalStorageImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @Dispatching.IO private val ioDispatcher: CoroutineDispatcher,
    private val androidKeyStore: AndroidKeyStore,
) : LocalStorage {

    private val logger by speziLogger()

    override suspend fun <T : Any> read(
        key: String,
        settings: LocalStorageSetting,
        serializer: DeserializationStrategy<T>,
    ): T? {
        return read(
            key = key,
            settings = settings,
            decoding = { data ->
                Json.decodeFromString(serializer, String(data, StandardCharsets.UTF_8))
            }
        )
    }

    override suspend fun <T : Any> read(
        key: String,
        settings: LocalStorageSetting,
        decoding: (ByteArray) -> T,
    ): T? = execute {
        val keys = keys(settings)
        val bytes = file(key).readBytes()
        val data = if (keys == null) {
            bytes
        } else {
            createCipher(Cipher.DECRYPT_MODE, keys.private).doFinal(bytes)
        }
        decoding(data)
    }

    override suspend fun <T : Any> store(
        key: String,
        value: T,
        settings: LocalStorageSetting,
        serializer: SerializationStrategy<T>,
    ) {
        store(
            key = key,
            value = value,
            settings = settings,
            encoding = { instance ->
                Json.encodeToString(serializer, instance).toByteArray(StandardCharsets.UTF_8)
            }
        )
    }

    override suspend fun <T : Any> store(
        key: String,
        value: T,
        settings: LocalStorageSetting,
        encoding: (T) -> ByteArray,
    ) {
        execute {
            val jsonData = encoding(value)
            val keys = keys(settings)
            val writeData = if (keys == null) {
                jsonData
            } else {
                createCipher(Cipher.ENCRYPT_MODE, keys.public).doFinal(jsonData)
            }
            file(key).writeBytes(writeData)
        }
    }

    override suspend fun delete(key: String) {
        execute {
            val file = file(key)
            if (file.exists()) {
                file.delete()
            }
        }
    }

    private fun file(key: String): File {
        val directory = File(context.filesDir, "${Storage.STORAGE_FILE_PREFIX}LocalStorage")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        return File(directory, "$key.localstorage")
    }

    private fun keys(settings: LocalStorageSetting): KeyPair? {
        return when (settings) {
            is Unencrypted -> null
            is Encrypted -> settings.keyPair
            is EncryptedUsingKeyStore -> with(androidKeyStore) {
                val privateKey = retrievePrivateKey(ANDROID_KEYSTORE_TAG)
                val publicKey = retrievePublicKey(ANDROID_KEYSTORE_TAG)
                if (privateKey != null && publicKey != null) {
                    KeyPair(publicKey, privateKey)
                } else {
                    createKey(ANDROID_KEYSTORE_TAG).getOrThrow()
                }
            }
        }
    }

    private fun createCipher(mode: Int, key: Key): Cipher =
        Cipher.getInstance(CIPHER_TRANSFORMATION).apply { init(mode, key) }

    private suspend fun <T> execute(block: suspend () -> T) = withContext(ioDispatcher) {
        runCatching { block() }
            .onFailure {
                logger.e(it) { "Error executing local storage operation" }
            }.getOrNull()
    }

    private companion object {
        const val ANDROID_KEYSTORE_TAG = "${AndroidKeyStore.PROVIDER}.LocalStorage"
        const val CIPHER_TRANSFORMATION = "RSA/ECB/PKCS1Padding"
    }
}
