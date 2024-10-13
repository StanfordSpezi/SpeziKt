package edu.stanford.spezi.modules.storage.key

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

@Suppress("TooManyFunctions")
class LocalKeyValueStorage @Inject constructor(
    @ApplicationContext private val context: Context,
) : KeyValueStorage {
    companion object {
        const val FILE_NAME = "spezi_preferences"
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
            name = FILE_NAME
        )
    }

    private val dataStore = context.dataStore

    override suspend fun getString(key: String, default: String): String {
        return getData(stringPreferencesKey(key)) ?: default
    }

    override suspend fun putString(key: String, value: String) {
        saveData(stringPreferencesKey(key), value)
    }

    override suspend fun deleteString(key: String) {
        deleteData(stringPreferencesKey(key))
    }

    override suspend fun getBoolean(key: String, default: Boolean): Boolean {
        return getData(booleanPreferencesKey(key)) ?: default
    }

    override suspend fun putBoolean(key: String, value: Boolean) {
        saveData(booleanPreferencesKey(key), value)
    }

    override suspend fun deleteBoolean(key: String) {
        deleteData(booleanPreferencesKey(key))
    }

    override suspend fun getLong(key: String, default: Long): Long {
        return getData(longPreferencesKey(key)) ?: default
    }

    override suspend fun putLong(key: String, value: Long) {
        saveData(longPreferencesKey(key), value)
    }

    override suspend fun deleteLong(key: String) {
        deleteData(longPreferencesKey(key))
    }

    override suspend fun getInt(key: String, default: Int): Int {
        return getData(intPreferencesKey(key)) ?: default
    }

    override suspend fun putInt(key: String, value: Int) {
        saveData(intPreferencesKey(key), value)
    }

    override suspend fun deleteInt(key: String) {
        deleteData(intPreferencesKey(key))
    }

    override suspend fun getFloat(key: String, default: Float): Float {
        return getData(floatPreferencesKey(key)) ?: default
    }

    override suspend fun putFloat(key: String, value: Float) {
        saveData(floatPreferencesKey(key), value)
    }

    override suspend fun deleteFloat(key: String) {
        deleteData(floatPreferencesKey(key))
    }

    override suspend fun getByteArray(key: String, default: ByteArray): ByteArray {
        return getData(byteArrayPreferencesKey(key)) ?: default
    }

    override suspend fun putByteArray(key: String, value: ByteArray) {
        saveData(byteArrayPreferencesKey(key), value)
    }

    override suspend fun deleteByteArray(key: String) {
        deleteData(byteArrayPreferencesKey(key))
    }

    override suspend fun clear() {
        context.dataStore.edit { preferences -> preferences.clear() }
    }

    private suspend fun <T : Any> saveData(key: Preferences.Key<T>, data: T) {
        dataStore.edit { preferences ->
            preferences[key] = data
        }
    }

    private suspend fun <T> getData(key: Preferences.Key<T>): T? {
        return runCatching {
            dataStore.data
                .catch { emit(emptyPreferences()) }
                .firstOrNull()?.get(key)
        }.getOrNull()
    }

    private suspend fun <T> deleteData(key: Preferences.Key<T>) {
        dataStore.edit { preferences ->
            preferences.remove(key)
        }
    }
}
