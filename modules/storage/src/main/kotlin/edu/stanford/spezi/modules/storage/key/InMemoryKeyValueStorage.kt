package edu.stanford.spezi.modules.storage.key

import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

@Suppress("TooManyFunctions")
class InMemoryKeyValueStorage @Inject constructor() : KeyValueStorage {
    private val storage = ConcurrentHashMap<String, Any?>()

    override suspend fun getString(key: String, default: String): String {
        return getValue(key) as? String ?: default
    }

    override suspend fun putString(key: String, value: String) {
        putValue(key, value)
    }

    override suspend fun deleteString(key: String) {
        remove(key)
    }

    override suspend fun getBoolean(key: String, default: Boolean): Boolean {
        return getValue(key) as? Boolean ?: default
    }

    override suspend fun putBoolean(key: String, value: Boolean) {
        putValue(key, value)
    }

    override suspend fun deleteBoolean(key: String) {
        remove(key)
    }

    override suspend fun getLong(key: String, default: Long): Long {
        return getValue(key) as? Long ?: default
    }

    override suspend fun putLong(key: String, value: Long) {
        putValue(key, value)
    }

    override suspend fun deleteLong(key: String) {
        remove(key)
    }

    override suspend fun getInt(key: String, default: Int): Int {
        return getValue(key) as? Int ?: default
    }

    override suspend fun putInt(key: String, value: Int) {
        putValue(key, value)
    }

    override suspend fun deleteInt(key: String) {
        remove(key)
    }

    override suspend fun getFloat(key: String, default: Float): Float {
        return getValue(key) as? Float ?: default
    }

    override suspend fun putFloat(key: String, value: Float) {
        putValue(key, value)
    }

    override suspend fun deleteFloat(key: String) {
        remove(key)
    }

    override suspend fun getByteArray(key: String, default: ByteArray): ByteArray {
        return getValue(key) as? ByteArray ?: default
    }

    override suspend fun putByteArray(key: String, value: ByteArray) {
        putValue(key, value)
    }

    override suspend fun deleteByteArray(key: String) {
        remove(key)
    }

    override suspend fun clear() {
        storage.clear()
    }

    fun putValue(key: String, value: Any?) {
        storage[key] = value
    }

    fun getValue(key: String): Any? = storage[key]

    private fun remove(key: String) {
        storage.remove(key)
    }
}
