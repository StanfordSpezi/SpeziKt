package edu.stanford.spezi.modules.storage.key

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.core.testing.runTestUnconfined
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.serialization.Serializable
import org.junit.Test

class EncryptedKeyValueStorageTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private var storage: EncryptedKeyValueStorage =
        EncryptedKeyValueStorage("test", context, UnconfinedTestDispatcher())

    private val key = "test_key"

    @Test
    fun `it should save and read string data correctly`() = runTestUnconfined {
        // given
        val expectedValue = "Test String"

        // when
        storage.putString(key, expectedValue)

        // then
        val actualValue = storage.getString(key, "")
        assertThat(actualValue).isEqualTo(expectedValue)
    }

    @Test
    fun `it should return default when reading non-existent string data`() = runTestUnconfined {
        // given
        val default = "default"

        // when
        val actualValue = storage.getString(key, default)

        // then
        assertThat(actualValue).isEqualTo(default)
    }

    @Test
    fun `it should delete string data correctly`() = runTestUnconfined {
        // given
        val value = "Test String"
        storage.putString(key, value)

        // when
        storage.deleteString(key)

        // then
        val actualValue = storage.getString(key, "default")
        assertThat(actualValue).isEqualTo("default")
    }

    @Test
    fun `it should save and read int data correctly`() = runTestUnconfined {
        // given
        val expectedValue = 42

        // when
        storage.putInt(key, expectedValue)

        // then
        val actualValue = storage.getInt(key, 0)
        assertThat(actualValue).isEqualTo(expectedValue)
    }

    @Test
    fun `it should return default when reading non-existent int data`() = runTestUnconfined {
        // given
        val default = 0

        // when
        val actualValue = storage.getInt(key, default)

        // then
        assertThat(actualValue).isEqualTo(default)
    }

    @Test
    fun `it should delete int data correctly`() = runTestUnconfined {
        // given
        val value = 42
        storage.putInt(key, value)

        // when
        storage.deleteInt(key)

        // then
        val actualValue = storage.getInt(key, 0)
        assertThat(actualValue).isEqualTo(0)
    }

    @Test
    fun `it should save and read boolean data correctly`() = runTestUnconfined {
        // given
        val expectedValue = true

        // when
        storage.putBoolean(key, expectedValue)

        // then
        val actualValue = storage.getBoolean(key, false)
        assertThat(actualValue).isEqualTo(expectedValue)
    }

    @Test
    fun `it should return default when reading non-existent boolean data`() = runTestUnconfined {
        // given
        val default = false

        // when
        val actualValue = storage.getBoolean(key, default)

        // then
        assertThat(actualValue).isEqualTo(default)
    }

    @Test
    fun `it should delete boolean data correctly`() = runTestUnconfined {
        // given
        storage.putBoolean(key, true)

        // when
        storage.deleteBoolean(key)

        // then
        val actualValue = storage.getBoolean(key, false)
        assertThat(actualValue).isEqualTo(false)
    }

    @Test
    fun `it should save and read float data correctly`() = runTestUnconfined {
        // given
        val expectedValue = 3.14f

        // when
        storage.putFloat(key, expectedValue)

        // then
        val actualValue = storage.getFloat(key, 0f)
        assertThat(actualValue).isEqualTo(expectedValue)
    }

    @Test
    fun `it should return default when reading non-existent float data`() = runTestUnconfined {
        // given
        val default = 0f

        // when
        val actualValue = storage.getFloat(key, default)

        // then
        assertThat(actualValue).isEqualTo(default)
    }

    @Test
    fun `it should delete float data correctly`() = runTestUnconfined {
        // given
        val value = 3.14f
        storage.putFloat(key, value)

        // when
        storage.deleteFloat(key)

        // then
        val actualValue = storage.getFloat(key, 0f)
        assertThat(actualValue).isEqualTo(0f)
    }

    @Test
    fun `it should save and read long data correctly`() = runTestUnconfined {
        // given
        val expectedValue = 123456789L

        // when
        storage.putLong(key, expectedValue)

        // then
        val actualValue = storage.getLong(key, 0L)
        assertThat(actualValue).isEqualTo(expectedValue)
    }

    @Test
    fun `it should return default when reading non-existent long data`() = runTestUnconfined {
        // given
        val default = 0L

        // when
        val actualValue = storage.getLong(key, default)

        // then
        assertThat(actualValue).isEqualTo(default)
    }

    @Test
    fun `it should delete long data correctly`() = runTestUnconfined {
        // given
        val value = 123456789L
        storage.putLong(key, value)

        // when
        storage.deleteLong(key)

        // then
        val actualValue = storage.getLong(key, 0L)
        assertThat(actualValue).isEqualTo(0L)
    }

    @Test
    fun `it should save and read byte array data correctly`() = runTestUnconfined {
        // given
        val expectedValue = byteArrayOf(0x01, 0x02, 0x03, 0x04)

        // when
        storage.putByteArray(key, expectedValue)

        // then
        val actualValue = storage.getByteArray(key, byteArrayOf())
        assertThat(actualValue).isEqualTo(expectedValue)
    }

    @Test
    fun `it should return default when reading non-existent byte array data`() = runTestUnconfined {
        // given
        val default = byteArrayOf()

        // when
        val actualValue = storage.getByteArray(key, default)

        // then
        assertThat(actualValue).isEqualTo(default)
    }

    @Test
    fun `it should delete byte array data correctly`() = runTestUnconfined {
        // given
        val value = byteArrayOf(0x01, 0x02, 0x03, 0x04)
        storage.putByteArray(key, value)

        // when
        storage.deleteByteArray(key)

        // then
        val actualValue = storage.getByteArray(key, byteArrayOf())
        assertThat(actualValue).isEqualTo(byteArrayOf())
    }

    @Test
    fun `it should handle serializable type correctly`() = runTestUnconfined {
        // given
        val data = Complex()
        storage.putSerializable(key, data)

        // when
        val contains = storage.getSerializable<Complex>(key) == data
        storage.deleteSerializable<Complex>(key)
        val deleted = storage.getSerializable<Complex>(key) == null

        // then
        assertThat(contains).isTrue()
        assertThat(deleted).isTrue()
    }

    @Test
    fun `it should handle serializable list read correctly`() = runTestUnconfined {
        // given
        val data = listOf(Complex())
        storage.putSerializable(key, data)

        // when
        val contains = storage.getSerializableList<Complex>(key) == data
        storage.deleteSerializable<List<Complex>>(key)
        val deleted = storage.getSerializable<List<Complex>>(key) == null

        // then
        assertThat(contains).isTrue()
        assertThat(deleted).isTrue()
    }

    @Test
    fun `it should handle clear correctly`() = runTestUnconfined {
        // given
        val initialValue = 1234
        storage.putInt(key, initialValue)

        // when
        storage.clear()

        // then
        assertThat(storage.getInt(key, -1)).isNotEqualTo(initialValue)
    }

    @Serializable
    data class Complex(val id: Int = 1)
}
