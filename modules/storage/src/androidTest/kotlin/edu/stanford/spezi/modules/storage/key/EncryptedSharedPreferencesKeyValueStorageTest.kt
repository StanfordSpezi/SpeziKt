package edu.stanford.spezi.modules.storage.key

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.core.testing.runTestUnconfined
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Test

class EncryptedSharedPreferencesKeyValueStorageTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private var storage: EncryptedSharedPreferencesKeyValueStorage =
        EncryptedSharedPreferencesKeyValueStorage(context)

    @Test
    fun `it should save and read string data correctly`() = runTestUnconfined {
        // Given
        val key = PreferenceKey.StringKey("test_string_key")
        val expectedValue = "Test String"

        // When
        storage.saveData(key, expectedValue)

        // Then
        val actualValue = storage.readDataBlocking(key)
        assertThat(actualValue).isEqualTo(expectedValue)
    }

    @Test
    fun `it should return null when reading non-existent data`() = runTestUnconfined {
        // Given
        val key = PreferenceKey.StringKey("non_existent_key")

        // When
        val actualValue = storage.readDataBlocking(key)

        // Then
        assertThat(actualValue).isNull()
    }

    @Test
    fun `it should overwrite existing data when saving with same key`() = runTestUnconfined {
        // Given
        val key = PreferenceKey.StringKey("test_string_key")
        val initialData = "Initial Data"
        val newData = "New Data"

        // When
        storage.saveData(key, initialData)
        storage.saveData(key, newData)

        // Then
        val actualValue = storage.readDataBlocking(key)
        assertThat(actualValue).isEqualTo(newData)
    }

    @Test
    fun `it should save and read int data correctly`() = runTestUnconfined {
        // Given
        val key = PreferenceKey.IntKey("test_int_key")
        val expectedValue = 42

        // When
        storage.saveData(key, expectedValue)

        // Then
        val actualValue = storage.readDataBlocking(key)
        assertThat(actualValue).isEqualTo(expectedValue)
    }

    @Test
    fun `it should save and read boolean data correctly`() = runTestUnconfined {
        // Given
        val key = PreferenceKey.BooleanKey("test_boolean_key")
        val expectedValue = true

        // When
        storage.saveData(key, expectedValue)

        // Then
        val actualValue = storage.readDataBlocking(key)
        assertThat(actualValue).isEqualTo(expectedValue)
    }

    @Test
    fun `it should save and read float data correctly`() = runTestUnconfined {
        // Given
        val key = PreferenceKey.FloatKey("test_float_key")
        val expectedValue = 3.14f

        // When
        storage.saveData(key, expectedValue)

        // Then
        val actualValue = storage.readDataBlocking(key)
        assertThat(actualValue).isEqualTo(expectedValue)
    }

    @Test
    fun `it should save and read long data correctly`() = runTestUnconfined {
        // Given
        val key = PreferenceKey.LongKey("test_long_key")
        val expectedValue = 123456789L

        // When
        storage.saveData(key, expectedValue)

        // Then
        val actualValue = storage.readDataBlocking(key)
        assertThat(actualValue).isEqualTo(expectedValue)
    }

    @Test
    fun `it should save and read double data correctly`() = runTestUnconfined {
        // Given
        val key = PreferenceKey.DoubleKey("test_double_key")
        val expectedValue = 3.14159

        // When
        storage.saveData(key, expectedValue)

        // Then
        val actualValue = storage.readDataBlocking(key)
        assertThat(actualValue).isEqualTo(expectedValue)
    }

    @Test
    fun `it should save and read byte array data correctly`() = runTestUnconfined {
        // Given
        val key = PreferenceKey.ByteArrayKey("test_byte_array_key")
        val expectedValue = byteArrayOf(0x01, 0x02, 0x03, 0x04)

        // When
        storage.saveData(key, expectedValue)

        // Then
        val actualValue = storage.readDataBlocking(key)
        assertThat(actualValue).isEqualTo(expectedValue)
    }

    @Test
    fun `it should delete data correctly`() = runTestUnconfined {
        // Given
        val key = PreferenceKey.StringKey("test_string_key")
        val value = "Test String"
        storage.saveData(key, value)

        // When
        storage.deleteData(key)

        // Then
        val actualValue = storage.readDataBlocking(key)
        assertThat(actualValue).isNull()
    }

    @Test
    fun `it should read data flow correctly`() = runTestUnconfined {
        // Given
        val key = PreferenceKey.StringKey("test_string_key")
        val expectedValue = "Test String"
        storage.saveData(key, expectedValue)

        // When
        val actualValue = runBlocking { storage.readData(key).first() }

        // Then
        assertThat(actualValue).isEqualTo(expectedValue)
    }
}
