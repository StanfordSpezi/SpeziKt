package edu.stanford.spezi.modules.storage.key

import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer


suspend inline fun <reified T : Any> KeyValueStorage.getSerializable(key: String): T? =
    when (this) {
        is EncryptedKeyValueStorage, is LocalKeyValueStorage -> {
            val jsonString = getString(key, "")
            runCatching {
                Json.decodeFromString(serializer<T>(), jsonString)
            }.getOrNull()
        }

        is InMemoryKeyValueStorage -> getValue(key) as? T
        else -> null
    }

suspend inline fun <reified T : Any> KeyValueStorage.putSerializable(key: String, value: T) {
    when (this) {
        is EncryptedKeyValueStorage, is LocalKeyValueStorage -> {
            runCatching {
                putString(key = key, Json.encodeToString(value))
            }
        }
        is InMemoryKeyValueStorage -> putValue(key, value)
    }
}

suspend inline fun <reified T : Any> KeyValueStorage.getSerializable(key: String, default: T): T =
    getSerializable(key) ?: default

suspend inline fun <reified T : Any> KeyValueStorage.deleteSerializable(key: String) =
    deleteString(key)

suspend inline fun <reified T : Any> KeyValueStorage.getSerializableList(
    key: String
): List<T> =
    when (this) {
        is EncryptedKeyValueStorage, is LocalKeyValueStorage -> {
            val jsonString = getString(key, "")
            runCatching {
                Json.decodeFromString(ListSerializer(serializer<T>()), jsonString)
            }.getOrNull() ?: emptyList()
        }

        is InMemoryKeyValueStorage -> getSerializable<List<T>>(key, emptyList())
        else -> emptyList()
    }
