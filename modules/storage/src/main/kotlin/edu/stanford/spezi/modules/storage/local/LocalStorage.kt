package edu.stanford.spezi.modules.storage.local

import android.content.Context
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.stanford.spezi.core.coroutines.di.Dispatching
import edu.stanford.spezi.modules.storage.secure.SecureStorage
import kotlinx.coroutines.CoroutineDispatcher
import java.io.File
import javax.inject.Inject
import kotlin.reflect.KClass

class LocalStorage @Inject constructor(
    @ApplicationContext val context: Context,
    @Dispatching.IO private val ioDispatcher: CoroutineDispatcher,
    ) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    private val secureStorage = SecureStorage()

    private inline fun <reified C: Any> store(
        // TODO: iOS has this only as a private helper function
        element: C,
        storageKey: String?,
        settings: LocalStorageSetting,
        encode: (C) -> ByteArray
    ): Unit {
        val file = file(storageKey, C::class)

        val alreadyExistedBefore = file.exists()

        // Called at the end of each execution path
        // We can not use defer as the function can potentially throw an error.


        val data = encode(element)

            val keys = settings.keys(secureStorage)

        // Determine if the data should be encrypted or not:
        if (keys == null) {
            file.writeBytes(data)
            setResourceValues(alreadyExistedBefore, settings, file)
            return
        }

        // TODO: Check if encryption is supported
        //
        // iOS:
        // // Encryption enabled:
        // guard SecKeyIsAlgorithmSupported (keys.publicKey, .encrypt, encryptionAlgorithm) else {
        //     throw LocalStorageError.encryptionNotPossible
        // }
        //
        // var encryptError: Unmanaged<CFError>?
        // guard let encryptedData = SecKeyCreateEncryptedData(
        //          keys.publicKey,
        //          encryptionAlgorithm,
        //          data as CFData, & encryptError) as Data? else {
        //      throw LocalStorageError.encryptionNotPossible
        // }

        val encryptedData = data
        file.writeBytes(encryptedData)
        setResourceValues(alreadyExistedBefore, settings, file)
    }

    private inline fun <reified C: Any> read( // TODO: iOS only has this as a private helper
        storageKey: String?,
        settings: LocalStorageSetting,
        decode: (ByteArray) -> C
    ): C {
        val file = file(storageKey, C::class)
        val keys = settings.keys(secureStorage = secureStorage)
            ?: return decode(file.readBytes())

        val privateKey = keys.first
        val publicKey = keys.second

        // TODO: iOS decryption:
        // guard SecKeyIsAlgorithmSupported(keys.privateKey, .decrypt, encryptionAlgorithm) else {
        //      throw LocalStorageError.decryptionNotPossible
        // }

        // var decryptError: Unmanaged<CFError>?
        // guard let decryptedData = SecKeyCreateDecryptedData(keys.privateKey, encryptionAlgorithm, data as CFData, &decryptError) as Data? else {
        //      throw LocalStorageError.decryptionNotPossible
        // }

        return decode(file.readBytes())
    }

    private fun setResourceValues(
        alreadyExistedBefore: Boolean,
        settings: LocalStorageSetting,
        file: File
    ) {
        try {
            if (settings.excludedFromBackupValue) {
                // TODO: Check how to exclude files from backup - may need more flexibility here though
            }
        } catch (error: Throwable) {
            // Revert a written file if it did not exist before.
            if (!alreadyExistedBefore) {
                file.delete()
            }
            throw LocalStorageError.CouldNotExcludedFromBackup
        }
    }

    private inline fun <reified C : Any> file(storageKey: String? = null, type: KClass<C> = C::class): File {
        val fileName = storageKey ?: type.qualifiedName ?: throw Error() // TODO: This should never happen, right?
        val directory = File(context.filesDir, "edu.stanford.spezi/LocalStorage")

        try {
            if (!directory.exists())
                directory.mkdirs()
        } catch (error: Throwable) {
            println("Failed to create directories: $error")
        }

        return File(context.filesDir, "edu.stanford.spezi/LocalStorage/$fileName.localstorage")
    }
}