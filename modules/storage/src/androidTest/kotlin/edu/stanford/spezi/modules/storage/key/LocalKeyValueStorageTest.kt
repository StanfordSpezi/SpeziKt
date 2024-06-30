package edu.stanford.spezi.modules.storage.key

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.core.testing.runTestUnconfined
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocalKeyValueStorageTest {
    private var context: Context = ApplicationProvider.getApplicationContext()
    private var localStorage: LocalKeyValueStorage = LocalKeyValueStorage(context)

    @Test
    fun `it should save and read data correctly`() = runTestUnconfined {
        // Given
        val key = PreferenceKey.StringKey("testKey")
        val data = "Hello, Leland Stanford!"

        // When
        localStorage.saveData(key, data)

        // Then
        val readData = localStorage.readDataBlocking(key)
        assertThat(readData).isEqualTo(data)
    }

    @Test
    fun `it should return null when reading non-existent data`() = runTestUnconfined {
        // Given
        val key = PreferenceKey.StringKey("nonExistentKey")

        // When
        val readData = localStorage.readDataBlocking(key)

        // Then
        assertThat(readData).isNull()
    }

    @Test
    fun `it should overwrite existing data when saving with same key`() = runTestUnconfined {
        // Given
        val key = PreferenceKey.StringKey("testKey")
        val initialData = "Hello, Leland Stanford!"
        val newData = "New data"

        // When
        localStorage.saveData(key, initialData)
        localStorage.saveData(key, newData)

        // Then
        val readData = localStorage.readDataBlocking(key)
        assertThat(readData).isEqualTo(newData)
    }

    @Test
    fun `it should delete data correctly`() = runTestUnconfined {
        // Given
        val key = PreferenceKey.StringKey("testKey")
        val data = "Hello, Leland Stanford!"

        // When

        localStorage.saveData(key, data)
        localStorage.deleteData(key)

        // Then
        val readData = localStorage.readDataBlocking(key)
        assertThat(readData).isNull()
    }
}
