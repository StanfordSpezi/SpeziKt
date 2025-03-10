package edu.stanford.spezi.storage.credential

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
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
        return lazy {
            when (type) {
                KeyValueStorageType.UNENCRYPTED -> context.getSharedPreferences(
                    fileName,
                    Context.MODE_PRIVATE
                )

                KeyValueStorageType.ENCRYPTED -> {
                    EncryptedSharedPreferences.create(
                        context,
                        fileName,
                        masterKey,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                    )
                }
            }
        }
    }
}
