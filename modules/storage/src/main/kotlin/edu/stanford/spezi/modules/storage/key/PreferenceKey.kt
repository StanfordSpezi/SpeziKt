package edu.stanford.spezi.modules.storage.key

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

sealed class PreferenceKey<T>(val key: Preferences.Key<T>) {
    class IntKey(name: String) : PreferenceKey<Int>(intPreferencesKey(name))
    class DoubleKey(name: String) : PreferenceKey<Double>(doublePreferencesKey(name))
    class StringKey(name: String) : PreferenceKey<String>(stringPreferencesKey(name))
    class BooleanKey(name: String) : PreferenceKey<Boolean>(booleanPreferencesKey(name))
    class FloatKey(name: String) : PreferenceKey<Float>(floatPreferencesKey(name))
    class LongKey(name: String) : PreferenceKey<Long>(longPreferencesKey(name))
    class ByteArrayKey(name: String) : PreferenceKey<ByteArray>(byteArrayPreferencesKey(name))
}
