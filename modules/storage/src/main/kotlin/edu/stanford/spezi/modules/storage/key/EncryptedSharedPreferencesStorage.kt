package edu.stanford.spezi.modules.storage.key

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class EncryptedSharedPreferencesStorage @Inject constructor(
    @ApplicationContext private val context: Context,
) : Storage {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences: SharedPreferences =
        EncryptedSharedPreferences.create(
            context,
            "spezi_shared_preferences",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

    @OptIn(ExperimentalStdlibApi::class)
    override suspend fun <T : Any> saveData(key: PreferenceKey<T>, data: T) {
        sharedPreferences.edit(commit = false) {
            when (data) {
                is String -> putString(key.key.name, data)
                is Int -> putInt(key.key.name, data)
                is Boolean -> putBoolean(key.key.name, data)
                is Float -> putFloat(key.key.name, data)
                is Long -> putLong(key.key.name, data)
                is Double -> putString(key.key.name, data.toString())
                is ByteArray -> putString(key.key.name, data.toHexString())
                else -> throw IllegalArgumentException("Unsupported type")
            }
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    override fun <T> readData(key: PreferenceKey<T>): Flow<T?> = flow {
        emit(
            when (key) {
                is PreferenceKey.StringKey -> sharedPreferences.getString(key.key.name, null)
                is PreferenceKey.IntKey -> sharedPreferences.getInt(key.key.name, 0)
                is PreferenceKey.BooleanKey -> sharedPreferences.getBoolean(key.key.name, false)
                is PreferenceKey.FloatKey -> sharedPreferences.getFloat(key.key.name, 0f)
                is PreferenceKey.LongKey -> sharedPreferences.getLong(key.key.name, 0L)
                is PreferenceKey.DoubleKey -> sharedPreferences.getString(key.key.name, null)
                    ?.toDouble()

                is PreferenceKey.ByteArrayKey -> sharedPreferences.getString(key.key.name, null)
                    ?.hexToByteArray()

                else -> {
                    throw IllegalArgumentException("Unsupported type")
                }
            } as T?
        )
    }

    @OptIn(ExperimentalStdlibApi::class)
    override suspend fun <T> readDataBlocking(key: PreferenceKey<T>): T? {
        return when (key) {
            is PreferenceKey.StringKey -> sharedPreferences.getString(key.key.name, null)
            is PreferenceKey.IntKey -> sharedPreferences.getInt(key.key.name, 0)
            is PreferenceKey.BooleanKey -> sharedPreferences.getBoolean(key.key.name, false)
            is PreferenceKey.FloatKey -> sharedPreferences.getFloat(key.key.name, 0f)
            is PreferenceKey.LongKey -> sharedPreferences.getLong(key.key.name, 0L)
            is PreferenceKey.DoubleKey -> sharedPreferences.getString(key.key.name, null)
                ?.toDouble()

            is PreferenceKey.ByteArrayKey -> sharedPreferences.getString(key.key.name, null)
                ?.hexToByteArray()

            else -> {
                throw IllegalArgumentException("Unsupported type")
            }
        } as T?
    }

    override suspend fun <T> deleteData(key: PreferenceKey<T>) {
        sharedPreferences.edit(commit = false) {
            remove(key.key.name)
        }
    }
}


