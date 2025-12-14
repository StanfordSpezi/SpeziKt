package edu.stanford.spezi.storage.credential

import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

@Suppress("TooManyFunctions")
class InMemoryKeyValueStorage @Inject constructor() : KeyValueStorage {
    private val storage = ConcurrentHashMap<String, Any?>()

    override fun getString(key: String): String? {
        return getValue(key) as? String
    }

    override fun getString(key: String, default: String): String {
        return getValue(key) as? String ?: default
    }

    override fun putString(key: String, value: String) {
        putValue(key, value)
    }

    override fun getBoolean(key: String, default: Boolean): Boolean {
        return getValue(key) as? Boolean ?: default
    }

    override fun putBoolean(key: String, value: Boolean) {
        putValue(key, value)
    }

    override fun getLong(key: String, default: Long): Long {
        return getValue(key) as? Long ?: default
    }

    override fun putLong(key: String, value: Long) {
        putValue(key, value)
    }

    override fun getInt(key: String, default: Int): Int {
        return getValue(key) as? Int ?: default
    }

    override fun putInt(key: String, value: Int) {
        putValue(key, value)
    }

    override fun getFloat(key: String, default: Float): Float {
        return getValue(key) as? Float ?: default
    }

    override fun putFloat(key: String, value: Float) {
        putValue(key, value)
    }

    override fun getByteArray(key: String): ByteArray? {
        return getValue(key) as? ByteArray
    }

    override fun getByteArray(key: String, default: ByteArray): ByteArray {
        return getByteArray(key) ?: default
    }

    override fun putByteArray(key: String, value: ByteArray) {
        putValue(key, value)
    }

    override fun allKeys(): Set<String> {
        return storage.keys
    }

    override fun delete(key: String) {
        storage.remove(key)
    }

    override fun clear() {
        storage.clear()
    }

    fun putValue(key: String, value: Any?) {
        storage[key] = value
    }

    fun getValue(key: String): Any? = storage[key]
}
