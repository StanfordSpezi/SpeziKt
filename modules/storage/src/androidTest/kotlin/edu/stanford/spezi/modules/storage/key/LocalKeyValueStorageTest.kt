package edu.stanford.spezi.modules.storage.key

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.core.testing.runTestUnconfined
import kotlinx.serialization.Serializable
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocalKeyValueStorageTest {
    private var context: Context = ApplicationProvider.getApplicationContext()
    private var localStorage: LocalKeyValueStorage = LocalKeyValueStorage(context)
    private val key = "local_storage_test_key"

    @Before
    fun before() = runTestUnconfined {
        localStorage.clear()
    }

    @Test
    fun `it should save and read String data correctly`() = runTestUnconfined {
        // given
        val data = "Hello, Leland Stanford!"

        // when
        localStorage.putString(key, data)

        // then
        val readData = localStorage.getString(key, "")
        assertThat(readData).isEqualTo(data)
    }

    @Test
    fun `it should return default String when reading non-existent data`() = runTestUnconfined {
        // given
        val default = "default string"

        // when
        val readData = localStorage.getString(key, default)

        // then
        assertThat(readData).isEqualTo(default)
    }

    @Test
    fun `it should overwrite existing String data`() = runTestUnconfined {
        // given
        val initialData = "Initial String"
        val newData = "Updated String"

        // when
        localStorage.putString(key, initialData)
        localStorage.putString(key, newData)

        // then
        val readData = localStorage.getString(key, "")
        assertThat(readData).isEqualTo(newData)
    }

    @Test
    fun `it should delete String data`() = runTestUnconfined {
        // given
        val data = "Some String"

        // when
        localStorage.putString(key, data)
        localStorage.deleteString(key)

        // then
        val readData = localStorage.getString(key, "")
        assertThat(readData).isEqualTo("")
    }

    @Test
    fun `it should save and read Boolean data correctly`() = runTestUnconfined {
        // given
        val data = true

        // when
        localStorage.putBoolean(key, data)

        // then
        val readData = localStorage.getBoolean(key, false)
        assertThat(readData).isEqualTo(data)
    }

    @Test
    fun `it should return default Boolean when reading non-existent data`() = runTestUnconfined {
        // given
        val default = false

        // when
        val readData = localStorage.getBoolean(key, default)

        // then
        assertThat(readData).isEqualTo(default)
    }

    @Test
    fun `it should overwrite existing Boolean data`() = runTestUnconfined {
        // given
        val initialData = true
        val newData = false

        // when
        localStorage.putBoolean(key, initialData)
        localStorage.putBoolean(key, newData)

        // then
        val readData = localStorage.getBoolean(key, true)
        assertThat(readData).isEqualTo(newData)
    }

    @Test
    fun `it should delete Boolean data`() = runTestUnconfined {
        // given
        val data = true

        // when
        localStorage.putBoolean(key, data)
        localStorage.deleteBoolean(key)

        // then
        val readData = localStorage.getBoolean(key, false)
        assertThat(readData).isEqualTo(false)
    }

    @Test
    fun `it should save and read Long data correctly`() = runTestUnconfined {
        // given
        val data = 12345L

        // when
        localStorage.putLong(key, data)

        // then
        val readData = localStorage.getLong(key, 0L)
        assertThat(readData).isEqualTo(data)
    }

    @Test
    fun `it should return default Long when reading non-existent data`() = runTestUnconfined {
        // given
        val default = 0L

        // when
        val readData = localStorage.getLong(key, default)

        // then
        assertThat(readData).isEqualTo(default)
    }

    @Test
    fun `it should overwrite existing Long data`() = runTestUnconfined {
        // given
        val initialData = 12345L
        val newData = 67890L

        // when
        localStorage.putLong(key, initialData)
        localStorage.putLong(key, newData)

        // then
        val readData = localStorage.getLong(key, 0L)
        assertThat(readData).isEqualTo(newData)
    }

    @Test
    fun `it should delete Long data`() = runTestUnconfined {
        // given
        val data = 12345L

        // when
        localStorage.putLong(key, data)
        localStorage.deleteLong(key)

        // then
        val readData = localStorage.getLong(key, 0L)
        assertThat(readData).isEqualTo(0L)
    }

    @Test
    fun `it should save and read Int data correctly`() = runTestUnconfined {
        // given
        val data = 42

        // when
        localStorage.putInt(key, data)

        // then
        val readData = localStorage.getInt(key, 0)
        assertThat(readData).isEqualTo(data)
    }

    @Test
    fun `it should return default Int when reading non-existent data`() = runTestUnconfined {
        // given
        val default = 0

        // then
        val readData = localStorage.getInt(key, default)
        assertThat(readData).isEqualTo(default)
    }

    @Test
    fun `it should overwrite existing Int data`() = runTestUnconfined {
        // given
        val initialData = 42
        val newData = 100

        // when
        localStorage.putInt(key, initialData)
        localStorage.putInt(key, newData)

        // then
        val readData = localStorage.getInt(key, 0)
        assertThat(readData).isEqualTo(newData)
    }

    @Test
    fun `it should delete Int data`() = runTestUnconfined {
        // given
        val data = 42
        localStorage.putInt(key, data)

        // when
        localStorage.deleteInt(key)

        // then
        val readData = localStorage.getInt(key, 0)
        assertThat(readData).isEqualTo(0)
    }

    @Test
    fun `it should save and read Float data correctly`() = runTestUnconfined {
        // given
        val data = 3.14f

        // when
        localStorage.putFloat(key, data)

        // then
        val readData = localStorage.getFloat(key, 0.0f)
        assertThat(readData).isEqualTo(data)
    }

    @Test
    fun `it should return default Float when reading non-existent data`() = runTestUnconfined {
        // given
        val default = 0.0f

        // when
        val readData = localStorage.getFloat(key, default)

        // then
        assertThat(readData).isEqualTo(default)
    }

    @Test
    fun `it should overwrite existing Float data`() = runTestUnconfined {
        // given
        val initialData = 3.14f
        val newData = 2.71f

        // when
        localStorage.putFloat(key, initialData)
        localStorage.putFloat(key, newData)

        // then
        val readData = localStorage.getFloat(key, 0.0f)
        assertThat(readData).isEqualTo(newData)
    }

    @Test
    fun `it should delete Float data`() = runTestUnconfined {
        // given
        val data = 3.14f

        // when
        localStorage.putFloat(key, data)
        localStorage.deleteFloat(key)

        // then
        val readData = localStorage.getFloat(key, 0.0f)
        assertThat(readData).isEqualTo(0.0f)
    }

    @Test
    fun `it should save and read ByteArray data correctly`() = runTestUnconfined {
        // given
        val data = byteArrayOf(1, 2, 3, 4)

        // when
        localStorage.putByteArray(key, data)

        // then
        val readData = localStorage.getByteArray(key, byteArrayOf())
        assertThat(readData).isEqualTo(data)
    }

    @Test
    fun `it should return default ByteArray when reading non-existent data`() = runTestUnconfined {
        // given
        val default = byteArrayOf(0)

        // when
        val readData = localStorage.getByteArray(key, default)

        // then
        assertThat(readData).isEqualTo(default)
    }

    @Test
    fun `it should overwrite existing ByteArray data`() = runTestUnconfined {
        // given
        val initialData = byteArrayOf(1, 2, 3, 4)
        val newData = byteArrayOf(5, 6, 7, 8)

        // when
        localStorage.putByteArray(key, initialData)
        localStorage.putByteArray(key, newData)

        // then
        val readData = localStorage.getByteArray(key, byteArrayOf())
        assertThat(readData).isEqualTo(newData)
    }

    @Test
    fun `it should delete ByteArray data`() = runTestUnconfined {
        // given
        val data = byteArrayOf(1, 2, 3, 4)

        // when
        localStorage.putByteArray(key, data)
        localStorage.deleteByteArray(key)

        // then
        val readData = localStorage.getByteArray(key, byteArrayOf())
        assertThat(readData).isEqualTo(byteArrayOf())
    }


    @Test
    fun `it should handle serializable type correctly`() = runTestUnconfined {
        // given
        val data = Complex()
        localStorage.putSerializable(key, data)

        // when
        val contains = localStorage.getSerializable<Complex>(key) == data
        localStorage.deleteSerializable<Complex>(key)
        val deleted = localStorage.getSerializable<Complex>(key) == null

        // then
        assertThat(contains).isTrue()
        assertThat(deleted).isTrue()
    }

    @Test
    fun `it should handle serializable list read correctly`() = runTestUnconfined {
        // given
        val data = listOf(Complex())
        localStorage.putSerializable(key, data)

        // when
        val contains = localStorage.getSerializableList<Complex>(key) == data
        localStorage.deleteSerializable<List<Complex>>(key)
        val deleted = localStorage.getSerializable<List<Complex>>(key) == null

        // then
        assertThat(contains).isTrue()
        assertThat(deleted).isTrue()
    }

    @Test
    fun `it should handle clear correctly`() = runTestUnconfined {
        // given
        val initialValue = 1234
        localStorage.putInt(key, initialValue)

        // when
        localStorage.clear()

        // then
        assertThat(localStorage.getInt(key, -1)).isNotEqualTo(initialValue)
    }

    @Serializable
    data class Complex(val id: Int = 1)
}
