package edu.stanford.spezi.modules.storage.key

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.stanford.spezi.core.logging.speziLogger
import javax.inject.Inject
import javax.inject.Singleton

interface KeyValueStorageFactory {
    fun create(
        fileName: String,
        type: KeyValueStorageType,
    ): KeyValueStorage
}

@Singleton
internal class KeyValueStorageFactoryImpl @Inject constructor(
    private val storageFactory: KeyValueStorageImpl.Factory,
    @ApplicationContext private val context: Context,
) : KeyValueStorageFactory {
    private val logger by speziLogger()

    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    override fun create(fileName: String, type: KeyValueStorageType): KeyValueStorage {
        val preferences = createSharedPreferences(fileName = fileName, type = type)
        return storageFactory.create(preferences)
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
