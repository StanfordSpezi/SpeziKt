package edu.stanford.spezi.storage.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import edu.stanford.spezi.core.DefaultInitializer
import edu.stanford.spezi.core.Module
import edu.stanford.spezi.core.logging.speziLogger

interface KeyValueStorageFactory : Module {
    fun create(
        fileName: String,
        type: KeyValueStorageType,
    ): KeyValueStorage

    companion object : DefaultInitializer<KeyValueStorageFactory> {
        override fun create(context: Context): KeyValueStorageFactory {
            return KeyValueStorageFactoryImpl(context)
        }
    }
}

internal class KeyValueStorageFactoryImpl(
    private val context: Context,
) : KeyValueStorageFactory {
    private val logger by speziLogger()

    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    override fun create(fileName: String, type: KeyValueStorageType): KeyValueStorage {
        val preferences = createSharedPreferences(fileName = fileName, type = type)
        return KeyValueStorageImpl(preferences)
    }

    private fun createSharedPreferences(
        fileName: String,
        type: KeyValueStorageType,
    ): Lazy<SharedPreferences> {
        return lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            when (type) {
                KeyValueStorageType.UNENCRYPTED -> createUnencryptedStorage(fileName = fileName)

                KeyValueStorageType.ENCRYPTED -> createEncryptedStorage(fileName = fileName).getOrNull() ?: run {
                    logger.w { "First encrypted storage creation failed, deleting existing file and retrying..." }
                    context.deleteSharedPreferences(fileName)
                    createEncryptedStorage(fileName = fileName).getOrThrow()
                }
            }
        }
    }

    private fun createUnencryptedStorage(fileName: String) = context.getSharedPreferences(
        fileName,
        Context.MODE_PRIVATE
    )

    private fun createEncryptedStorage(fileName: String) = runCatching {
        EncryptedSharedPreferences.create(
            context,
            fileName,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }.onSuccess {
        logger.i { "Successfully created encrypted storage $fileName" }
    }
}
