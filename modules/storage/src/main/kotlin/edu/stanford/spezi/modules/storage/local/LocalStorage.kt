package edu.stanford.spezi.modules.storage.local

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.stanford.spezi.modules.storage.secure.SecureStorage
import java.io.File
import java.security.Key
import javax.crypto.Cipher
import javax.inject.Inject
import kotlin.reflect.KClass

class LocalStorage @Inject constructor(
    @ApplicationContext val context: Context,
) {
    private val secureStorage = SecureStorage(context)

    private fun createCipher(mode: Int, key: Key): Cipher =
        // TODO: Supported values: https://developer.android.com/reference/kotlin/javax/crypto/Cipher
        Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding").apply { init(mode, key) }
g
    fun store(storageKey: String, data: ByteArray, settings: LocalStorageSetting) =
        store(file(storageKey, ByteArray::class), data, settings)

    fun <C : Any> store(
        value: C,
        storageKey: String? = null,
        type: KClass<C>,
        settings: LocalStorageSetting,
        encode: (C) -> ByteArray,
    ) = store(file(storageKey, type), encode(value), settings)

    private fun store(
        file: File,
        data: ByteArray,
        settings: LocalStorageSetting,
    ) {
        val keys = settings.keys(secureStorage) ?: run {
            file.writeBytes(data)
            return
        }
        val encryptedData = createCipher(Cipher.ENCRYPT_MODE, keys.public)
            .doFinal(data)
        file.writeBytes(encryptedData)
    }

    fun <C : Any> read(
        storageKey: String? = null,
        type: KClass<C>,
        settings: LocalStorageSetting,
        decode: (ByteArray) -> C,
    ): C = read(file(storageKey, type), settings, decode)

    fun read(storageKey: String, settings: LocalStorageSetting): ByteArray =
        read(file(storageKey, ByteArray::class), settings) { it }

    private fun <C : Any> read(
        file: File,
        settings: LocalStorageSetting,
        decode: (ByteArray) -> C,
    ): C {
        val keys = settings.keys(secureStorage = secureStorage)
            ?: return decode(file.readBytes())
        val data = createCipher(Cipher.DECRYPT_MODE, keys.private)
            .doFinal(file.readBytes())
        return decode(data)
    }

    fun delete(storageKey: String) = delete(file(storageKey, String::class))

    fun <C : Any> delete(type: KClass<C>) = delete(file(null, type))

    private fun delete(file: File) {
        if (file.exists()) {
            file.delete()
        }
    }

    private fun file(storageKey: String?, type: KClass<*>): File {
        val filename = storageKey
            ?: type.qualifiedName
            ?: type.simpleName
            ?: throw LocalStorageError.FileNameCouldNotBeIdentified
        val directory = File(context.filesDir, "edu.stanford.spezi/LocalStorage")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        return File(context.filesDir, "edu.stanford.spezi/LocalStorage/$filename.localstorage")
    }
}
