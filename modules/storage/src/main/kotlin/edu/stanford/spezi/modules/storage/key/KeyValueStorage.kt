package edu.stanford.spezi.modules.storage.key

@Suppress("TooManyFunctions")
interface KeyValueStorage {

    suspend fun getString(key: String, default: String): String
    suspend fun putString(key: String, value: String)
    suspend fun deleteString(key: String)

    suspend fun getBoolean(key: String, default: Boolean): Boolean
    suspend fun putBoolean(key: String, value: Boolean)
    suspend fun deleteBoolean(key: String)

    suspend fun getLong(key: String, default: Long): Long
    suspend fun putLong(key: String, value: Long)
    suspend fun deleteLong(key: String)

    suspend fun getInt(key: String, default: Int): Int
    suspend fun putInt(key: String, value: Int)
    suspend fun deleteInt(key: String)

    suspend fun getFloat(key: String, default: Float): Float
    suspend fun putFloat(key: String, value: Float)
    suspend fun deleteFloat(key: String)

    suspend fun getByteArray(key: String, default: ByteArray): ByteArray
    suspend fun putByteArray(key: String, value: ByteArray)
    suspend fun deleteByteArray(key: String)

    suspend fun clear()
}
