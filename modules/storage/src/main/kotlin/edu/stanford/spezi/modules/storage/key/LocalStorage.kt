package edu.stanford.spezi.modules.storage.key

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalStorage @Inject constructor(
    @ApplicationContext context: Context,
) : Storage {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
            name = "spezi_preferences"
        )
    }

    private val dataStore = context.dataStore

    override suspend fun <T : Any> saveData(key: PreferenceKey<T>, data: T) {
        dataStore.edit { preferences ->
            preferences[key.key] = data
        }
    }

    override fun <T> readData(key: PreferenceKey<T>): Flow<T?> {
        return dataStore.data.map { preferences ->
            preferences[key.key]
        }
    }

    override suspend fun <T> readDataBlocking(key: PreferenceKey<T>): T? {
        return dataStore.data.first()[key.key]
    }

    override suspend fun <T> deleteData(key: PreferenceKey<T>) {
        dataStore.edit { preferences ->
            preferences.remove(key.key)
        }
    }
}
