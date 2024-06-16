package edu.stanford.spezi.modules.storage.file

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.core.testing.runTestUnconfined
import kotlinx.coroutines.Dispatchers
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EncryptedFileStorageTest {
    private var context: Context = ApplicationProvider.getApplicationContext()
    private var secureFileStorage: SecureFileStorage =
        EncryptedFileStorage(
            context = context,
            ioDispatcher = Dispatchers.Unconfined,
        )

    @Test
    fun `it should save and read file correctly`() = runTestUnconfined {
        // Given
        val fileName = "testFile"
        val data = "Hello, World!".toByteArray()

        // When
        secureFileStorage.saveFile(fileName, data)

        // Then
        val readData = secureFileStorage.readFile(fileName)
        assertThat(readData).isEqualTo(data)
    }

    @Test
    fun `it should return null when reading non-existent file`() = runTestUnconfined {
        // Given
        val fileName = "nonExistentFile"

        // When
        val readData = secureFileStorage.readFile(fileName)

        // Then
        assertThat(readData).isNull()
    }

    @Test
    fun `it should overwrite existing file when saving with same filename`() = runTestUnconfined {
        // Given
        val fileName = "testFile"
        val initialData = "Hello, World!".toByteArray()
        val newData = "New data".toByteArray()

        // When
        secureFileStorage.saveFile(fileName, initialData)
        secureFileStorage.saveFile(fileName, newData)

        // Then
        val readData = secureFileStorage.readFile(fileName)
        assertThat(readData).isEqualTo(newData)
    }

    @Test
    fun `it should delete file correctly`() = runTestUnconfined {
        // Given
        val fileName = "testFile"
        val data = "Hello, World!".toByteArray()
        secureFileStorage.saveFile(fileName, data)

        // When
        secureFileStorage.deleteFile(fileName)

        // Then
        val readData = secureFileStorage.readFile(fileName)
        assertThat(readData).isNull()
    }
}
