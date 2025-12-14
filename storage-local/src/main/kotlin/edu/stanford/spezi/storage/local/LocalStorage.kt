package edu.stanford.spezi.storage.local

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.stanford.spezi.core.DefaultInitializer
import edu.stanford.spezi.core.Module
import edu.stanford.spezi.core.coroutines.Dispatching
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.storage.local.LocalStorageSetting.Encrypted
import edu.stanford.spezi.storage.local.LocalStorageSetting.EncryptedUsingKeyStore
import edu.stanford.spezi.storage.local.LocalStorageSetting.Unencrypted
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
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

interface LocalStorage : Module {

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

    suspend fun delete(key: String)

    companion object : DefaultInitializer<LocalStorage> {
        override fun create(context: Context): LocalStorage {
            return LocalStorageImpl(
                context = context,
                ioDispatcher = Dispatchers.IO,
                keyStorage = KeyStorageImpl(),
            )
        }
    }
}

internal class LocalStorageImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @Dispatching.IO private val ioDispatcher: CoroutineDispatcher,
    private val keyStorage: KeyStorage,
) : LocalStorage {

    private val logger by speziLogger()

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
                getInitializedCipher(Cipher.ENCRYPT_MODE, keys.public).doFinal(jsonData)
            }
            file(key).writeBytes(writeData)
        }
    }

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
            getInitializedCipher(Cipher.DECRYPT_MODE, keys.private).doFinal(bytes)
        }
        decoding(data)
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
        val directory = File(context.filesDir, "${STORAGE_FILE_PREFIX}LocalStorage")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        return File(directory, "$key.localstorage")
    }

    private fun keys(settings: LocalStorageSetting): KeyPair? {
        return when (settings) {
            is Unencrypted -> null
            is Encrypted -> settings.keyPair
            is EncryptedUsingKeyStore -> with(keyStorage) {
                retrieveKeyPair(ANDROID_KEYSTORE_TAG)
                    ?: create(ANDROID_KEYSTORE_TAG).getOrThrow()
            }
        }
    }

    private fun getInitializedCipher(mode: Int, key: Key): Cipher =
        Cipher.getInstance(KeyStorage.CIPHER_TRANSFORMATION).apply { init(mode, key) }

    private suspend fun <T> execute(block: suspend () -> T) = withContext(ioDispatcher) {
        runCatching { block() }
            .onFailure {
                logger.e(it) { "Error executing local storage operation" }
            }.getOrNull()
    }

    private companion object {
        const val ANDROID_KEYSTORE_TAG = "LocalStorageTag"
        const val STORAGE_FILE_PREFIX = "edu.stanford.spezi.storage."
    }
}
