package edu.stanford.spezi.spezi.credentialstorage.key

import com.google.common.truth.Truth.assertThat
import kotlinx.serialization.Serializable
import org.junit.Test

class InMemoryStorageTest {

    private val storage = InMemoryKeyValueStorage()
    private val key = "local_storage_test_key"

    @Test
    fun `it should save and read string data correctly`() {
        // given
        val expectedValue = "Test String"

        // when
        storage.putString(key, expectedValue)

        // then
        val actualValue = storage.getString(key, "")
        assertThat(actualValue).isEqualTo(expectedValue)
    }

    @Test
    fun `it should return default when reading non-existent string data`() {
        // given
        val default = "default"

        // when
        val actualValue = storage.getString(key, default)

        // then
        assertThat(actualValue).isEqualTo(default)
    }

    @Test
    fun `it should delete string data correctly`() {
        // given
        val value = "Test String"
        storage.putString(key, value)

        // when
        storage.delete(key)

        // then
        val actualValue = storage.getString(key, "default")
        assertThat(actualValue).isEqualTo("default")
    }

    @Test
    fun `it should save and read int data correctly`() {
        // given
        val expectedValue = 42

        // when
        storage.putInt(key, expectedValue)

        // then
        val actualValue = storage.getInt(key, 0)
        assertThat(actualValue).isEqualTo(expectedValue)
    }

    @Test
    fun `it should return default when reading non-existent int data`() {
        // given
        val default = 0

        // when
        val actualValue = storage.getInt(key, default)

        // then
        assertThat(actualValue).isEqualTo(default)
    }

    @Test
    fun `it should delete int data correctly`() {
        // given
        val value = 42
        storage.putInt(key, value)

        // when
        storage.delete(key)

        // then
        val actualValue = storage.getInt(key, 0)
        assertThat(actualValue).isEqualTo(0)
    }

    @Test
    fun `it should save and read boolean data correctly`() {
        // given
        val expectedValue = true

        // when
        storage.putBoolean(key, expectedValue)

        // then
        val actualValue = storage.getBoolean(key, false)
        assertThat(actualValue).isEqualTo(expectedValue)
    }

    @Test
    fun `it should return default when reading non-existent boolean data`() {
        // given
        val default = false

        // when
        val actualValue = storage.getBoolean(key, default)

        // then
        assertThat(actualValue).isEqualTo(default)
    }

    @Test
    fun `it should delete boolean data correctly`() {
        // given
        storage.putBoolean(key, true)

        // when
        storage.delete(key)

        // then
        val actualValue = storage.getBoolean(key, false)
        assertThat(actualValue).isEqualTo(false)
    }

    @Test
    fun `it should save and read float data correctly`() {
        // given
        val expectedValue = 3.14f

        // when
        storage.putFloat(key, expectedValue)

        // then
        val actualValue = storage.getFloat(key, 0f)
        assertThat(actualValue).isEqualTo(expectedValue)
    }

    @Test
    fun `it should return default when reading non-existent float data`() {
        // given
        val default = 0f

        // when
        val actualValue = storage.getFloat(key, default)

        // then
        assertThat(actualValue).isEqualTo(default)
    }

    @Test
    fun `it should delete float data correctly`() {
        // given
        val value = 3.14f
        storage.putFloat(key, value)

        // when
        storage.delete(key)

        // then
        val actualValue = storage.getFloat(key, 0f)
        assertThat(actualValue).isEqualTo(0f)
    }

    @Test
    fun `it should save and read long data correctly`() {
        // given
        val expectedValue = 123456789L

        // when
        storage.putLong(key, expectedValue)

        // then
        val actualValue = storage.getLong(key, 0L)
        assertThat(actualValue).isEqualTo(expectedValue)
    }

    @Test
    fun `it should return default when reading non-existent long data`() {
        // given
        val default = 0L

        // when
        val actualValue = storage.getLong(key, default)

        // then
        assertThat(actualValue).isEqualTo(default)
    }

    @Test
    fun `it should delete long data correctly`() {
        // given
        val value = 123456789L
        storage.putLong(key, value)

        // when
        storage.delete(key)

        // then
        val actualValue = storage.getLong(key, 0L)
        assertThat(actualValue).isEqualTo(0L)
    }

    @Test
    fun `it should save and read byte array data correctly`() {
        // given
        val expectedValue = byteArrayOf(0x01, 0x02, 0x03, 0x04)

        // when
        storage.putByteArray(key, expectedValue)

        // then
        val actualValue = storage.getByteArray(key, byteArrayOf())
        assertThat(actualValue).isEqualTo(expectedValue)
    }

    @Test
    fun `it should return default when reading non-existent byte array data`() {
        // given
        val default = byteArrayOf()

        // when
        val actualValue = storage.getByteArray(key, default)

        // then
        assertThat(actualValue).isEqualTo(default)
    }

    @Test
    fun `it should return null when reading non-existent byte array data`() {
        // given
        storage.clear()

        // when
        val actualValue = storage.getByteArray(key)

        // then
        assertThat(actualValue).isNull()
    }

    @Test
    fun `it should delete byte array data correctly`() {
        // given
        val value = byteArrayOf(0x01, 0x02, 0x03, 0x04)
        storage.putByteArray(key, value)

        // when
        storage.delete(key)

        // then
        val actualValue = storage.getByteArray(key, byteArrayOf())
        assertThat(actualValue).isEqualTo(byteArrayOf())
    }

    @Test
    fun `it should handle serializable type correctly`() {
        // given
        val data = Complex()
        storage.putSerializable(key, data)

        // when
        val contains = storage.getSerializable<Complex>(key) == data
        storage.delete(key)
        val deleted = storage.getSerializable<Complex>(key) == null

        // then
        assertThat(contains).isTrue()
        assertThat(deleted).isTrue()
    }

    @Test
    fun `it should handle serializable list read correctly`() {
        // given
        val data = listOf(Complex())
        storage.putSerializable(key, data)

        // when
        val contains = storage.getSerializableList<Complex>(key) == data
        storage.delete(key)
        val deleted = storage.getSerializable<List<Complex>>(key) == null

        // then
        assertThat(contains).isTrue()
        assertThat(deleted).isTrue()
    }

    @Test
    fun `it should handle clear correctly`() {
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
