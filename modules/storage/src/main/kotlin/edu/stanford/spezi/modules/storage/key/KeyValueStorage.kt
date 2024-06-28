package edu.stanford.spezi.modules.storage.key

import kotlinx.coroutines.flow.Flow

interface KeyValueStorage {
    suspend fun <T : Any> saveData(key: PreferenceKey<T>, data: T)
    fun <T> readData(key: PreferenceKey<T>): Flow<T?>
    suspend fun <T> readDataBlocking(key: PreferenceKey<T>): T?
    suspend fun <T> deleteData(key: PreferenceKey<T>)
}
