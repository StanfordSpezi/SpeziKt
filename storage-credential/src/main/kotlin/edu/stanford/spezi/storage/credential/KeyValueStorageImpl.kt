package edu.stanford.spezi.storage.credential

import android.content.SharedPreferences
import android.util.Base64
import androidx.core.content.edit
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

@Suppress("TooManyFunctions")
class KeyValueStorageImpl @AssistedInject internal constructor(
    @Assisted preferences: Lazy<SharedPreferences>,
) : KeyValueStorage {

    private val sharedPreferences by preferences

    override fun allKeys(): Set<String> = sharedPreferences.all.keys

    override fun getString(key: String): String? = sharedPreferences.getString(key, null)

    override fun getString(key: String, default: String): String =
        sharedPreferences.getString(key, default) ?: default

    override fun putString(key: String, value: String) {
        sharedPreferences.edit { putString(key, value) }
    }

    override fun getBoolean(key: String, default: Boolean): Boolean =
        sharedPreferences.getBoolean(key, default)

    override fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit { putBoolean(key, value) }
    }

    override fun getLong(key: String, default: Long): Long =
        sharedPreferences.getLong(key, default)

    override fun putLong(key: String, value: Long) {
        sharedPreferences.edit { putLong(key, value) }
    }

    override fun getInt(key: String, default: Int): Int = sharedPreferences.getInt(key, default)

    override fun putInt(key: String, value: Int) {
        sharedPreferences.edit { putInt(key, value) }
    }

    override fun getFloat(key: String, default: Float): Float =
        sharedPreferences.getFloat(key, default)

    override fun putFloat(key: String, value: Float) {
        sharedPreferences.edit { putFloat(key, value) }
    }

    override fun getByteArray(key: String): ByteArray? = runCatching {
        val encoded = sharedPreferences.getString(key, null)
        encoded?.let { Base64.decode(it, Base64.DEFAULT) }
    }.getOrNull()

    override fun getByteArray(key: String, default: ByteArray): ByteArray {
        return getByteArray(key) ?: default
    }

    override fun delete(key: String) {
        sharedPreferences.edit { remove(key) }
    }

    override fun clear() {
        sharedPreferences.edit { clear() }
    }

    override fun putByteArray(key: String, value: ByteArray) {
        val encoded = Base64.encodeToString(value, Base64.DEFAULT)
        sharedPreferences.edit { putString(key, encoded) }
    }

    @AssistedFactory
    internal interface Factory {
        fun create(
            preferences: Lazy<SharedPreferences>,
        ): KeyValueStorageImpl
    }
}
