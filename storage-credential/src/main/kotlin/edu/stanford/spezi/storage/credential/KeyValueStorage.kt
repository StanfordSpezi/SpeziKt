package edu.stanford.spezi.storage.credential

import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

@Suppress("TooManyFunctions")
sealed interface KeyValueStorage {

    fun getString(key: String): String?
    fun getString(key: String, default: String): String
    fun putString(key: String, value: String)

    fun getBoolean(key: String, default: Boolean): Boolean
    fun putBoolean(key: String, value: Boolean)

    fun getLong(key: String, default: Long): Long
    fun putLong(key: String, value: Long)

    fun getInt(key: String, default: Int): Int
    fun putInt(key: String, value: Int)

    fun getFloat(key: String, default: Float): Float
    fun putFloat(key: String, value: Float)

    fun getByteArray(key: String): ByteArray?
    fun getByteArray(key: String, default: ByteArray): ByteArray
    fun putByteArray(key: String, value: ByteArray)

    fun allKeys(): Set<String>

    fun delete(key: String)

    fun clear()
}

inline fun <reified T : Any> KeyValueStorage.getSerializable(key: String): T? =
    when (this) {
        is KeyValueStorageImpl -> {
            val jsonString = getString(key, "")
            runCatching {
                Json.decodeFromString(serializer<T>(), jsonString)
            }.getOrNull()
        }

        is InMemoryKeyValueStorage -> getValue(key) as? T
    }

inline fun <reified T : Any> KeyValueStorage.putSerializable(key: String, value: T) {
    when (this) {
        is KeyValueStorageImpl -> {
            runCatching {
                putString(key = key, Json.encodeToString(value))
            }
        }
        is InMemoryKeyValueStorage -> putValue(key, value)
    }
}

inline fun <reified T : Any> KeyValueStorage.getSerializable(key: String, default: T): T =
    getSerializable(key) ?: default

inline fun <reified T : Any> KeyValueStorage.getSerializableList(
    key: String,
): List<T> =
    when (this) {
        is KeyValueStorageImpl -> {
            val jsonString = getString(key, "")
            runCatching {
                Json.decodeFromString(ListSerializer(serializer<T>()), jsonString)
            }.getOrNull() ?: emptyList()
        }

        is InMemoryKeyValueStorage -> getSerializable<List<T>>(key, emptyList())
    }
