package edu.stanford.spezi.modules.storage.key

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.stanford.spezi.core.coroutines.di.Dispatching
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

@Suppress("TooManyFunctions")
class EncryptedSharedPreferencesKeyValueStorage @Inject constructor(
    @ApplicationContext private val context: Context,
    @Dispatching.IO private val ioDispatcher: CoroutineDispatcher,
) : KeyValueStorage {
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

    override suspend fun getString(key: String, default: String): String {
        return execute { sharedPreferences.getString(key, default) ?: default }
    }

    override suspend fun putString(key: String, value: String) {
        execute { sharedPreferences.edit { putString(key, value) } }
    }

    override suspend fun deleteString(key: String) {
        execute { sharedPreferences.edit { remove(key) } }
    }

    override suspend fun getBoolean(key: String, default: Boolean): Boolean {
        return execute { sharedPreferences.getBoolean(key, default) }
    }

    override suspend fun putBoolean(key: String, value: Boolean) {
        execute { sharedPreferences.edit { putBoolean(key, value) } }
    }

    override suspend fun deleteBoolean(key: String) {
        execute { sharedPreferences.edit { remove(key) } }
    }

    override suspend fun getLong(key: String, default: Long): Long {
        return execute { sharedPreferences.getLong(key, default) }
    }

    override suspend fun putLong(key: String, value: Long) {
        execute { sharedPreferences.edit { putLong(key, value) } }
    }

    override suspend fun deleteLong(key: String) {
        execute { sharedPreferences.edit { remove(key) } }
    }

    override suspend fun getInt(key: String, default: Int): Int {
        return execute { sharedPreferences.getInt(key, default) }
    }

    override suspend fun putInt(key: String, value: Int) {
        execute { sharedPreferences.edit { putInt(key, value) } }
    }

    override suspend fun deleteInt(key: String) {
        execute { sharedPreferences.edit { remove(key) } }
    }

    override suspend fun getFloat(key: String, default: Float): Float {
        return execute { sharedPreferences.getFloat(key, default) }
    }

    override suspend fun putFloat(key: String, value: Float) {
        execute { sharedPreferences.edit { putFloat(key, value) } }
    }

    override suspend fun deleteFloat(key: String) {
        execute { sharedPreferences.edit { remove(key) } }
    }

    override suspend fun getByteArray(key: String, default: ByteArray): ByteArray {
        return execute {
            val encoded = sharedPreferences.getString(key, null)
            encoded?.let { Base64.decode(it, Base64.DEFAULT) } ?: default
        }
    }

    override suspend fun clear() {
        sharedPreferences.edit { clear() }
    }

    override suspend fun putByteArray(key: String, value: ByteArray) {
        execute {
            val encoded = Base64.encodeToString(value, Base64.DEFAULT)
            sharedPreferences.edit { putString(key, encoded) }
        }
    }

    override suspend fun deleteByteArray(key: String) {
        execute { sharedPreferences.edit { remove(key) } }
    }

    private suspend fun <T> execute(block: suspend () -> T) = withContext(ioDispatcher) { block() }
}
